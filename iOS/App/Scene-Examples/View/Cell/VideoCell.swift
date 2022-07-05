//
//  VideoCell.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/9.
//

import UIKit

class VideoCell: UICollectionViewCell {
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
    
    func setup() {
        backgroundColor = .clear
        titleLabel.textColor = .black.withAlphaComponent(0.85)
        titleLabel.font = .systemFont(ofSize: 14)
        titleLabel.text = "12345"
        
        micImageView.image = UIImage(named: "icon-mic-off")
        
        renderView.backgroundColor = UIColor(hex: "F9F9F9")
        renderView.layer.cornerRadius = 50
        renderView.layer.masksToBounds = true
        
        contentView.addSubview(renderView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(micImageView)
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        micImageView.translatesAutoresizingMaskIntoConstraints = false
        renderView.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -3).isActive = true
        titleLabel.centerXAnchor.constraint(equalTo: contentView.centerXAnchor).isActive = true
        titleLabel.heightAnchor.constraint(equalToConstant: 17).isActive = true
        
        renderView.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 0).isActive = true
        renderView.bottomAnchor.constraint(equalTo: titleLabel.topAnchor, constant: -3).isActive = true
        renderView.leftAnchor.constraint(equalTo: contentView.leftAnchor).isActive = true
        renderView.rightAnchor.constraint(equalTo: contentView.rightAnchor).isActive = true
        
        micImageView.rightAnchor.constraint(equalTo: renderView.rightAnchor).isActive = true
        micImageView.bottomAnchor.constraint(equalTo: renderView.bottomAnchor).isActive = true
        micImageView.widthAnchor.constraint(equalToConstant: 28).isActive = true
        micImageView.heightAnchor.constraint(equalToConstant: 28).isActive = true
    }
    
    func update(info: Info) {
        titleLabel.text = info.title
        micImageView.image = info.hasAudio ? UIImage(named: "icon-mic-on") : UIImage(named: "icon-mic-off")
        renderView.isHidden = !info.hasVideo
    }
}

extension VideoCell {
    struct Info {
        let title: String
        let hasAudio: Bool
        let hasVideo: Bool
        let userId: String
        
        static var empty: Info {
            Info(title: "",
                 hasAudio: false,
                 hasVideo: false,
                 userId: "")
        }
    }
}
