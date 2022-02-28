//
//  NavigationController.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/2/25.
//

import UIKit

class NavigationController: UINavigationController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        navigationBar.shadowImage = UIImage()
        let image = createImageWithColor(UIColor(hex: "FAF9FF"))
        navigationBar.setBackgroundImage(image, for: .default)
        navigationBar.titleTextAttributes = [.font : UIFont.systemFont(ofSize: 22, weight: .medium)]
        navigationBar.isTranslucent = false
    }
    
    
    func createImageWithColor(_ color: UIColor, frame: CGRect = CGRect(x: 0, y: 0, width: 1, height: 1)) -> UIImage? {
        // 开始绘图
        UIGraphicsBeginImageContext(frame.size)
        
        // 获取绘图上下文
        let context = UIGraphicsGetCurrentContext()
        // 设置填充颜色
        context?.setFillColor(color.cgColor)
        // 使用填充颜色填充区域
        context?.fill(frame)
        
        // 获取绘制的图像
        let image = UIGraphicsGetImageFromCurrentImageContext()
        
        // 结束绘图
        UIGraphicsEndImageContext()
        return image
    }
}
