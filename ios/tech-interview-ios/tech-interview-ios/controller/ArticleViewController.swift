//
//  ArticleViewController.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import UIKit
import MXParallaxHeader
import Material

class ArticleViewController: UIViewController{

    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var categoryLabel: UILabel!
    @IBOutlet weak var starButton: UIButton!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var textContainer: UILabel!
    weak var articleDelegate: ArticleDelegate?
    var appDelegate: AppDelegate!
    var article = Article()
    var category: String?
    var image: UIImage?

    override func viewDidLoad() {
        super.viewDidLoad()
        navigationController?.delegate = self
        appDelegate = UIApplication.shared.delegate as? AppDelegate
        titleLabel.text = article.title?.uppercased()
        textContainer.text = article.description
        categoryLabel.text = category
        starButton.setImage(article.favorite ? ImageSource.starOn : ImageSource.starOff , for: .normal)
        prepareParallax(headerWith: image)
    }

    override func viewWillAppear(_ animated: Bool) {
        navigationController?.isNavigationBarHidden = true
        navigationDrawerController?.isRightViewEnabled = false
    }

    override func viewWillDisappear(_ animated: Bool) {
        navigationController?.isNavigationBarHidden = false
        navigationDrawerController?.isRightViewEnabled = true
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

extension ArticleViewController{

    @IBAction func didStarTapped(_ sender: UIButton) {

        article.favorite = !article.favorite

        if article.favorite{
            starButton.setImage(ImageSource.starOn, for: .normal)
            Joyjet.favorites.append(article)
        }else{

            starButton.setImage(ImageSource.starOff, for: .normal)
            Joyjet.favorites.removeAll {
                if $0.title == article.title &&
                    $0.indexPath?.section == article.indexPath?.section &&
                    $0.indexPath?.row == article.indexPath?.row{
                    return true
                }else{
                    return false
                }
            }
        }

        starButton.pulseAnimation()
        appDelegate.feedController.didChange(article: article)
    }

    @IBAction func didBackTapped(_ sender: UIButton) {
        backButton.pulseAnimation()
        navigationController?.popViewController(animated: true)
    }

    func prepareParallax(headerWith image: UIImage?){
        let headerView = UIImageView()
        if let image = image{
            headerView.image = image
            headerView.setOverlay()
        }else{
            headerView
                .af_setImage(
                    withURL: article.galery[0].toUrl(),
                    placeholderImage: UIImage(named: "joyjet_banner"),
                    imageTransition: .crossDissolve(0.3),
                    runImageTransitionIfCached: false) { response in
                        if let data = response.data{
                            headerView.image = UIImage(data: data)
                            headerView.setOverlay()
                        }
            }
        }
        headerView.contentMode = .scaleAspectFill
        headerView.backgroundColor = .black
        prepareParallax(viewWith: headerView)
    }

    func prepareParallax(viewWith header: UIImageView){
        scrollView.parallaxHeader.view = header
        scrollView.parallaxHeader.height = 263
        scrollView.parallaxHeader.mode = .fill
        scrollView.parallaxHeader.minimumHeight = -0.1
        scrollView.parallaxHeader.view?.setOverlay()
        scrollView.delegate = self
    }
}

extension ArticleViewController: UIScrollViewDelegate{

    func scrollViewDidScroll(_ scrollView: UIScrollView) {

        // Fade in/out navigation bar itens
        if scrollView.parallaxHeader.progress <= 0.4 {
            starButton.fadeOut()
            backButton.fadeOut()
        }else{
            starButton.fadeIn()
            backButton.fadeIn()
        }
    }
}

extension ArticleViewController: UINavigationControllerDelegate{

    func navigationController(_ navigationController: UINavigationController, willShow viewController: UIViewController, animated: Bool) {
        if let coordinator = navigationController.topViewController?.transitionCoordinator {
            coordinator.notifyWhenInteractionChanges({ (context) in
                if !context.isCancelled{
                    print("=========> POPPED VIEW CONTROLLER VIA SWIPE")
                }
            })
        }
    }
}




