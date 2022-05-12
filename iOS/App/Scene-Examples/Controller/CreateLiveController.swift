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

protocol CreateLiveControllerDelegate: NSObjectProtocol {
    func createLiveControllerDidStartButtonTap(info: LiveRoomInfo, agoraKit: AgoraRtcEngineKit)
}

class CreateLiveController: UIViewController {
    private let createLiveView = CreateLiveView()
    weak var delegate: CreateLiveControllerDelegate?
    
    private var agoraKit: AgoraRtcEngineKit?
    
    
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
        avaterEngine = agoraKit?.queryAvatarEngine()
        let avatarConfigs = AgoraAvatarConfigs()
        avatarConfigs.mode = .avatar
        avatarConfigs.enable_face_detection = 1
        avatarConfigs.enable_human_detection = 0
        avaterEngine!.enableOrUpdateLocalAvatarVideo(true, configs: avatarConfigs)
        
        /// 渲染
        let canvas = AgoraRtcVideoCanvas()
        canvas.uid = UserInfo.userId
        canvas.renderMode = .hidden
        canvas.view = createLiveView.localView
        avaterEngine.setupLocalVideoCanvas(canvas)
        agoraKit?.startPreview()
    }
    
    private var avaterEngine: AvatarEngineProtocol!
    
    static func createEngine() -> AgoraRtcEngineKit {
        let config = AgoraRtcEngineConfig()
        config.appId = KeyCenter.appId
        config.channelProfile = .liveBroadcasting
        config.areaCode = .global
        
        let videoConfig = AgoraVideoEncoderConfiguration(size: VideoSetInfo.default.resolution.size,
                                                         frameRate: VideoSetInfo.default.fremeRate.rtcType,
                                                         bitrate: VideoSetInfo.default.bitRate,
                                                         orientationMode: .fixedPortrait,
                                                         mirrorMode: .auto)
        
        let agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
        agoraKit.setClientRole(.broadcaster)
        agoraKit.setVideoEncoderConfiguration(videoConfig)
        
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
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        view.endEditing(true)
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
        default:
            break
        }
    }
}

// MARK: - CAAvatarSDKDelegate

extension CreateLiveController: CAAvatarSDKDelegate {
    func onInit() {
        
    }
    
    func onError(_ error: String!) {
        
    }
}
