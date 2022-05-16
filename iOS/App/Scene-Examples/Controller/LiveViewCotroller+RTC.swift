//
//  LiveViewCotroller+RTC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/10.
//

import Foundation
import AgoraRtcKit

extension LiveViewCotroller {
    func setupAgoraKit() {
        if entryType == .fromJoinRoom {
            let config = AgoraRtcEngineConfig()
            config.appId = KeyCenter.appId
            config.channelProfile = .liveBroadcasting
            config.areaCode = .global
            
            let videoConfig = AgoraVideoEncoderConfiguration(size: VideoSetInfo.default.resolution.size,
                                                             frameRate: VideoSetInfo.default.fremeRate.rtcType,
                                                             bitrate: VideoSetInfo.default.bitRate,
                                                             orientationMode: .fixedPortrait,
                                                             mirrorMode: .auto)
            
            agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: self)
            agoraKit?.setClientRole(.broadcaster)
            agoraKit?.setVideoEncoderConfiguration(videoConfig)
            agoraKit?.enableVideo()
            agoraKit?.setDefaultAudioRouteToSpeakerphone(true)
            
            avatarEngineWapper = CreateLiveController.createAvaterEngineWapper(agoraKit: agoraKit!)
            avatarEngineWapper.delegate = self
            avatarEngineWapper.startInit()
            agoraKit?.setAvatarEngineDelegate(avatarEngineWapper)
            joinChannel()
            return
        }
        
        /** from create vc **/
        avatarEngineWapper.delegate = self
        if !avatarEngineWapper.hasStartInit {
            avatarEngineWapper.startInit()
        }
        agoraKit?.setAvatarEngineDelegate(avatarEngineWapper)
        agoraKit?.delegate = self
        joinChannel()
    }
    
    func joinChannel() {
        let option = AgoraRtcChannelMediaOptions()
        option.publishAudioTrack = .of(false)
        option.publishAvatarTrack = .of(true)
        option.clientRoleType = .of((Int32)(AgoraClientRole.broadcaster.rawValue))
        option.autoSubscribeVideo = .of(true)
        option.autoSubscribeAudio = .of(true)
        
        guard let engine = agoraKit else {
            fatalError("agoraKit must not nil")
        }
        engine.delegate = self
        engine.enableLocalAudio(false)
        let connection = AgoraRtcConnection()
        connection.channelId = info.roomId
        connection.localUid = UserInfo.userId
        self.rtcConnetcion = connection
        let result = engine.joinChannelEx(byToken: KeyCenter.token,
                                          connection: connection,
                                          delegate: self,
                                          mediaOptions: option,
                                          joinSuccess: nil)
        if result != 0 {
            LogUtils.log(message: "joinChannelEx fail: \(result)", level: .error)
        }
        else {
            LogUtils.log(message: "joinChannelEx success: \(info.roomId)", level: .info)
        }
    }
    
    func closeRtc() {
        agoraKit?.leaveChannelEx(rtcConnetcion!, leaveChannelBlock: { state in
            LogUtils.log(message: "\(state)", level: .info)
        })
//        AgoraRtcEngineKit.destroy()
    }
    
    func resetRenderView(member: Member) {
        let uid = UInt(member.userId)!
        if member.userId == UserInfo.uid {
            avatarEngineWapper.setupLocalVideoCanvas(view: nil)
            LogUtils.log(message: "resetRenderView local \(member.userId)", level: .info)
        }
        else {
            guard let engine = agoraKit else {
                return
            }
            let canvas = AgoraRtcVideoCanvas()
            canvas.view = nil
            canvas.renderMode = .hidden
            canvas.uid = uid
            let ret = engine.setupRemoteVideoEx(canvas, connection: rtcConnetcion!)
            avatarEngineWapper.setupRemoteVideoCanvas(view: nil, uid: uid, connection: rtcConnetcion!)
            agoraKit?.muteRemoteVideoStream(uid, mute: true)
            LogUtils.log(message: "resetRenderView remote \(member.userId) \(ret)", level: .info)
        }
    }
    
    func setRenderView(info: VideoCell.Info,
                       renderView: UIView) {
        
        if info.userId == UserInfo.uid {
            avatarEngineWapper.setupLocalVideoCanvas(view: renderView)
            LogUtils.log(message: "setRenderView local \(info.userId)", level: .info)
        }
        else {
            guard let engine = agoraKit else {
                return
            }
            let uid = UInt(info.userId)!
            let canvas = AgoraRtcVideoCanvas()
            canvas.view = renderView
            canvas.renderMode = .hidden
            canvas.uid = uid
            let ret = engine.setupRemoteVideoEx(canvas, connection: rtcConnetcion!)
            agoraKit?.muteRemoteVideoStream(uid, mute: false)
            LogUtils.log(message: "setRenderView remote \(uid) ret:\(ret))", level: .info)
        }
    }
    
    func openAudio(open: Bool) {
        agoraKit?.enableLocalAudio(open)
    }
    
    func updateVideoConfig(videoInfo: VideoSetInfo) {
        let videoConfig = AgoraVideoEncoderConfiguration(size: videoInfo.resolution.size,
                                                         frameRate: videoInfo.fremeRate.rtcType,
                                                         bitrate: videoInfo.bitRate,
                                                         orientationMode: .fixedPortrait,
                                                         mirrorMode: .auto)
        if let connection = rtcConnetcion {
            agoraKit?.setVideoEncoderConfigurationEx(videoConfig, connection: connection)
        }
//        else {
//            agoraKit?.setVideoEncoderConfiguration(videoConfig)
//        }
    }
}

// MARK: - AgoraRtcEngineDelegate AvatarEngineWapperDelegate

extension LiveViewCotroller: AgoraRtcEngineDelegate, AvatarEngineWapperDelegate {
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
        
        let noti = NotiView.Info(imageName: "portrait01",
                                 title: "\(uid) ",
                                 subtitle: "Join_Live_Room".localized)
        liveView.append(noti: noti)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        LogUtils.log(message: "remote user leval: \(uid) reason \(reason)", level: .info)
        let noti = NotiView.Info(imageName: "portrait01",
                                 title: "\(uid) ",
                                 subtitle: "Leave_Live_Room".localized)
        liveView.append(noti: noti)
    }
    
    func avatarEngineWapperDidRecvEvent(event: AvatarEngineWapper.Event) {
        
    }
    
    func avatarEngineWapperDidRecvDressList(list: [AvatarEngineWapper.DressInfo]) {
        
    }
    
    func avatarEngineWapperDidRecvFaceUpList(list: [AvatarEngineWapper.FaceUpInfo]) {
        
    }
}

// MARK: - Info extension

extension VideoSettingSheetVC.FremeRate {
    var rtcType: AgoraVideoFrameRate {
        return AgoraVideoFrameRate(rawValue: rawValue)!
    }
}

extension VideoSettingSheetVC.Resolution {
    var size: CGSize {
        switch self {
        case .v320x240:
            return .init(width: 320, height: 240)
        case .v480x360:
            return .init(width: 480, height: 360)
        case .v640x360:
            return .init(width: 640, height: 360)
        case .v640x480:
            return .init(width: 640, height: 480)
        case .v960x549:
            return .init(width: 960, height: 549)
        case .v960x720:
            return .init(width: 960, height: 720)
        case .v1280x720:
            return .init(width: 1280, height: 720)
        }
    }
}
