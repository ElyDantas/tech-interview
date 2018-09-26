//
//  Category.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 21/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import Foundation

class Category: Decodable{

    var category: String?
    var articles = [Article]()

    init() {}

    enum CodingKeys: String, CodingKey {
        case category = "category"
        case articles = "items"
    }
}
