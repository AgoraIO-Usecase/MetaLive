//
//  BottomView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit

protocol BottomViewDelegate: NSObjectProtocol {
    func bottomView(view: BottomView, shouldUpdate bottomConstant: CGFloat)
    func bottomView(view: BottomView, didTap action: BottomView.ActionType)
}

class BottomView: UIView {
    static let viewHeight = 38.0
    weak var delegate: BottomViewDelegate?
    let textBgView = UIView()
    let textField = UITextField()
    let micButton = UIButton()
    let beautyButton = UIButton()
    let moreButton = UIButton()
    let handupsButton = UIButton()
    let textBgViewWidthMin = 130.0
    let textBgViewWidthMax = UIScreen.main.bounds.width - 15 - 15
    var textBgViewWidthConstraint: NSLayoutConstraint!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        textBgView.layer.cornerRadius = BottomView.viewHeight/2
        textBgView.backgroundColor = UIColor(hex: "F5F5F9")
        
        textField.placeholder = "说点什么..."
        textField.returnKeyType = .send
        textField.clearButtonMode = .whileEditing
        
        micButton.setImage(UIImage(named: "icon-mic-off"), for: .normal)
        micButton.setImage(UIImage(named: "icon-mic-on"), for: .selected)
        beautyButton.setImage(UIImage(named: "icon-magic-grap"), for: .normal)
        moreButton.setImage(UIImage(named: "icon-more"), for: .normal)
        handupsButton.setImage(UIImage(named: "icon-raisehand"), for: .normal)
        
        textBgView.translatesAutoresizingMaskIntoConstraints = false
        textField.translatesAutoresizingMaskIntoConstraints = false
        micButton.translatesAutoresizingMaskIntoConstraints = false
        beautyButton.translatesAutoresizingMaskIntoConstraints = false
        handupsButton.translatesAutoresizingMaskIntoConstraints = false
        moreButton.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(textBgView)
        addSubview(textField)
        addSubview(micButton)
        addSubview(beautyButton)
        addSubview(moreButton)
        addSubview(handupsButton)
        
        textBgView.leftAnchor.constraint(equalTo: leftAnchor, constant: 15).isActive = true
        textBgView.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        textBgView.heightAnchor.constraint(equalToConstant: BottomView.viewHeight).isActive = true
        textBgViewWidthConstraint = textBgView.widthAnchor.constraint(equalToConstant: textBgViewWidthMin)
        textBgViewWidthConstraint.isActive = true
        
        textField.leftAnchor.constraint(equalTo: textBgView.leftAnchor, constant: 10).isActive = true
        textField.rightAnchor.constraint(equalTo: textBgView.rightAnchor, constant: -10).isActive = true
        textField.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        
        handupsButton.rightAnchor.constraint(equalTo: rightAnchor, constant: -17).isActive = true
        handupsButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        
        moreButton.rightAnchor.constraint(equalTo: handupsButton.leftAnchor, constant: -5).isActive = true
        moreButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        
        beautyButton.rightAnchor.constraint(equalTo: moreButton.leftAnchor, constant: -5).isActive = true
        beautyButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        
        micButton.rightAnchor.constraint(equalTo: beautyButton.leftAnchor, constant: -5).isActive = true
        micButton.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
    }
    
    private func commonInit() {
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(keyboardWillShow(noti:)),
                                               name: UIResponder.keyboardWillShowNotification,
                                               object: nil)
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(keyboardWillHide(noti:)),
                                               name: UIResponder.keyboardWillHideNotification,
                                               object: nil)
        textField.delegate = self
        micButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        beautyButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        handupsButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        moreButton.addTarget(self, action: #selector(buttonTap(button:)), for: .touchUpInside)
        textField.isUserInteractionEnabled = false
    }
    
    @objc func keyboardWillShow(noti: Notification) {
        let kFrame = noti.userInfo![UIResponder.keyboardFrameEndUserInfoKey] as! CGRect
        let duration = noti.userInfo![UIResponder.keyboardAnimationDurationUserInfoKey] as! Double
        let bottom = UIApplication.shared.windows.first?.safeAreaInsets.bottom ?? 0
        UIView.animate(withDuration: duration) {
            let constant = (kFrame.size.height - bottom) * -1
            self.delegate?.bottomView(view: self, shouldUpdate: constant)
            self.setEditting(true)
            self.layoutIfNeeded()
        }
    }
    
    @objc func keyboardWillHide(noti: Notification) {
        let duration = noti.userInfo![UIResponder.keyboardAnimationDurationUserInfoKey] as! Double
        UIView.animate(withDuration: duration) {
            self.delegate?.bottomView(view: self, shouldUpdate: 0)
            self.setEditting(false)
            self.layoutIfNeeded()
        }
    }
    
    @objc func buttonTap(button: UIButton) {
        var actionType = ActionType.more
        
        if button == micButton {
            micButton.isSelected = !micButton.isSelected
            actionType = .mic
            delegate?.bottomView(view: self, didTap: actionType)
            return
        }
        
        if button == beautyButton {
            actionType = .beauty
            delegate?.bottomView(view: self, didTap: actionType)
            return
        }
        
        if button == moreButton {
            actionType = .more
            delegate?.bottomView(view: self, didTap: actionType)
            return
        }
        
        if button == handupsButton {
            actionType = .handsup
            delegate?.bottomView(view: self, didTap: actionType)
            return
        }
    }
    
    func setMic(open: Bool) {
        micButton.isSelected = !open
    }
    
    func setEditting(_ editting: Bool) {
        if editting {
            micButton.isHidden = true
            beautyButton.isHidden = true
            moreButton.isHidden = true
            handupsButton.isHidden = true
            textBgViewWidthConstraint.constant = textBgViewWidthMax
        }
        else {
            micButton.isHidden = false
            beautyButton.isHidden = false
            moreButton.isHidden = false
            handupsButton.isHidden = false
            textBgViewWidthConstraint.constant = textBgViewWidthMin
        }
    }
}

extension BottomView: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        let text = textField.text ?? ""
        if text.isEmpty { return false }
        endEditing(true)
        return true
    }
}

extension BottomView {
    enum ActionType {
        case mic
        case beauty
        case more
        case gift
        case handsup
    }
}
