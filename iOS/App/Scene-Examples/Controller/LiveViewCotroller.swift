//
//  LiveViewCotroller.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit

class LiveViewCotroller: UIViewController {
    var videoView: VideoView!
    var notiView: NotiView!
    let bottomView = BottomView()
    var bottomConstraint: NSLayoutConstraint!
    override func viewDidLoad() {
        super.viewDidLoad()
        notiView = NotiView()
        videoView = VideoView()
        setupUI()
        commonInit()
    }
    
    func setupUI() {
        notiView.translatesAutoresizingMaskIntoConstraints = false
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        videoView.translatesAutoresizingMaskIntoConstraints = false
        
        view.addSubview(videoView)
        view.addSubview(notiView)
        view.addSubview(bottomView)
        
        videoView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor).isActive = true
        videoView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        videoView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        videoView.bottomAnchor.constraint(equalTo: notiView.topAnchor).isActive = true
        
        notiView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        notiView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        notiView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -1 * BottomView.viewHeight).isActive = true
        notiView.heightAnchor.constraint(equalToConstant: 170).isActive = true
        
        bottomView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        bottomView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        bottomConstraint = bottomView.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor)
        bottomConstraint.isActive = true
        bottomView.heightAnchor.constraint(equalToConstant: BottomView.viewHeight).isActive = true
    }
    
    func commonInit() {
        bottomView.delegate = self
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
//        let info = NotiView.Info(imageName: "portrait01", title: "小仁", subtitle: "送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛送出了一个铃铛")
//        notiView.append(info: info)
//        view.endEditing(true)
        
        let list = Array<HandsUpSheetVC.Info>(repeating: .init(style: .isUp, title: "hahahha", imageName: "portrait01"), count: 4)
        let vc = HandsUpSheetVC()
        vc.show(in: self, list: list)
    }
}

extension LiveViewCotroller: BottomViewDelegate {
    func bottomView(view: BottomView, didTap action: BottomView.ActionType) {
        
    }
    
    func bottomView(view: BottomView, shouldUpdate bottonConstant: CGFloat) {
        bottomConstraint.constant = bottonConstant
        self.view.layoutIfNeeded()
    }
}
