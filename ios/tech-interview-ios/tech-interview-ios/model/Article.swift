//
//  Article.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import Foundation

class Article: Decodable{

    var title: String?
    var description: String?
    var galery = [String]()
    var category: String?
    var indexPath: IndexPath?
    var favorite = false

    init() {}

    enum CodingKeys: String, CodingKey{
        case title = "title"
        case description = "description"
        case galery = "galery"
    }
}





