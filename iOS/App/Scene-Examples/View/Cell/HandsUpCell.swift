//
//  HandsUpCell.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//

import UIKit

protocol HandsUpCellDelegate: NSObjectProtocol {
    func handsUpCell(cell: HandsUpCell, didTap action: HandsUpCell.Action)
}

class HandsUpCell: UITableViewCell {
    private let imageIconView = UIImageView()
    private let titleLabel = UILabel()
    private let agreeButton = UIButton()
    private let rejectButton = UIButton()
    private var info: Info!
    var indexPath: IndexPath!
    weak var delegate: HandsUpCellDelegate?
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setup()
        commonInit()
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
        
        agreeButton.setTitle("同意", for: .normal)
        agreeButton.setTitleColor(.white, for: .normal)
        agreeButton.titleLabel?.font = .systemFont(ofSize: 16)
        agreeButton.backgroundColor = UIColor(hex: "6F57EB")
        agreeButton.layer.cornerRadius = 34/2
        agreeButton.layer.masksToBounds = true
        
        rejectButton.setTitle("拒绝", for: .normal)
        rejectButton.titleLabel?.font = .systemFont(ofSize: 16)
        rejectButton.setTitleColor(UIColor(hex: "6F57EB"), for: .normal)
        rejectButton.layer.borderColor = UIColor(hex: "6F57EB").cgColor
        rejectButton.layer.borderWidth = 1
        rejectButton.layer.cornerRadius = 34/2
        
        imageIconView.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        agreeButton.translatesAutoresizingMaskIntoConstraints = false
        rejectButton.translatesAutoresizingMaskIntoConstraints = false
        
        
        contentView.addSubview(imageIconView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(agreeButton)
        contentView.addSubview(rejectButton)
        
        imageIconView.leftAnchor.constraint(equalTo: leftAnchor, constant: 15).isActive = true
        imageIconView.topAnchor.constraint(equalTo: topAnchor, constant: 8).isActive = true
        imageIconView.heightAnchor.constraint(equalToConstant: 45).isActive = true
        imageIconView.widthAnchor.constraint(equalToConstant: 45).isActive = true
        
        titleLabel.leftAnchor.constraint(equalTo: imageIconView.rightAnchor, constant: 5).isActive = true
        titleLabel.centerYAnchor.constraint(equalTo: imageIconView.centerYAnchor).isActive = true
        
        agreeButton.rightAnchor.constraint(equalTo: rightAnchor, constant: -20).isActive = true
        agreeButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        agreeButton.heightAnchor.constraint(equalToConstant: 34).isActive = true
        agreeButton.widthAnchor.constraint(equalToConstant: 76).isActive = true
        
        rejectButton.rightAnchor.constraint(equalTo: agreeButton.leftAnchor, constant: -10).isActive = true
        rejectButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        rejectButton.heightAnchor.constraint(equalToConstant: 34).isActive = true
        rejectButton.widthAnchor.constraint(equalToConstant: 76).isActive = true
    }
    
    private func commonInit() {
        agreeButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        rejectButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
    }
    
    @objc func buttonTap(button: UIButton) {
        if button == rejectButton {
            delegate?.handsUpCell(cell: self, didTap: .reject)
            return
        }
        
        if button == agreeButton {
            let action: Action = info.style == .normal ? .up : .dowm
            delegate?.handsUpCell(cell: self, didTap: action)
            return
        }
    }
    
    func set(info: Info, indexPath: IndexPath) {
        self.info = info
        self.indexPath = indexPath
        imageIconView.image = UIImage(named: info.imageName)
        titleLabel.text = info.title
        let name = info.style == .normal ? "同意" : "下麦"
        agreeButton.setTitle(name, for: .normal)
        rejectButton.isHidden = info.style == .isUp
    }
}

extension HandsUpCell {
    enum Action {
        /// 拒绝
        case reject
        /// 上麦
        case up
        /// 下麦
        case dowm
    }
    
    enum Style {
        /// 正常
        case normal
        /// 已上麦
        case isUp
    }
    
    struct Info {
        let id: String
        let style: Style
        let title: String
        let imageName: String
    }
}
