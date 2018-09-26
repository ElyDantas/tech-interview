//
//  CustomCollectionCell.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
import ImageSlideshow
import AlamofireImage

class ArticleTableViewCell: UITableViewCell{

    @IBOutlet weak var previousButton: UIButton!
    @IBOutlet weak var nextButton: UIButton!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var carousel: ImageSlideshow!
    var cell: ArticleTableViewCell!
    weak var articleDelegate: ArticleDelegate?
    var article = Article()

    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
    }

}

extension ArticleTableViewCell{

    func prepareButtons(){
        if article.galery.count <= 1{
            previousButton.isHidden = true
            nextButton.isHidden = true
        }
    }

    func prepareCarousel(){
        let carouselTapGesture = UITapGestureRecognizer(target: self, action: #selector(self.didTap(_:)))
        carousel.addGestureRecognizer(carouselTapGesture)
        carousel.draggingEnabled = false
        carousel.pageIndicator = nil
        carousel.setOverlay()
        carousel.contentScaleMode = UIView.ContentMode.scaleAspectFill
        carousel.setImageInputs(article.galery.map({ AlamofireSource(urlString: $0)}).map{$0! as InputSource})
    }

    @IBAction func didTappedNextButton(_ sender: UIButton) {
        let index = (carousel.currentPage == carousel.images.count - 1) ? 0 : carousel.currentPage + 1
        carousel.setCurrentPage(index, animated: false)
    }

    @IBAction func didTappedPreviousButton(_ sender: UIButton) {
        let index = (carousel.currentPage == 0) ? carousel.images.count - 1 : carousel.currentPage - 1
        carousel.setCurrentPage(index, animated: false)
    }

    @objc func didTap(_ sender: UITapGestureRecognizer) {
        articleDelegate?.open(article: article, withImage: carousel.currentSlideshowItem?.imageView.image)
    }
}





