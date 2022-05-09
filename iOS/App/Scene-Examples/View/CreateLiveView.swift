//
//  CreateLiveView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/6.
//

import UIKit

protocol CreateLiveViewDelegate: NSObjectProtocol {
    func createLiveViewDidTapAction(action: CreateLiveView.Action)
}

class CreateLiveView: UIView {
    let randomNameView = LiveRandomNameView()
    let localView = UIView()
    private let cameraChangeButton = UIButton()
    private let settingButton = UIButton()
    private let startLiveButton = UIButton()
    private let closeButton = UIButton()
    private let beautyButton = UIButton()
    weak var delegate: CreateLiveViewDelegate?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupUI() {
        beautyButton.setImage(.init(named: "icon-magic"), for: .normal)
        closeButton.setImage(.init(named: "icon_close_white"), for: .normal)
        cameraChangeButton.setImage(UIImage(named: "icon-camera-change"), for: .normal)
        
        settingButton.setImage(UIImage(named: "icon-setting-normal"), for: .normal)
        settingButton.setImage(UIImage(named: "icon-setting"), for: .selected)
        
        startLiveButton.setTitle("Create_Start".localized, for: .normal)
        startLiveButton.setTitleColor(.white, for: .normal)
        startLiveButton.titleLabel?.font = .systemFont(ofSize: 14)
        startLiveButton.backgroundColor = .blueColor
        startLiveButton.layer.cornerRadius = 20
        startLiveButton.layer.masksToBounds = true
        
        backgroundColor = .init(hex: "#404B54")
        
        randomNameView.translatesAutoresizingMaskIntoConstraints = false
        startLiveButton.translatesAutoresizingMaskIntoConstraints = false
        settingButton.translatesAutoresizingMaskIntoConstraints = false
        localView.translatesAutoresizingMaskIntoConstraints = false
        cameraChangeButton.translatesAutoresizingMaskIntoConstraints = false
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        beautyButton.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(localView)
        addSubview(randomNameView)
        addSubview(settingButton)
        addSubview(closeButton)
        addSubview(beautyButton)
        addSubview(startLiveButton)
        addSubview(cameraChangeButton)
        
        closeButton.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor).isActive = true
        closeButton.leftAnchor.constraint(equalTo: leftAnchor, constant: 10).isActive = true
        
        cameraChangeButton.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor).isActive = true
        cameraChangeButton.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        
        localView.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        localView.topAnchor.constraint(equalTo:topAnchor).isActive = true
        localView.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
        localView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        
        randomNameView.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 10).isActive = true
        randomNameView.topAnchor.constraint(equalTo: cameraChangeButton.bottomAnchor, constant: 10).isActive = true
        randomNameView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -10).isActive = true
        randomNameView.heightAnchor.constraint(equalToConstant: 40).isActive = true
        
        startLiveButton.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        startLiveButton.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor, constant: -35).isActive = true
        startLiveButton.widthAnchor.constraint(equalToConstant: 100).isActive = true
        startLiveButton.heightAnchor.constraint(equalToConstant: 40).isActive = true
        
        settingButton.centerYAnchor.constraint(equalTo: startLiveButton.centerYAnchor).isActive = true
        settingButton.leadingAnchor.constraint(equalTo: startLiveButton.trailingAnchor, constant: 25).isActive = true
        
        beautyButton.rightAnchor.constraint(equalTo: startLiveButton.leftAnchor, constant: -25).isActive = true
        beautyButton.centerYAnchor.constraint(equalTo: startLiveButton.centerYAnchor).isActive = true
    }
    
    private func commonInit() {
        cameraChangeButton.addTarget(self, action: #selector(buttonTap(_:)), for: .touchUpInside)
        settingButton.addTarget(self, action: #selector(buttonTap(_:)), for: .touchUpInside)
        startLiveButton.addTarget(self, action: #selector(buttonTap(_:)), for: .touchUpInside)
        closeButton.addTarget(self, action: #selector(buttonTap(_:)), for: .touchUpInside)
    }
    
    @objc private func buttonTap(_ sender: UIButton) {
        if sender == closeButton {
            delegate?.createLiveViewDidTapAction(action: .close)
            return
        }
        
        if sender == startLiveButton {
            delegate?.createLiveViewDidTapAction(action: .start)
            return
        }
    }
}

extension CreateLiveView {
    enum Action {
        case close
        case start
        case switchCamera
        case setting
        case beauty
    }
}
