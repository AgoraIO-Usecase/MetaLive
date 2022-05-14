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
    func createLiveControllerDidStartButtonTap(info: LiveRoomInfo, agoraKit: AgoraRtcEngineKit)
}

class CreateLiveController: UIViewController {
    private let createLiveView = CreateLiveView()
    weak var delegate: CreateLiveControllerDelegate?
    private var agoraKit: AgoraRtcEngineKit?
    var videoSetInfo: VideoSetInfo = .default
    var avatarEngineWapper: AvatarEngineWapper!
    
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
        
        guard let avaterEngine = agoraKit?.queryAvatarEngine() else {
            fatalError("queryAvatarEngine fail")
        }
        
        avatarEngineWapper = .init(engine: avaterEngine)
        avatarEngineWapper.delegate = self
        
        let context = AgoraAvatarContext()
        context.aiAppId = KeyCenter.cocosAppId
        context.aiToken = KeyCenter.cocosAppKey
        let ret = avaterEngine.initialize(context)
        if ret != 0 {
            LogUtils.log(message: "initialize fail \(ret)", level: .info)
        }
        
        agoraKit?.setAvatarEngineDelegate(avatarEngineWapper)
        
        let avatarConfigs = AgoraAvatarConfigs()
        avatarConfigs.mode = .avatar
        avatarConfigs.mediaSource = .primaryCamera
        avatarConfigs.enable_face_detection = 1
        avatarConfigs.enable_human_detection = 0
        avaterEngine.enableOrUpdateLocalAvatarVideo(true, configs: avatarConfigs)
        
        /// 渲染
        let canvas = AgoraRtcVideoCanvas()
        canvas.uid = 0
        canvas.renderMode = .hidden
        canvas.view = createLiveView.localView
        avaterEngine.setupLocalVideoCanvas(canvas)

        let canvas0 = AgoraRtcVideoCanvas()
        canvas0.uid = 0
        canvas0.renderMode = .hidden
        canvas0.view = createLiveView.originalView
        agoraKit?.setupLocalVideo(canvas0)
        agoraKit?.startPreview()
    }
    
    static func createEngine() -> AgoraRtcEngineKit {
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.appId
        config.channelProfile = .liveBroadcasting
        config.areaCode = .global
        
        let agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        agoraKit.setClientRole(.broadcaster)
        
        /// 开启扬声器
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        agoraKit.enableVideo()
        return agoraKit
    }
    
    fileprivate func startRoom() {
        let roomName = createLiveView.randomNameView.text
        let roomId = "\(arc4random_uniform(899999) + 100000)"
        let info = LiveRoomInfo(roomName: roomName,
                                roomId: roomId,
                                userId: UserInfo.uid)
        guard let engine = agoraKit else {
            fatalError()
        }
        
        dismiss(animated: true, completion: { [weak self] in
            self?.delegate?.createLiveControllerDidStartButtonTap(info: info, agoraKit: engine)
        })
    }
    
    func updateVideoConfig(videoInfo: VideoSetInfo) {
        let videoConfig = AgoraVideoEncoderConfiguration(size: videoInfo.resolution.size,
                                                         frameRate: videoInfo.fremeRate.rtcType,
                                                         bitrate: videoInfo.bitRate,
                                                         orientationMode: .fixedPortrait,
                                                         mirrorMode: .auto)
        agoraKit?.setVideoEncoderConfiguration(videoConfig)
        
        avatarEngineWapper.setQuality(quality: videoInfo.renderQuality)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
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
        vc.show(in: self)
    }
}

extension CreateLiveController: CreateLiveViewDelegate {
    func createLiveViewDidTapAction(action: CreateLiveView.Action) {
        switch action {
        case .close:
            dismiss(animated: true, completion: nil)
            break
        case .start:
            startRoom()
            break
        case .setting:
            let info = VideoSetInfo(resolution: .v640x480,
                                    fremeRate: .fps30,
                                    renderQuality: .high,
                                    bitRate: 7000)
            let vc = VideoSettingSheetVC(videoInfo: info)
            vc.delegate = self
            vc.show(in: self)
            break
        case .beauty:
            avatarEngineWapper.requestFaceUpList()
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

extension CreateLiveController: VideoSettingSheetVCDelegate, PinchFaceSheetVCDelegate {
    func pinchFaceSheetVCDidValueChange(infoIndex: Int,
                                        itemIndex: Int,
                                        value: Float) {
        guard let infos = avatarEngineWapper.faceUpInfos else {
            return
        }
        
        let item = infos[infoIndex].items[itemIndex]
        avatarEngineWapper.updateFaceUp(id: item.id,
                                        value: item.value)
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
