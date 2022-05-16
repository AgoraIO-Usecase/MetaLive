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
            
            agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
            agoraKit?.setClientRole(.broadcaster)
            agoraKit?.setVideoEncoderConfiguration(videoConfig)
            agoraKit?.enableVideo()
            agoraKit?.setDefaultAudioRouteToSpeakerphone(true)
            
            avatarEngineWapper = CreateLiveController.createAvaterEngineWapper(agoraKit: agoraKit!)
            avatarEngineWapper.delegate = self
            avatarEngineWapper.startInit()
            joinChannel()
            return
        }
        
        /** from create vc **/
        avatarEngineWapper.delegate = self
        joinChannel()
        if avatarEngineWapper.hsaStartInit {
            avatarEngineWapper.startInit()
        }
    }
    
    func joinChannel() {
        let option = AgoraRtcChannelMediaOptions()
        option.publishAudioTrack = .of(false)
        option.publishCameraTrack = .of(true)
        option.clientRoleType = .of((Int32)(AgoraClientRole.broadcaster.rawValue))
        option.autoSubscribeVideo = .of(true)
        option.autoSubscribeAudio = .of(true)
        
        guard let engine = agoraKit else {
            fatalError("agoraKit must not nil")
        }
        engine.delegate = self
        engine.enableLocalAudio(false)
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
    
    func closeRtc() {
        agoraKit?.leaveChannel({ state in
            LogUtils.log(message: "\(state)", level: .info)
        })
        AgoraRtcEngineKit.destroy()
    }
    
    func resetRenderView(member: Member) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = nil
        canvas.uid = UInt(member.userId)!
        if member.userId == UserInfo.uid {
            agoraKit?.setupLocalVideo(canvas)
            LogUtils.log(message: "resetRenderView local \(member.userId)", level: .info)
        }
        else {
            agoraKit?.setupRemoteVideo(canvas)
            agoraKit?.muteRemoteVideoStream(canvas.uid, mute: true)
            LogUtils.log(message: "resetRenderView remote \(member.userId)", level: .info)
        }
    }
    
    func setRenderView(info: VideoCell.Info,
                       renderView: UIView) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = renderView
        canvas.renderMode = .hidden
        if info.userId == UserInfo.uid {
            canvas.uid = 0
            agoraKit?.setupLocalVideo(canvas)
            agoraKit?.startPreview()
            LogUtils.log(message: "setRenderView local \(info.userId)", level: .info)
        }
        else {
            canvas.uid = UInt(info.userId)!
            agoraKit?.setupRemoteVideo(canvas)
            agoraKit?.muteRemoteVideoStream(canvas.uid, mute: false)
            LogUtils.log(message: "setRenderView remote \(info.userId)", level: .info)
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
        agoraKit?.setVideoEncoderConfiguration(videoConfig)
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
