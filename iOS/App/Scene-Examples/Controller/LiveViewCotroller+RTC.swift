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
            
            agoraKit = AgoraRtcEngineKit.sharedEngine(with: config, delegate: nil)
            agoraKit?.setClientRole(.broadcaster)
            agoraKit?.enableVideo()
            agoraKit?.setDefaultAudioRouteToSpeakerphone(true)
            joinChannel()
            return
        }
        
        /** from create vc **/
        joinChannel()
    }
    
    func joinChannel() {
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
    
    func removeRenderView(member: Member) {
        let canvas = AgoraRtcVideoCanvas()
        canvas.view = nil
        canvas.uid = UInt(member.userId)!
        if member.userId == UserInfo.uid {
            agoraKit?.setupLocalVideo(canvas)
            LogUtils.log(message: "removeRenderView local \(member.userId)", level: .info)
        }
        else {
            agoraKit?.setupRemoteVideo(canvas)
            LogUtils.log(message: "removeRenderView remote \(member.userId)", level: .info)
        }
    }
    
    func openAudio(open: Bool) {
        agoraKit?.enableLocalAudio(open)
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
