//
//  VideoViewItem.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//

import UIKit

class VideoViewItem: UIView {
    let titleLabel = UILabel()
    let micImageView = UIImageView()
    let renderView = UIView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        backgroundColor = .clear
        titleLabel.textColor = .black.withAlphaComponent(0.85)
        titleLabel.font = .systemFont(ofSize: 14)
        titleLabel.text = "12345"
        
        micImageView.image = UIImage(named: "icon-mic-off")
        
        renderView.backgroundColor = UIColor(hex: "F9F9F9")
        renderView.layer.cornerRadius = 50
        renderView.layer.masksToBounds = true
        
        addSubview(renderView)
        addSubview(titleLabel)
        addSubview(micImageView)
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        micImageView.translatesAutoresizingMaskIntoConstraints = false
        renderView.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -3).isActive = true
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        
        let w = (UIScreen.main.bounds.width - 3*10)/2
        renderView.topAnchor.constraint(equalTo: topAnchor, constant: 0).isActive = true
        renderView.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        renderView.heightAnchor.constraint(equalToConstant: w).isActive = true
        renderView.widthAnchor.constraint(equalToConstant: w).isActive = true
        
        micImageView.rightAnchor.constraint(equalTo: renderView.rightAnchor).isActive = true
        micImageView.bottomAnchor.constraint(equalTo: renderView.bottomAnchor).isActive = true
        micImageView.widthAnchor.constraint(equalToConstant: 28).isActive = true
        micImageView.heightAnchor.constraint(equalToConstant: 28).isActive = true
    }
}
