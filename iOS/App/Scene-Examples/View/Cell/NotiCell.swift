//
//  NotiCell.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit

class NotiCell: UITableViewCell {
    private let imageIconView = UIImageView()
    private let titleLabel = UILabel()
    private let subtitleLabel = UILabel()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setup() {
        selectionStyle = .none
        imageIconView.layer.cornerRadius = 45/2
        imageIconView.layer.masksToBounds = true
        
        titleLabel.font = .systemFont(ofSize: 12, weight: .medium)
        titleLabel.textColor = UIColor(hex: "B9B9B9")
        
        subtitleLabel.font = .systemFont(ofSize: 12, weight: .bold)
        subtitleLabel.textColor = .black.withAlphaComponent(0.5)
        subtitleLabel.numberOfLines = 0
        
        imageIconView.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        subtitleLabel.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(imageIconView)
        addSubview(titleLabel)
        addSubview(subtitleLabel)
        
        imageIconView.leftAnchor.constraint(equalTo: leftAnchor, constant: 15).isActive = true
        imageIconView.topAnchor.constraint(equalTo: topAnchor, constant: 8).isActive = true
        imageIconView.heightAnchor.constraint(equalToConstant: 45).isActive = true
        imageIconView.widthAnchor.constraint(equalToConstant: 45).isActive = true
        
        titleLabel.leftAnchor.constraint(equalTo: imageIconView.rightAnchor, constant: 5).isActive = true
        titleLabel.bottomAnchor.constraint(equalTo: imageIconView.centerYAnchor, constant: -3).isActive = true
        
        subtitleLabel.leftAnchor.constraint(equalTo: imageIconView.rightAnchor, constant: 5).isActive = true
        subtitleLabel.topAnchor.constraint(equalTo: imageIconView.centerYAnchor, constant: 3).isActive = true
        subtitleLabel.rightAnchor.constraint(equalTo: rightAnchor, constant: -5).isActive = true
        subtitleLabel.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    
    func set(imageName: String,
             title: String,
             subtitle: String) {
        imageIconView.image = UIImage(named: imageName)
        titleLabel.text = title
        subtitleLabel.text = subtitle
    }
}
