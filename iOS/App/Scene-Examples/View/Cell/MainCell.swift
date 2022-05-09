//
//  MainCell.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/2/28.
//

import UIKit

class MainCell: UITableViewCell {
    private let titleLabel = UILabel()
    private let personLabel = UILabel()
    private let personIconView = UIImageView()
    private let imageIconView1 = UIImageView()
    private let imageIconView2 = UIImageView()
    private let imageIconView3 = UIImageView()
    private let imageIconView4 = UIImageView()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        selectionStyle = .none
        backgroundColor = .clear
        titleLabel.font = UIFont.systemFont(ofSize: 17, weight: .medium)
        personLabel.text = "0"
        personLabel.textColor = UIColor(hex: "989A9C")
        personLabel.font = UIFont.systemFont(ofSize: 12)
        personIconView.image = UIImage(named: "icon-user")
        
        imageIconView1.image = UIImage(named: "portrait01")
        imageIconView2.image = UIImage(named: "portrait02")
        imageIconView3.image = UIImage(named: "portrait03")
        imageIconView4.image = UIImage(named: "portrait04")
        
        imageIconView1.layer.cornerRadius = 22
        imageIconView2.layer.cornerRadius = 22
        imageIconView3.layer.cornerRadius = 22
        imageIconView4.layer.cornerRadius = 22
        
        imageIconView1.layer.masksToBounds = true
        imageIconView2.layer.masksToBounds = true
        imageIconView3.layer.masksToBounds = true
        imageIconView4.layer.masksToBounds = true
        
        let bgView = UIView()
        bgView.layer.cornerRadius = 33
        bgView.backgroundColor = .white
        
        bgView.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        imageIconView1.translatesAutoresizingMaskIntoConstraints = false
        imageIconView2.translatesAutoresizingMaskIntoConstraints = false
        imageIconView3.translatesAutoresizingMaskIntoConstraints = false
        imageIconView4.translatesAutoresizingMaskIntoConstraints = false
        personLabel.translatesAutoresizingMaskIntoConstraints = false
        personIconView.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(bgView)
        addSubview(titleLabel)
        addSubview(imageIconView1)
        addSubview(imageIconView2)
        addSubview(imageIconView3)
        addSubview(imageIconView4)
        addSubview(personLabel)
        addSubview(personIconView)
        
        bgView.leftAnchor.constraint(equalTo: leftAnchor, constant: 25).isActive = true
        bgView.rightAnchor.constraint(equalTo: rightAnchor, constant: -25).isActive = true
        bgView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        bgView.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -15).isActive = true
        
        titleLabel.leftAnchor.constraint(equalTo: bgView.leftAnchor, constant: 15).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 27).isActive = true
        
        imageIconView1.leftAnchor.constraint(equalTo: titleLabel.leftAnchor).isActive = true
        imageIconView1.bottomAnchor.constraint(equalTo: bottomAnchor, constant: -35).isActive = true
        imageIconView1.heightAnchor.constraint(equalToConstant: 44).isActive = true
        imageIconView1.widthAnchor.constraint(equalToConstant: 44).isActive = true
        
        imageIconView2.leftAnchor.constraint(equalTo: imageIconView1.rightAnchor, constant: -5).isActive = true
        imageIconView2.centerYAnchor.constraint(equalTo: imageIconView1.centerYAnchor).isActive = true
        imageIconView2.heightAnchor.constraint(equalToConstant: 44).isActive = true
        imageIconView2.widthAnchor.constraint(equalToConstant: 44).isActive = true
        
        imageIconView3.leftAnchor.constraint(equalTo: imageIconView2.rightAnchor, constant: -5).isActive = true
        imageIconView3.centerYAnchor.constraint(equalTo: imageIconView1.centerYAnchor).isActive = true
        imageIconView3.heightAnchor.constraint(equalToConstant: 44).isActive = true
        imageIconView3.widthAnchor.constraint(equalToConstant: 44).isActive = true
        
        imageIconView4.leftAnchor.constraint(equalTo: imageIconView3.rightAnchor, constant: -5).isActive = true
        imageIconView4.centerYAnchor.constraint(equalTo: imageIconView1.centerYAnchor).isActive = true
        imageIconView4.heightAnchor.constraint(equalToConstant: 44).isActive = true
        imageIconView4.widthAnchor.constraint(equalToConstant: 44).isActive = true
        
        personIconView.rightAnchor.constraint(equalTo: bgView.rightAnchor, constant: -15).isActive = true
        personIconView.centerYAnchor.constraint(equalTo: imageIconView1.centerYAnchor).isActive = true
        
        personLabel.rightAnchor.constraint(equalTo: personIconView.leftAnchor, constant: -5).isActive = true
        personLabel.centerYAnchor.constraint(equalTo: imageIconView1.centerYAnchor).isActive = true
    }
    
    func set(title: String) {
        titleLabel.text = title
    }
}
