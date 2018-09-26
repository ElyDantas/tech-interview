//
//  Joyjet.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 21/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import Foundation
import Alamofire

protocol JoyjetService{
    static func fetchCategories(completetion: @escaping ((_ categories: [Category]) -> Void))
}

class Joyjet: JoyjetService{

    static private let url = "https://cdn.joyjet.com/tech-interview/mobile-test-one.json"
    static var favorites = [Article]()

    /// Retrieve categories from web service
    static func fetchCategories(completetion: @escaping ((_ categories: [Category]) -> Void)){
        Alamofire.request(url).responseData { response in
            if let data = response.data{
                do{
                    let categories = try JSONDecoder().decode([Category].self, from: data)
                    completetion(categories)
                }catch{
                    print(error)
                }
            }
        }
    }

    /// Retrieve favorite's articles
    static func getFavorites(from categories: [Category]) -> [Article]{
        var favorites = [Article]()
        categories.forEach { favorites.append(contentsOf: $0.articles.filter { return $0.favorite})}
        return favorites
    }
}
