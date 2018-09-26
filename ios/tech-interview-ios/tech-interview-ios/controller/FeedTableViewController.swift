//
//  HomeTableViewController.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import UIKit
import PureLayout
import Material
import Alamofire
import AlamofireImage
import ImageSlideshow

protocol ArticleDelegate: class{
    func open(article: Article, withImage: UIImage?)
    func close(article: Article)
}

class FeedTableViewController: UITableViewController{

    fileprivate var menuButton: IconButton!
    var appDelegate: AppDelegate!
    var categories = [Category]()
    weak var articleDelegate: ArticleDelegate?

    override func viewDidLoad() {
        super.viewDidLoad()
        appDelegate = UIApplication.shared.delegate as? AppDelegate
        prepareFeed()
        prepareNavigationBar()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

extension FeedTableViewController{

    fileprivate func closeNavigationDrawer(result: Bool) {
        navigationDrawerController?.closeRightView()
    }

    @IBAction func didMenuTapped(_ sender: UIBarButtonItem) {
        navigationDrawerController?.openRightView()
    }

    func prepareFeed(){
        Joyjet.fetchCategories {
            $0.forEach({ (Category) in
                Category.articles.forEach({ (Article) in
                    Article.category = Category.category
                })
            })
            self.categories = $0
            self.tableView.reloadData()
        }
    }

    func didChange(article: Article){
        if let indexPath = article.indexPath{
            categories[indexPath.section].articles[indexPath.row] = article
            tableView.reloadRows(at: [indexPath], with: .none)
        }
    }

    func getCategory(_ index: Int) -> String{
        return categories[index].category ?? ""
    }

    func openFavorites(){
        let favorites = prepare(viewController: "Favorites") as! FavoriteTableViewController
        favorites.articles = Joyjet.getFavorites(from: categories)
        navigationDrawerController?.transition(
            to: AppNavigationController(rootViewController: favorites),
            completion: closeNavigationDrawer
        )
    }

    fileprivate func prepareNavigationBar() {
        menuButton = IconButton(image: ImageSource.menu)
        menuButton.addTarget(self, action: #selector(didMenuTapped(_:)), for: .touchUpInside)
        let navigationBarTitle = UILabel(forAutoLayout: ())
        navigationBarTitle.text = "Digital Space"
        navigationBarTitle.font = UIFont(name: "Montserrat-Light", size: 18)
        navigationBarTitle.textColor = UIColor(hex: "#5DA4E5")
        navigationItem.rightViews = [menuButton]
        navigationItem.centerViews = [navigationBarTitle]
        navigationBarTitle.autoCenterInSuperview()
        navigationController?.isNavigationBarHidden = false
    }

    func prepare(header: UIView, inSection section: Int) -> UIView{
        header.backgroundColor = UIColor(hex: "#4D92DF")
        let headerLabel = UILabel(frame: CGRect(x: 0, y: 0, width:
            Int(tableView.bounds.size.width), height: Int(40.0)))
        headerLabel.font = UIFont(name: "Montserrat-Regular", size: 16)
        headerLabel.adjustsFontSizeToFitWidth = true
        headerLabel.textColor = UIColor.white
        headerLabel.text = categories[section].category
        headerLabel.sizeToFit()
        header.addSubview(headerLabel)
        headerLabel.autoAlignAxis(toSuperviewMarginAxis: .horizontal)
        headerLabel.autoPinEdge(toSuperviewEdge: .leading, withInset: 30.0)
        return header
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "openArticle" {
            if let articleViewController = (segue.destination as? ArticleViewController){
                let dictionary = sender as! [String: Any]
                for (key, value) in dictionary{
                    if key == "article"{ articleViewController.article = value as! Article}
                    if key == "category"{ articleViewController.category = value as? String}
                    if key == "image"{ articleViewController.image = value as? UIImage}
                }
            }
        }
    }
}

// Tableview Datasource
extension FeedTableViewController{

    override func numberOfSections(in tableView: UITableView) -> Int {
        return categories.count
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        for (index, category) in categories.enumerated(){
            if index == section{
                return category.articles.count
            }
        }
        return 0
    }
}

// Tableview Delegate
extension FeedTableViewController{

    override func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView(frame: CGRect(x: 00, y: 0, width: tableView.bounds.size.width, height: 40.0))
        return prepare(header: headerView, inSection: section)
    }

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 40.0
    }

    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 233.0
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        guard let cell = (tableView.dequeueReusableCell(withIdentifier: "articleCell", for: indexPath) as? ArticleTableViewCell) else{ return UITableViewCell()}

        let article = categories[indexPath.section].articles[indexPath.row]
        article.indexPath = indexPath
        cell.article = article
        cell.articleDelegate = self
        cell.titleLabel.text = article.title?.uppercased()
        cell.descriptionLabel.text = article.description
        cell.prepareButtons()
        cell.prepareCarousel()
        return cell
    }
}

extension FeedTableViewController: ArticleDelegate{
    func close(article: Article) {}
    func open(article: Article, withImage image: UIImage?){
        if let index = article.indexPath{
            let articleController = prepare(viewController: "Article") as! ArticleViewController
            if let img = image{ articleController.image = img}
            articleController.category = categories[index.section].category
            articleController.article = article
            navigationController?.pushViewController(articleController, animated: true)
        }
    }
}

