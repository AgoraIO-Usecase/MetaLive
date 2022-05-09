//
//  LiveViewCotroller.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit
import AgoraRtcKit

class LiveViewCotroller: UIViewController {
    private let liveView = LiveView()
    private var agoraKit: AgoraRtcEngineKit?
    fileprivate var info: LiveRoomInfo!
    fileprivate var entryType: EntryType!
    fileprivate var openMic = false
    
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
        agoraKit?.leaveChannel({ state in
            LogUtils.log(message: "\(state)", level: .info)
        })
        AgoraRtcEngineKit.destroy()
        let roomId = info.roomId
        entryType == .fromCrateRoom ? RoomManager.deleteRoom(roomId: roomId) : RoomManager.leaveRoom(roomId: roomId)
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
    }
    
    private func commonInit() {
        liveView.delegate = self
    }
    
    private func setupAgoraKit() {
        if entryType == .fromJoinRoom {
            let config = AgoraRtcEngineConfig()
            config.appId = KeyCenter.appId
            config.channelProfile = .liveBroadcasting
            config.areaCode = .global
            
            agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
            agoraKit?.setClientRole(.broadcaster)
            agoraKit?.enableVideo()
            agoraKit?.disableAudio()
            agoraKit?.setDefaultAudioRouteToSpeakerphone(true)
            joinChannel()
            return
        }
        
        /** from create vc **/
        joinChannel()
    }
    
    private func joinChannel() {
        let option = AgoraRtcChannelMediaOptions()
        option.publishAudioTrack = .of(true)
        option.publishCameraTrack = .of(true)
        option.clientRoleType = .of((Int32)(AgoraClientRole.broadcaster.rawValue))
        option.autoSubscribeVideo = .of(true)
        option.autoSubscribeAudio = .of(true)
        
        guard let engine = agoraKit else {
            fatalError("agoraKit must not nil")
        }
        engine.delegate = self
        let result = engine.joinChannel(byToken: KeyCenter.token,
                                        channelId: info.roomId,
                                        uid: UserInfo.userId,
                                        mediaOptions: option,
                                        joinSuccess: nil)
        
        
        if result != 0 {
            LogUtils.log(message: "joinChannel fail: \(result)", level: .error)
        }
        else {
            LogUtils.log(message: "joinChannel success: \(info.roomId)", level: .info)
        }
    }
    
    private func joinSync() {
        if entryType == .fromCrateRoom {
            RoomManager.createAndJoin(info: info) { [weak self](members) in
                self?.handleMembersUpdate(members: members)
                self?.subscribeEvent()
                LogUtils.log(message: "createAndJoin success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
        else {
            RoomManager.join(roomId: info.roomId) { [weak self](members) in
                self?.handleMembersUpdate(members: members)
                self?.subscribeEvent()
                LogUtils.log(message: "join success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
    }
    
    private func subscribeEvent() {
        if entryType == .fromJoinRoom {
            RoomManager.subscribeRoomDelete(roomId: info.roomId, onDeleted: handleRoomDelete)
        }
    }
    
    private func handleMembersUpdate(members: [Member]) {
//        let acceptMembers = members.filter({ $0.status == .accept })
        
        if let hostMember = members.first(where: { $0.userId == info.userId }) { /// 房主
            let canvas = AgoraRtcVideoCanvas()
            
            canvas.renderMode = .hidden
            canvas.view = liveView.renderViews.first
            if hostMember.userId == UserInfo.uid { /** 当前是房主本人 **/
                canvas.uid = 0
                agoraKit?.setupLocalVideo(canvas)
                agoraKit?.enableVideo()
                agoraKit?.startPreview()
            }
            else { /** 当前非房主本人 **/
                canvas.uid = UInt(hostMember.userId)!
                agoraKit?.setupRemoteVideo(canvas)
            }
        }
    }
    
    private func handleRoomDelete() {
        showAlert(title: "room_is_closed".localized, message: "") { [weak self] in
            self?.navigationController?.popViewController(animated: true)
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        let list = Array<HandsUpSheetVC.Info>(repeating: .init(style: .isUp, title: "hahahha", imageName: "portrait01"), count: 4)
        let vc = HandsUpSheetVC()
        vc.show(in: self, list: list)
    }
}

extension LiveViewCotroller: LiveViewDelegate {
    func liveViewDidTapButtomAction(action: BottomView.ActionType) {
        switch action {
        case .mic:
            openMic = !openMic
            agoraKit?.muteLocalAudioStream(openMic)
            break
        default:
            break
        }
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
}
