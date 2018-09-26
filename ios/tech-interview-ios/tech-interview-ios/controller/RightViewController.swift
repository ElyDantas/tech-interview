//
//  RightViewController.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import UIKit

class RightViewController: UIViewController {

    @IBOutlet weak var joyjetIcon: UIImageView!
    var appDelegate: AppDelegate!

    override func viewDidLoad() {
        super.viewDidLoad()
        appDelegate = UIApplication.shared.delegate as? AppDelegate
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}

extension RightViewController {

    @IBAction func openListCategory(_ sender: UIButton) {

        if let navigationController = navigationDrawerController?.rootViewController as? AppNavigationController{
            let controllerType = type(of: navigationController.viewControllers[0])
            if controllerType is FeedTableViewController.Type{
                closeNavigationDrawer(result: true)
                return
            }
        }

        navigationDrawerController?.transition(
            to: AppNavigationController(rootViewController: appDelegate.feedController),
            completion: closeNavigationDrawer
        )
    }

    @IBAction func openListFavorite(_ sender: UIButton) {

        if let navigationController = navigationDrawerController?.rootViewController as? AppNavigationController{
            let controllerType = type(of: navigationController.viewControllers[0])
            if controllerType is FavoriteTableViewController.Type{
                closeNavigationDrawer(result: true)
                return
            }
        }

        appDelegate.feedController.openFavorites()
    }

    fileprivate func closeNavigationDrawer(result: Bool) {
        navigationDrawerController?.closeRightView()
    }
}
