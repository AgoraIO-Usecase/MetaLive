//
//  LiveViewCotroller.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit
import AgoraRtcKit

class LiveViewCotroller: UIViewController {
    let liveView = LiveView()
    var agoraKit: AgoraRtcEngineKit?
    var info: LiveRoomInfo!
    var entryType: EntryType!
    var openMic = false
    var members = [Member]()
    var infos = [Info]()
    
    /// init
    /// - Parameters:
    ///   - agoraKit: agoraKit from create vc
    init(info: LiveRoomInfo,
         agoraKit: AgoraRtcEngineKit?) {
        self.info = info
        self.agoraKit = agoraKit
        self.entryType = agoraKit != nil ? .fromCrateRoom : .fromJoinRoom
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        LogUtils.log(message: "LiveViewCotroller deinit", level: .info)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        commonInit()
        joinSync()
        setupAgoraKit()
    }
    
    private func setupUI() {
        title = "\(info.roomName)(\(info.roomId))"
        view.backgroundColor = .white
        
        liveView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(liveView)
        liveView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        liveView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        liveView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        liveView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(image: .init(named: "icon_close_white"),
                                                           style: .plain,
                                                           target: self,
                                                           action: #selector(closeTap))
    }
    
    private func commonInit() {
        liveView.videoView.delegate = self
        liveView.delegate = self
    }
    
    func updateView() {
        let list = infos.map({ VideoCell.Info(title: $0.title,
                                              havAudio: $0.hasAudio,
                                              havVideo: $0.hasVideo,
                                              userId: $0.userId) })
            .filter({ $0.havVideo })
        
        let currentMicState = infos.first(where: { $0.userId == UserInfo.uid })?.member.status == .accept
        
        if Thread.isMainThread {
            liveView.enableMic(enable: currentMicState)
            liveView.videoView.update(infos: list)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.liveView.enableMic(enable: currentMicState)
            self?.liveView.videoView.update(infos: list)
        }
    }
    
    @objc func closeTap() {
        entryType == .fromJoinRoom ? showQuictAlert() : showCloseAlert()
    }
}

extension LiveViewCotroller { /** show view **/
    func showBeCloseAlert() { /** 房间被关闭 **/
        showAlert(title: "room_is_closed".localized, message: "", showCancle: false, confirm: { [weak self] in
            self?.closeSync()
            self?.closeRtc()
            self?.navigationController?.popViewController(animated: true)
        })
    }
    
    func showCloseAlert() { /** 关闭房间 **/
        showAlert(title: "room_close".localized, message: "", confirm: { [weak self] in
            self?.closeSync()
            self?.closeRtc()
            self?.navigationController?.popViewController(animated: true)
        })
    }
    
    func showQuictAlert() { /** 离开房间 **/
        showAlert(title: "Leave_Live_Room".localized, message: "", confirm: { [weak self] in
            self?.closeSync()
            self?.closeRtc()
            self?.navigationController?.popViewController(animated: true)
        })
    }
    
    private func showMembersView() {
        showWaitHUD()
        getMembers(success: { [weak self](members) in
            guard let self = self else { return }
            self.members = members.filter({ ($0.status == .inviting || $0.status == .accept) && $0.userId != UserInfo.uid })
            let list = self.members.map({ HandsUpSheetVC.Info(id: $0.userId,
                                                              style: $0.status == .inviting ? .normal : .isUp,
                                                              title: $0.userName,
                                                              imageName: "portrait01") })
            let vc = HandsUpSheetVC()
            vc.show(in: self, list: list)
            vc.delegate = self
            self.hideHUD()
        }, fail: { [weak self](error) in
            self?.hideHUD()
            self?.showHUDError(error: error.description)
        })
    }
}

extension LiveViewCotroller: LiveViewDelegate, VideoViewDelegate, HandsUpSheetVCDelegate {
    func liveViewDidTapButtomAction(action: BottomView.ActionType) {
        switch action {
        case .mic:
            setMic()
            break
        case .handsup:
            entryType! == .fromCrateRoom ? showMembersView() : sendHandsupSync()
            break
        default:
            break
        }
    }
    
    func videoViewShouldRender(info: VideoCell.Info, renderView: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = renderView
        canvas.renderMode = .hidden
        if info.userId == UserInfo.uid {
            canvas.uid = 0
            agoraKit?.setupLocalVideo(canvas)
            agoraKit?.startPreview()
        }
        else {
            canvas.uid = UInt(info.userId)!
            agoraKit?.setupRemoteVideo(canvas)
        }
    }
    
    func handsUpSheetVC(vc: HandsUpSheetVC, didTap action: HandsUpCell.Action, at index: Int) {
        let member = members[index]
        updateMember(member: member, action: action)
    }
}

extension LiveViewCotroller { /** info **/
    enum EntryType {
        /// 房主进入
        case fromCrateRoom
        /// 成员进入
        case fromJoinRoom
    }
    
    struct Info: Equatable {
        let uid: UInt
        let title: String
        let userId: String
        let hasAudio: Bool
        let hasVideo: Bool
        let member: Member
        
        init(member: Member) {
            self.uid = UInt(member.userId)!
            self.title = member.userName
            self.userId = member.userId
            self.hasAudio = member.hasAudio
            self.hasVideo = member.status == .accept
            self.member = member
        }
        
        static func == (lhs: Self, rhs: Self) -> Bool {
            return lhs.uid == rhs.uid &&
            lhs.title == rhs.title &&
            lhs.userId == rhs.userId &&
            lhs.hasAudio == rhs.hasAudio &&
            lhs.hasVideo == rhs.hasVideo
        }
        
        var isMe: Bool {
            return userId == UserInfo.uid
        }
    }
}
