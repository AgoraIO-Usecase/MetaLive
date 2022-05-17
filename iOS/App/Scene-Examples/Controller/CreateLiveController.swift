//
//  CreateLiveController.swift
//  Scene-Examples
//
//  Created by zhaoyongqiang on 2021/11/10.
//

import UIKit
import AgoraRtcKit
import AgoraSyncManager
import AvatarSDK
import AgoraUIKit_iOS
import AgoraEditAvatar

protocol CreateLiveControllerDelegate: NSObjectProtocol {
    func createLiveControllerDidStartButtonTap(info: LiveRoomInfo,
                                               agoraKit: AgoraRtcEngineKit,
                                               avaterEngineWapper: AvatarEngineWapper,
                                               videoSetInfo: VideoSetInfo)
}

class CreateLiveController: UIViewController {
    private let createLiveView = CreateLiveView()
    weak var delegate: CreateLiveControllerDelegate?
    private var agoraKit: AgoraRtcEngineKit?
    var videoSetInfo: VideoSetInfo = .default
    var avatarEngineWapper: AvatarEngineWapper!
    var isAvatarLoaded = false
    
    deinit {
        LogUtils.log(message: "CreateLiveController deinit", level: .info)
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        commonInit()
        setupAgoraKit()
    }
    
    func setupUI() {
        createLiveView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(createLiveView)
        
        createLiveView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        createLiveView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        createLiveView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        createLiveView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
    }
    
    func commonInit() {
        createLiveView.delegate = self
    }
    
    private func setupAgoraKit() {
        agoraKit = CreateLiveController.createEngine()
        
        let avatarEngineWapper = CreateLiveController.createAvaterEngineWapper(agoraKit: agoraKit!)
        avatarEngineWapper.startInit()
        if agoraKit!.setAvatarEngineDelegate(avatarEngineWapper) == false { fatalError("set delegate fail") }
        avatarEngineWapper.setupLocalVideoCanvas(view: createLiveView.localView)
        avatarEngineWapper.delegate = self
        self.avatarEngineWapper = avatarEngineWapper

        let canvas0 = AgoraRtcVideoCanvas()
        canvas0.uid = 0
        canvas0.renderMode = .hidden
        canvas0.view = createLiveView.originalView
        agoraKit?.setupLocalVideo(canvas0)
        agoraKit?.startPreview()
    }
    
    static func createAvaterEngineWapper(agoraKit: AgoraRtcEngineKit) -> AvatarEngineWapper {
        guard let avaterEngine = agoraKit.queryAvatarEngine() else {
            fatalError("queryAvatarEngine fail")
        }
        
        let avatarEngineWapper = AvatarEngineWapper(engine: avaterEngine)
        return avatarEngineWapper
    }
    
    static func createEngine() -> AgoraRtcEngineKit {
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.appId
        config.channelProfile = .liveBroadcasting
        config.areaCode = .global
        
        let agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        agoraKit.setClientRole(.broadcaster)
        
        let videoConfig = AgoraVideoEncoderConfiguration(size: VideoSetInfo.default.resolution.size,
                                                         frameRate: VideoSetInfo.default.fremeRate.rtcType,
                                                         bitrate: VideoSetInfo.default.bitRate,
                                                         orientationMode: .fixedPortrait,
                                                         mirrorMode: .auto)
        agoraKit.setVideoEncoderConfiguration(videoConfig)
        
        /// 开启扬声器
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        agoraKit.enableVideo()
        return agoraKit
    }
    
    fileprivate func startRoom() {
        guard isAvatarLoaded else {
            showWaitInit()
            return
        }
        
        let roomName = createLiveView.randomNameView.text
        let roomId = "\(arc4random_uniform(899999) + 100000)"
        let info = LiveRoomInfo(roomName: roomName,
                                roomId: roomId,
                                userId: UserInfo.uid)
        let videoSetInfo = self.videoSetInfo
        guard let engine = agoraKit, let avatatEngine = avatarEngineWapper else {
            fatalError()
        }
        
        dismiss(animated: true, completion: { [weak self] in
            self?.delegate?.createLiveControllerDidStartButtonTap(info: info,
                                                                  agoraKit: engine,
                                                                  avaterEngineWapper: avatatEngine,
                                                                  videoSetInfo: videoSetInfo)
        })
    }
    
