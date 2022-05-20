//
//  LiveViewCotroller.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit
import AgoraRtcKit
import AgoraEditAvatar

class LiveViewCotroller: UIViewController {
    let liveView = LiveView()
    var agoraKit: AgoraRtcEngineKit?
    var info: LiveRoomInfo!
    var entryType: EntryType!
    var openMic = false
    var members = [Member]()
    var infos = [Info]()
    var videoSetInfo: VideoSetInfo = .default
    var avatarEngineWapper: AvatarEngineWapper!
    var rtcConnetcion: AgoraRtcConnection!
    /// 当前使用avatar or 原图视频流
    var publishAvatarStream = true
    weak var localRenderView: UIView?
    
    /// init
    /// - Parameters:
    ///   - agoraKit: agoraKit from create vc
    init(info: LiveRoomInfo,
         agoraKit: AgoraRtcEngineKit?,
         avaterEngineWapper: AvatarEngineWapper?,
         videoSetInfo: VideoSetInfo?) {
        self.info = info
        self.agoraKit = agoraKit
        self.entryType = agoraKit != nil ? .fromCrateRoom : .fromJoinRoom
        self.avatarEngineWapper = avaterEngineWapper
        self.videoSetInfo = videoSetInfo ?? .default
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
                                              hasAudio: $0.hasAudio,
                                              hasVideo: $0.hasVideo,
                                              userId: $0.userId) })
            .filter({ $0.hasVideo })
        
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

// MARK: - show view

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
            self.members = members.filter({ ($0.status == .raising || $0.status == .accept) && $0.userId != UserInfo.uid })
            let list = self.members.map({ HandsUpSheetVC.Info(id: $0.userId,
                                                              style: $0.status == .raising ? .normal : .isUp,
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
    
    fileprivate func showVideoSetView() {
        let vc = VideoSettingSheetVC(videoInfo: videoSetInfo)
        vc.delegate = self
        vc.show(in: self)
    }
    
    fileprivate func showMoreViewForBeauty() {
        let vc = MoreSheetVC(infos: [.init(action: .dress), .init(action: .face)])
        vc.delegate = self
        vc.show(in: self)
    }
    
    fileprivate func showMoreViewForSet() {
        let vc = MoreSheetVC(infos: [.init(action: .change), .init(action: .videoSet)])
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showFaceUpView(list: [AvatarEngineWapper.FaceUpInfo]) {
        avatarEngineWapper.startFaceUp()
        let infos = list.map({ PinchFaceSheetVC.Info(info: $0) })
        let vc = PinchFaceSheetVC(infos: infos)
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showDressUpView(list: [AvatarEngineWapper.DressInfo]) {
        let infos = list.map({ DressUpSheetVC.Info(info: $0) })
        let vc = DressUpSheetVC(infos: infos)
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showWaitInit() {
        showHUDError(error: "请等待初始化完")
    }
    
    func showError(error: String) {
        if Thread.isMainThread {
            showHUDError(error: error)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.showHUDError(error: error)
        }
    }
}

// MARK: - view event

extension LiveViewCotroller: LiveViewDelegate,
                             VideoViewDelegate,
                             HandsUpSheetVCDelegate,
                             MoreSheetVCDelegate,
                             VideoSettingSheetVCDelegate,
                             PinchFaceSheetVCDelegate,
                             DressUpSheetVCDelegate {
    func pinchFaceSheetVCDidDismiss() {
        avatarEngineWapper.stopDressUp()
    }
    
    func dressUpSheetVCDidDismiss() {
        avatarEngineWapper.stopDressUp()
    }
    
    func dressUpSheetVCDidSelectedItem(index: Int,
                                       info: AEABottomInfo) {
        guard let infos = avatarEngineWapper.dressInfos else {
            return
        }
        let tempInfo = infos[index]
        let item = tempInfo.items[info.selectedItemIndex]
        let id = item.id
        let type = "\(tempInfo.type.rawValue)"
        avatarEngineWapper.updateDerssUp(type: type, id: id)
    }
    
    func pinchFaceSheetVCDidValueChange(infoIndex: Int,
                                        itemIndex: Int,
                                        value: Float) {
        guard let infos = avatarEngineWapper.faceUpInfos else {
            return
        }
        
        let item = infos[infoIndex].items[itemIndex]
        avatarEngineWapper.updateFaceUp(id: item.id,
                                        value: value)
    }
    
    func moreSheetVCDidTap(action: MoreSheetVC.Action) {
        guard avatarEngineWapper.didAvatarLoadSuccess else {
            showWaitInit()
            return
        }
        
        switch action {
        case .dress:
            avatarEngineWapper.startDressUp()
            avatarEngineWapper.requestDressUpList()
            break
        case .face:
            avatarEngineWapper.startFaceUp()
            avatarEngineWapper.requestFaceUpList()
            break
        case .change:
            changeAvatarAndOriginalStream()
            break
        case .videoSet:
            showVideoSetView()
            break
        }
    }
    
    func liveViewDidTapButtomAction(action: BottomView.ActionType) {
        switch action {
        case .mic:
            setMic()
            break
        case .handsup:
            entryType! == .fromCrateRoom ? showMembersView() : sendHandsupSync()
            break
        case .more:
            showMoreViewForSet()
            break
        case .beauty:
            showMoreViewForBeauty()
        default:
            break
        }
    }
    
    func videoViewShouldRender(info: VideoCell.Info, renderView: UIView) {
        setRenderView(info: info,
                      renderView: renderView)
    }
    
    func handsUpSheetVC(vc: HandsUpSheetVC, didTap action: HandsUpCell.Action, at index: Int) {
        let member = members[index]
        updateMember(member: member, action: action)
    }
    
    func videoSettingSheetVCDidTap(type: VideoSettingSheetVC.InfoType, value: Int) {
        var temp = videoSetInfo
        switch type {
        case .resolution:
            temp.resolution = .init(rawValue: value)!
            break
        case .fremeRate:
            temp.fremeRate = .init(rawValue: value)!
            break
        case .renderQuality:
            temp.renderQuality = .init(rawValue: value)!
            break
        }
        videoSetInfo = temp
        updateVideoConfig(videoInfo: videoSetInfo)
    }
    
    func videoSettingSheetVCDidValueChange(value: Int) {
        var temp = videoSetInfo
        temp.bitRate = value
        videoSetInfo = temp
        updateVideoConfig(videoInfo: videoSetInfo)
    }
}
