//
//  LiveView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/6.
//

import UIKit

protocol LiveViewDelegate: NSObjectProtocol {
    func liveViewDidTapButtomAction(action: BottomView.ActionType)
}

class LiveView: UIView {
    let videoView = VideoView()
    private let notiView = NotiView()
    private let bottomView = BottomView()
    private var bottomConstraint: NSLayoutConstraint?
    weak var delegate: LiveViewDelegate?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setupUI() {
        backgroundColor = .white
        notiView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        videoView.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(videoView)
        addSubview(notiView)
        addSubview(bottomView)
        
        videoView.topAnchor.constraint(equalTo: safeAreaLayoutGuide.topAnchor).isActive = true
        videoView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        videoView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        videoView.bottomAnchor.constraint(equalTo: notiView.topAnchor).isActive = true
        
        notiView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        notiView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        notiView.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor, constant: -1 * BottomView.viewHeight).isActive = true
        notiView.heightAnchor.constraint(equalToConstant: 170).isActive = true
        
        bottomView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        bottomView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        bottomConstraint = bottomView.bottomAnchor.constraint(equalTo: safeAreaLayoutGuide.bottomAnchor)
        bottomConstraint?.isActive = true
        bottomView.heightAnchor.constraint(equalToConstant: BottomView.viewHeight).isActive = true
    }
    
    private func commonInit() {
        bottomView.delegate = self
    }
    
    func append(noti: NotiView.Info) {
        notiView.append(info: noti)
    }
}

extension LiveView: BottomViewDelegate {
    func bottomView(view: BottomView, didTap action: BottomView.ActionType) {
        delegate?.liveViewDidTapButtomAction(action: action)
    }
    
    func bottomView(view: BottomView, shouldUpdate bottonConstant: CGFloat) {
        bottomConstraint?.constant = bottonConstant
        layoutIfNeeded()
    }
}
