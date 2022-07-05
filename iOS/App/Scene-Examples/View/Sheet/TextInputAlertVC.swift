//
//  ASCheckBoxAlertVC.swift
//  AgoraSceneUI
//
//  Created by ZYP on 2021/1/19.
//

import UIKit
import Presentr

protocol TextInputAlertVCDelegate: NSObject {
    func textInputAlertVCDidTapSureButton(text: String)
}

class TextInputAlertVC: UIViewController {
    private let titleLabel = UILabel()
    private let cancleButton = UIButton()
    private let sureButton = UIButton()
    private let textField = UITextField()
    private let presenter = Presentr(presentationType: .custom(width: .custom(size: 280), height: .custom(size: 230), center: .center))
    weak var delegate: TextInputAlertVCDelegate?
    
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
        
        textField.placeholder = "请输入房间名称"
        textField.returnKeyType = .done
        
        let randomButton = UIButton()
        randomButton.setImage(UIImage(named: "icon-random"), for: .normal)
        
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
        textFieldBgView.addSubview(randomButton)
        view.addSubview(textField)
        view.addSubview(cancleButton)
        view.addSubview(sureButton)
        
        bgView.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        textFieldBgView.translatesAutoresizingMaskIntoConstraints = false
        randomButton.translatesAutoresizingMaskIntoConstraints = false
        textField.translatesAutoresizingMaskIntoConstraints = false
        cancleButton.translatesAutoresizingMaskIntoConstraints = false
        sureButton.translatesAutoresizingMaskIntoConstraints = false
        
        NSLayoutConstraint.activate([bgView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
                                     bgView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
                                     bgView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
                                     bgView.topAnchor.constraint(equalTo: view.topAnchor)])
        
        NSLayoutConstraint.activate([titleLabel.topAnchor.constraint(equalTo: view.topAnchor, constant: 20),
                                     titleLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor)])
        
        NSLayoutConstraint.activate([textFieldBgView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20),
                                     textFieldBgView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 30),
                                     textFieldBgView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -30),
                                     textFieldBgView.heightAnchor.constraint(equalToConstant: 68)])
        
        NSLayoutConstraint.activate([textField.centerYAnchor.constraint(equalTo: textFieldBgView.centerYAnchor),
                                     textField.leftAnchor.constraint(equalTo: textFieldBgView.leftAnchor, constant: 10),
                                     textField.rightAnchor.constraint(equalTo: textFieldBgView.rightAnchor, constant: -40)])
        
        NSLayoutConstraint.activate([randomButton.centerYAnchor.constraint(equalTo: textField.centerYAnchor),
                                     randomButton.rightAnchor.constraint(equalTo: textFieldBgView.rightAnchor, constant: -10)])
        
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
        textField.delegate = self
    }
    
    func show(in vc: UIViewController) {
        config()
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
            let text = textField.text ?? ""
            dismiss(animated: true, completion: { [weak self]() in
                self?.delegate?.textInputAlertVCDidTapSureButton(text: text)
            })
            return
        }
        
        dismiss(animated: true, completion: nil)
    }
    
    func config() {
        titleLabel.text = "创建房间"
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }

}

extension TextInputAlertVC: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        view.endEditing(true)
        let text = textField.text ?? ""
        if text.isEmpty {
            return false
        }
        dismiss(animated: true, completion: { [weak self]() in
            self?.delegate?.textInputAlertVCDidTapSureButton(text: text)
        })
        return true
    }
}
