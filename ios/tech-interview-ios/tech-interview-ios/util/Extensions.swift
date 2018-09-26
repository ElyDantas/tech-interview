//
//  Extensions.swift
//  tech-interview-ios
//
//  Created by Ely Dantas on 19/09/2018.
//  Copyright © 2018 Ely. All rights reserved.
//

import Foundation
import UIKit
import PureLayout

extension UIViewController{
    func prepare(viewController identifier: String, storyboardId: String = "Main") -> UIViewController{
        return UIStoryboard(name: storyboardId, bundle: nil).instantiateViewController(withIdentifier: identifier)
    }
}

extension UIApplicationDelegate{
    func prepare(viewController identifier: String, storyboardId: String = "Main") -> UIViewController{
        return UIStoryboard(name: storyboardId, bundle: nil).instantiateViewController(withIdentifier: identifier)
    }
}

extension UITableViewCell {
    var tableView: UITableView? {
        return self.parentView(of: UITableView.self)
    }
}

extension UIView{

    func setOverlay(alpha: CGFloat = 0.2){
        if let overlay = self.viewWithTag(99999){
            overlay.backgroundColor = UIColor(red: 0/255, green: 0/255, blue: 0/255, alpha: alpha)
        }else{
            let overlay = UIView(frame: CGRect(x: 0, y: 0, width: frame.size.width, height: frame.size.height))
            overlay.backgroundColor = UIColor(red: 0/255, green: 0/255, blue: 0/255, alpha: alpha)
            overlay.tag = 99999
            addSubview(overlay)
            overlay.autoPinEdgesToSuperviewEdges()
        }
    }

    func parentView<T: UIView>(of type: T.Type) -> T? {
        guard let view = self.superview else {
            return nil
        }
        return (view as? T) ?? view.parentView(of: T.self)
    }

    func fadeIn(_ duration: TimeInterval = 0.3, delay: TimeInterval? = 0.0, completion: @escaping ((Bool) -> Void) = {(finished: Bool) -> Void in}) {
        UIView.animate(withDuration: duration, delay: delay!, options: UIView.AnimationOptions.curveLinear, animations:{
            self.alpha = 1.0
        }, completion: completion)  }

    func fadeOut(_ duration: TimeInterval = 0.3, delay: TimeInterval? = 0.0, completion: @escaping (Bool) -> Void = {(finished: Bool) -> Void in}) {
        UIView.animate(withDuration: duration, delay: delay!, options: UIView.AnimationOptions.curveLinear, animations:{
            self.alpha = 0.0
        }, completion: completion)
    }

    func pulseAnimation(_ duration: TimeInterval = 0.3, delay: TimeInterval = 0.0, completion: @escaping (Bool) -> Void = {(finished: Bool) -> Void in}) {
        UIView.animate(withDuration: duration, delay: delay, options: [UIView.AnimationOptions.curveLinear, UIView.AnimationOptions.allowUserInteraction], animations:{
            self.alpha = 0.0
            self.alpha = 1.0
        }, completion: completion)
    }
}

extension Array {
    func grouped<T>(by criteria: (Element) -> T) -> [T: [Element]] {
        var groups = [T: [Element]]()
        for element in self {
            let key = criteria(element)
            if groups.keys.contains(key) == false {
                groups[key] = [Element]()
            }
            groups[key]?.append(element)
        }
        return groups
    }
    func randomItem() -> Element? {
        if isEmpty { return nil }
        let index = Int(arc4random_uniform(UInt32(self.count)))
        return self[index]
    }
}

extension UIImageView{

    func transitionCrossDissolve(withImage newImage: UIImage){
        UIView.transition(with: self,
                          duration: 0.3,
                          options: .transitionCrossDissolve,
                          animations: { self.image = newImage},completion: nil)
    }
}

extension String{

    func toUrl() -> URL{
        return URL(string: self)!
    }
}

extension UIImage {
    func alpha(_ value:CGFloat) -> UIImage {
        UIGraphicsBeginImageContextWithOptions(size, false, scale)
        draw(at: CGPoint.zero, blendMode: .normal, alpha: value)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
}

extension UIColor{
    convenience init(hex:String){
        var cString:String = hex.trimmingCharacters(in: .whitespacesAndNewlines).uppercased()
        if (cString.hasPrefix("#")) { cString.remove(at: cString.startIndex)}
        var rgbValue:UInt32 = 0
        Scanner(string: cString).scanHexInt32(&rgbValue)
        self.init(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
}

typealias UIButtonTargetClosure = (UIButton) -> ()

extension UIButton {

    private struct AssociatedKeys {
        static var targetClosure = "targetClosure"
    }

    private var targetClosure: UIButtonTargetClosure? {
        get {
            guard let closureWrapper = objc_getAssociatedObject(self, &AssociatedKeys.targetClosure) as? ClosureWrapper else { return nil }
            return closureWrapper.closure
        }
        set(newValue) {
            guard let newValue = newValue else { return }
            objc_setAssociatedObject(self, &AssociatedKeys.targetClosure, ClosureWrapper(newValue), objc_AssociationPolicy.OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }

    func addTargetClosure(closure: @escaping UIButtonTargetClosure) {
        targetClosure = closure
        addTarget(self, action: #selector(UIButton.closureAction), for: .touchUpInside)
    }

    @objc func closureAction() {
        guard let targetClosure = targetClosure else { return }
        targetClosure(self)
    }
}

class ClosureWrapper: NSObject {
    let closure: UIButtonTargetClosure
    init(_ closure: @escaping UIButtonTargetClosure) {
        self.closure = closure
    }
}
