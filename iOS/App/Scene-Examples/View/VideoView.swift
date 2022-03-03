//
//  VideoView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//

import UIKit

class VideoView: UIView {
    var viewItems = [VideoViewItem]()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        let viewItem1 = VideoViewItem()
        let viewItem2 = VideoViewItem()
        let viewItem3 = VideoViewItem()
        let viewItem4 = VideoViewItem()
        
        addSubview(viewItem1)
        addSubview(viewItem2)
        addSubview(viewItem3)
        addSubview(viewItem4)
        
        viewItem1.translatesAutoresizingMaskIntoConstraints = false
        viewItem2.translatesAutoresizingMaskIntoConstraints = false
        viewItem3.translatesAutoresizingMaskIntoConstraints = false
        viewItem4.translatesAutoresizingMaskIntoConstraints = false
        
        let w = (UIScreen.main.bounds.width - 30)/2
        let h = w+25
        
        viewItem1.leftAnchor.constraint(equalTo: leftAnchor, constant: 10).isActive = true
        viewItem1.topAnchor.constraint(equalTo: topAnchor, constant: 10).isActive = true
        viewItem1.widthAnchor.constraint(equalToConstant: w).isActive = true
        viewItem1.heightAnchor.constraint(equalToConstant: h).isActive = true
        
        viewItem2.rightAnchor.constraint(equalTo: rightAnchor, constant: -10).isActive = true
        viewItem2.topAnchor.constraint(equalTo: topAnchor, constant: 10).isActive = true
        viewItem2.widthAnchor.constraint(equalToConstant: w).isActive = true
        viewItem2.heightAnchor.constraint(equalToConstant: h).isActive = true
        
        viewItem3.leftAnchor.constraint(equalTo: leftAnchor, constant: 10).isActive = true
        viewItem3.topAnchor.constraint(equalTo: viewItem1.bottomAnchor, constant: 10).isActive = true
        viewItem3.widthAnchor.constraint(equalToConstant: w).isActive = true
        viewItem3.heightAnchor.constraint(equalToConstant: h).isActive = true
        
        viewItem4.rightAnchor.constraint(equalTo: rightAnchor, constant: -10).isActive = true
        viewItem4.topAnchor.constraint(equalTo: viewItem2.bottomAnchor, constant: 10).isActive = true
        viewItem4.widthAnchor.constraint(equalToConstant: w).isActive = true
        viewItem4.heightAnchor.constraint(equalToConstant: h).isActive = true
        
        viewItems = [viewItem1, viewItem2, viewItem3, viewItem4]
    }
}
