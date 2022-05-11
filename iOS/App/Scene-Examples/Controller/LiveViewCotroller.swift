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
    var currentMember: Member?
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
        
        navigationItem.leftBarButtonItem = UIBarButtonItem(title: "关闭", style: .plain, target: self, action: #selector(closeTap))
    }
    
    private func commonInit() {
        liveView.videoView.delegate = self
        liveView.delegate = self
    }
    
    func handleMembersUpdate(members: [Member]) {
        currentMember = members.first(where: { $0.userId == UserInfo.uid })
        let temp = members.filter({ $0.status == .accept })
        update(members: temp)
    }
    
    func handleRoomDelete() {
        showAlert(title: "room_is_closed".localized, message: "") { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
    }
    
    private func setMic() {
        guard let objId = RoomManager.currentMemberId, let member = currentMember else {
            return
        }
        let state = !openMic
        var temp = member
        temp.hasAudio = state
        showWaitHUD()
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: objId,
                                 member: temp) { [weak self] in
            self?.openMic = state
            self?.agoraKit?.muteLocalAudioStream(state)
            self?.hideHUD()
        } fail: { [weak self](error) in
            self?.showHUDError(error: error.description)
        }
    }
    
    private func showHandsupListView() {
        showWaitHUD()
        RoomManager.getMembers(roomId: info.roomId) { [weak self](members) in
            guard let self = self else {
                return
            }
            self.members = members.filter({ ($0.status == .inviting || $0.status == .accept) && $0.userId != UserInfo.uid })
            let list = self.members.map({ HandsUpSheetVC.Info(id: $0.userId,
                                                              style: $0.status == .inviting ? .normal : .isUp,
                                                              title: $0.userName,
                                                              imageName: "portrait01") })
            let vc = HandsUpSheetVC()
            vc.show(in: self, list: list)
            vc.delegate = self
            self.hideHUD()
        } fail: { [weak self](error) in
            self?.hideHUD()
            self?.showHUDError(error: error.description)
        }
    }
    
    private func sendHandsup() {
        guard let objId = RoomManager.currentMemberId, let member = currentMember else {
            return
        }
        var temp = member
        temp.status = .inviting
        showWaitHUD(title: "举手中")
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: objId,
                                 member: temp) { [weak self] in
            self?.hideHUD()
            self?.showHUD(title: "举手成功", duration: 3)
        } fail: { [weak self](error) in
            self?.hideHUD()
            self?.showHUDError(error: error.description)
        }
    }
    
    func removeRenderView(member: Member) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = nil
        canvas.uid = UInt(member.userId)!
        if member.userId == UserInfo.uid {
            agoraKit?.setupLocalVideo(canvas)
        }
        else {
            agoraKit?.setupRemoteVideo(canvas)
        }
    }
    
    func updateView() {
        let list = infos.map({ VideoCell.Info(title: $0.title,
                                              havAudio: $0.hasAudio,
                                              havVideo: $0.hasVideo,
                                              userId: $0.userId) })
            .filter({ $0.havVideo })
        
        if Thread.isMainThread {
            liveView.videoView.update(infos: list)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.liveView.videoView.update(infos: list)
        }
    }
    
    @objc func closeTap() {
        let roomId = info.roomId
        RoomManager.unsubscribeMember(roomId: roomId)
        RoomManager.unsubscribeRoomDelete(roomId: roomId)
        entryType == .fromCrateRoom ? RoomManager.deleteRoom(roomId: roomId) : RoomManager.leaveRoom(roomId: roomId)
        agoraKit?.leaveChannel({ state in
            LogUtils.log(message: "\(state)", level: .info)
        })
        AgoraRtcEngineKit.destroy()
        navigationController?.popViewController(animated: true)
    }
}

extension LiveViewCotroller: LiveViewDelegate, VideoViewDelegate, HandsUpSheetVCDelegate {
    func liveViewDidTapButtomAction(action: BottomView.ActionType) {
        switch action {
        case .mic:
            setMic()
            break
        case .handsup:
            if entryType == .fromCrateRoom {
                showHandsupListView()
            }
            else {
                sendHandsup()
            }
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
        var member = members[index]
        switch action {
        case .reject:
            member.status = .refuse
            break
        case .dowm:
            member.status = .end
            break
        case .up:
            member.status = .accept
            break
        }
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: member.objectId,
                                 member: member,
                                 success: {
            self.showHUD(title: "成功")
        }, fail: { [weak self](error) in
            self?.showHUDError(error: error.localizedDescription)
        })
    }
}

extension LiveViewCotroller: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurWarning warningCode: AgoraWarningCode) {
        LogUtils.log(message: "warning: \(warningCode.description)", level: .warning)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOccurError errorCode: AgoraErrorCode) {
        LogUtils.log(message: "error: \(errorCode)", level: .error)
        showAlert(title: "Error", message: "Error \(errorCode.description) occur", confirm: {})
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
        LogUtils.log(message: "Join \(channel) with uid \(uid) elapsed \(elapsed)ms", level: .info)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        LogUtils.log(message: "remote user join: \(uid) \(elapsed)ms", level: .info)
        
        let noti = NotiView.Info(imageName: "icon-user",
                                 title: "\(uid) ",
                                 subtitle: "Join_Live_Room".localized)
        liveView.append(noti: noti)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        LogUtils.log(message: "remote user leval: \(uid) reason \(reason)", level: .info)
        let noti = NotiView.Info(imageName: "icon-user",
                                 title: "\(uid) ",
                                 subtitle: "Leave_Live_Room".localized)
        liveView.append(noti: noti)
    }
}

extension LiveViewCotroller {
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
        
        init(member: Member) {
            self.uid = UInt(member.userId)!
            self.title = member.userName
            self.userId = member.userId
            self.hasAudio = member.hasAudio
            self.hasVideo = member.status == .accept
        }
    }
}
