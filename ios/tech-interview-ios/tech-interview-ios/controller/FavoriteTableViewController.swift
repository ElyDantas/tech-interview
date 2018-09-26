
//
//  FavoriteTableViewController.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import UIKit
import PureLayout
import Material

class FavoriteTableViewController: UITableViewController {

    let deviceOrietation = UIApplication.shared.statusBarOrientation
    fileprivate var menuButton: IconButton!
    weak var articleDelegate: ArticleDelegate?
    var appDelegate: AppDelegate!
    var articles = [Article]()
    var didAppear = false

    override func viewDidLoad() {
        super.viewDidLoad()
        appDelegate = UIApplication.shared.delegate as? AppDelegate
        prepareNavigationItem()
    }

    override func viewWillAppear(_ animated: Bool) {
        if didAppear{
            // Reload favorites in case dataset size changed
            let articles = Joyjet.getFavorites(from: appDelegate.feedController.categories)
            if articles.count != self.articles.count{
                self.articles = articles
                tableView.reloadData()
            }
        }else {
            didAppear = true
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

extension FavoriteTableViewController{

    @IBAction func didMenuTapped(_ sender: UIBarButtonItem) {
        navigationDrawerController?.openRightView()
    }

    fileprivate func prepareNavigationItem() {
        menuButton = IconButton(image: ImageSource.menu)
        menuButton.addTarget(self, action: #selector(didMenuTapped(_:)), for: .touchUpInside)
        let navigationBarTitle = UILabel(forAutoLayout: ())
        navigationBarTitle.text = "Favorites"
        navigationBarTitle.font = UIFont(name: "Montserrat-Light", size: 18)
        navigationBarTitle.textColor = UIColor(hex: "#5DA4E5")
        navigationItem.rightViews = [menuButton]
        navigationItem.centerViews = [navigationBarTitle]
        navigationBarTitle.autoCenterInSuperview()
        navigationController?.isNavigationBarHidden = false
    }
}

// Tableview Datasource
extension FavoriteTableViewController{

    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return articles.count
    }
}

// Tableview Delegate
extension FavoriteTableViewController{

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 233
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = (tableView.dequeueReusableCell(withIdentifier: "articleCell", for: indexPath) as? ArticleTableViewCell) else{ return UITableViewCell()}

        let article = articles[indexPath.row]
        article.indexPath = indexPath
        cell.article = article
        cell.titleLabel.text = article.title?.uppercased()
        cell.descriptionLabel.text = article.description
        cell.articleDelegate = self
        cell.prepareButtons()
        cell.prepareCarousel()
        return cell
    }
}

extension FavoriteTableViewController: ArticleDelegate{

    func close(article: Article){}

    func open(article: Article, withImage image: UIImage?){
        if let position = article.indexPath{
            let article = articles[position.row]
            if let index = article.indexPath{
                let articleController = prepare(viewController: "Article") as! ArticleViewController
                if let img = image{ articleController.image = img}
                articleController.category = appDelegate.feedController.getCategory(index.section)
                articleController.article = article
                articleController.articleDelegate = self
                navigationController?.pushViewController(articleController, animated: true)
            }
        }
    }
}
