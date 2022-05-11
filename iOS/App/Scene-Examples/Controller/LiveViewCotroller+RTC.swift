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
            agoraKit?.disableAudio()
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
}
