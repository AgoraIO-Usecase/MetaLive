//
//  AlertVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//

import UIKit
import Presentr

protocol AlertVCDelegate: NSObject {
    func alertVCDidTapSureButton(vc: AlertVC)
}

class AlertVC: UIViewController {
    private let titleLabel = UILabel()
    private let cancleButton = UIButton()
    private let sureButton = UIButton()
    private let subTitleLabel = UILabel()
    private let presenter = Presentr(presentationType: .custom(width: .custom(size: 280), height: .custom(size: 210), center: .center))
    weak var delegate: AlertVCDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        commonInit()
    }
    
    private func setupUI() {
        let bgView = UIView()
        bgView.layer.cornerRadius = 13
        bgView.layer.masksToBounds = true
        bgView.backgroundColor = .white
        view.backgroundColor = .clear
        
        titleLabel.font = UIFont.systemFont(ofSize: 18)
        
        let textFieldBgView = UIView()
        textFieldBgView.layer.cornerRadius = 14
        textFieldBgView.backgroundColor = UIColor(hex: "D8D8D8")
        
        subTitleLabel.textColor = .black.withAlphaComponent(0.5)
        subTitleLabel.font = .systemFont(ofSize: 14)
        subTitleLabel.numberOfLines = 0
        subTitleLabel.textAlignment = .center
        
        cancleButton.setTitle("取消", for: .normal)
        cancleButton.titleLabel?.font = .systemFont(ofSize: 16)
        cancleButton.setTitleColor(UIColor(hex: "6F57EB"), for: .normal)
        cancleButton.layer.borderColor = UIColor(hex: "6F57EB").cgColor
        cancleButton.layer.borderWidth = 1
        cancleButton.layer.cornerRadius = 20
        cancleButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        
        sureButton.setTitle("确定", for: .normal)
        sureButton.setTitleColor(.white, for: .normal)
        sureButton.titleLabel?.font = .systemFont(ofSize: 16)
        sureButton.backgroundColor = UIColor(hex: "6F57EB")
        sureButton.layer.cornerRadius = 20
        sureButton.layer.masksToBounds = true
        sureButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        
        view.addSubview(bgView)
        view.addSubview(textFieldBgView)
        textFieldBgView.addSubview(titleLabel)
        view.addSubview(subTitleLabel)
        view.addSubview(cancleButton)
        view.addSubview(sureButton)
        
        bgView.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        textFieldBgView.translatesAutoresizingMaskIntoConstraints = false
        subTitleLabel.translatesAutoresizingMaskIntoConstraints = false
        cancleButton.translatesAutoresizingMaskIntoConstraints = false
        sureButton.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([bgView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                                     bgView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                                     bgView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
                                     bgView.topAnchor.constraint(equalTo: view.topAnchor)])
        
        NSLayoutConstraint.activate([titleLabel.topAnchor.constraint(equalTo: view.topAnchor, constant: 20),
                                     titleLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor)])
        
        NSLayoutConstraint.activate([subTitleLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20),
                                     subTitleLabel.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 30),
                                     subTitleLabel.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -30)])
        
        NSLayoutConstraint.activate([cancleButton.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 15),
                                     cancleButton.trailingAnchor.constraint(equalTo: view.centerXAnchor, constant: -5),
                                     cancleButton.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -30),
                                     cancleButton.heightAnchor.constraint(equalToConstant: 40)])
        
        NSLayoutConstraint.activate([sureButton.leadingAnchor.constraint(equalTo: view.centerXAnchor, constant: 5),
                                     sureButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -15),
                                     sureButton.bottomAnchor.constraint(equalTo: view.bottomAnchor, constant: -30),
                                     sureButton.heightAnchor.constraint(equalToConstant: 40)])
    }
    
    func commonInit() {
        
    }
    
    func show(in vc: UIViewController) {
        presenter.roundCorners = true
        let prensentAnimation = SystemAlertPrensentAnimation()
        let dismissAnimation = CrossDissolveAnimation(options: .normal(duration: 0.25))
        presenter.transitionType = .custom(prensentAnimation)
        presenter.dismissTransitionType = .custom(dismissAnimation)
        presenter.backgroundOpacity = 0.18
        presenter.backgroundTap = .noAction
        presenter.cornerRadius = 13
        presenter.keyboardTranslationType = .moveUp
        vc.customPresentViewController(presenter, viewController: self, animated: true, completion: nil)
    }
    
    @objc func buttonTap(button: UIButton) {
        view.endEditing(true)
        
        if button == sureButton {
            dismiss(animated: true, completion: { [weak self]() in
                guard let self = self else {
                    return
                }
                self.delegate?.alertVCDidTapSureButton(vc: self)
            })
            return
        }
        
        dismiss(animated: true, completion: nil)
    }
    
    func set(title: String, subTitle: String) {
        titleLabel.text = title
        subTitleLabel.text = subTitle
    }
}

extension AlertVC: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        view.endEditing(true)
        let text = textField.text ?? ""
        if text.isEmpty {
            return false
        }
        dismiss(animated: true, completion: { [weak self]() in
            guard let self = self else {
                return
            }
            self.delegate?.alertVCDidTapSureButton(vc: self)
        })
        return true
    }
}