    func updateVideoConfig(videoInfo: VideoSetInfo) {
        guard let engine = agoraKit else { return }
        let videoConfig = AgoraVideoEncoderConfiguration(size: videoInfo.resolution.size,
                                                         frameRate: videoInfo.fremeRate.rtcType,
                                                         bitrate: videoInfo.bitRate,
                                                         orientationMode: .fixedPortrait,
                                                         mirrorMode: .enabled)
        let ret = engine.setVideoEncoderConfiguration(videoConfig)
        LogUtils.log(message: "setVideoEncoderConfiguration \(ret)", level: .info)
        
        avatarEngineWapper.setQuality(quality: videoInfo.renderQuality)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
    }
    
    func showFaceUpView(list: [AvatarEngineWapper.FaceUpInfo]) {
        guard isAvatarLoaded else {
            showWaitInit()
            return
        }
        avatarEngineWapper.startFaceUp()
        let infos = list.map({ PinchFaceSheetVC.Info(info: $0) })
        let vc = PinchFaceSheetVC(infos: infos)
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showDressUpView(list: [AvatarEngineWapper.DressInfo]) {
        guard isAvatarLoaded else {
            showWaitInit()
            return
        }
        let infos = list.map({ DressUpSheetVC.Info(info: $0) })
        let vc = DressUpSheetVC(infos: infos)
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showMoreView() {
        guard isAvatarLoaded else {
            showWaitInit()
            return
        }
        let vc = MoreSheetVC(infos: [.init(action: .dress), .init(action: .face)])
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showVideoSetView() {
        guard isAvatarLoaded else {
            showWaitInit()
            return
        }
        let vc = VideoSettingSheetVC(videoInfo: videoSetInfo)
        vc.delegate = self
        vc.show(in: self)
    }
    
    func showWaitInit() {
        showHUDError(error: "请等待初始化完")
    }
}

extension CreateLiveController: CreateLiveViewDelegate {
    func createLiveViewDidTapAction(action: CreateLiveView.Action) {
        switch action {
        case .close:
            avatarEngineWapper = nil
            AgoraRtcEngineKit.destroy()
            dismiss(animated: true, completion: nil)
            break
        case .start:
            startRoom()
            break
        case .setting:
            showVideoSetView()
            break
        case .beauty:
            showMoreView()
            break
        default:
            break
        }
    }
}

// MARK: - CAAvatarSDKDelegate AgoraAvatarEngineEventDelegate

extension CreateLiveController: AvatarEngineWapperDelegate {
    func avatarEngineWapperDidRecvEvent(event: AvatarEngineWapper.Event) {
        switch event {
        case .avatarSetSuccess:
            createLiveView.hidenIndicatedView()
            isAvatarLoaded = true
            updateVideoConfig(videoInfo: videoSetInfo)
            break
        case .avatarSetFail:
            showHUDError(error: "avatarSetFail")
            break
        }
    }
    
    func avatarEngineWapperDidRecvDressList(list: [AvatarEngineWapper.DressInfo]) {
        showDressUpView(list: list)
    }
    
    func avatarEngineWapperDidRecvFaceUpList(list: [AvatarEngineWapper.FaceUpInfo]) {
        showFaceUpView(list: list)
    }
}

extension CreateLiveController: VideoSettingSheetVCDelegate,
                                PinchFaceSheetVCDelegate,
                                DressUpSheetVCDelegate,
                                MoreSheetVCDelegate {
    func pinchFaceSheetVCDidDismiss() {
        avatarEngineWapper.stopDressUp()
    }
    
    func dressUpSheetVCDidDismiss() {
        avatarEngineWapper.stopDressUp()
    }
    
    func moreSheetVCDidTap(action: MoreSheetVC.Action) {
        switch action {
        case .face:
            avatarEngineWapper.requestFaceUpList()
            avatarEngineWapper.startFaceUp()
            break
        case .dress:
            avatarEngineWapper.requestDressUpList()
            avatarEngineWapper.stopDressUp()
            break
        default:
            break
        }
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
    
    func videoSettingSheetVCDidTap(type: VideoSettingSheetVC.InfoType, value: Int) {
        switch type {
        case .fremeRate:
            videoSetInfo.fremeRate = .init(rawValue: value)!
            break
        case .resolution:
            videoSetInfo.resolution = .init(rawValue: value)!
            break
        case .renderQuality:
            videoSetInfo.renderQuality = .init(rawValue: value)!
            break
        }
        
        updateVideoConfig(videoInfo: videoSetInfo)
    }
    
    func videoSettingSheetVCDidValueChange(value: Int) {
        videoSetInfo.bitRate = value
        updateVideoConfig(videoInfo: videoSetInfo)
    }
}
