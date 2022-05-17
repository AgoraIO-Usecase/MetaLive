//
//  Infos.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/6.
//

import Foundation

struct LiveRoomInfo: Codable {
    let roomName: String
    let roomId: String
    let userId: String
    var roomType = "2"
}

struct Member: Codable {
    var objectId: String
    let avatar: String
    let userId: String
    let userName: String
    var status: Status
    var hasAudio: Bool
    var hasVideo: Bool = false
}

enum Status: Int, Codable {
    // 举手中
    case raising = 5
    // 已接受
    case accept = 2
    // 已拒绝
    case refuse = 3
    // 已结束
    case end = 4
    // 邀请中
    case inviting = 1
}

struct VideoSetInfo {
    var resolution: VideoSettingSheetVC.Resolution
    var fremeRate: VideoSettingSheetVC.FremeRate
    var renderQuality: VideoSettingSheetVC.RenderQuality
    var bitRate: Int
    
    static var `default`: VideoSetInfo {
        return VideoSetInfo(resolution: .v640x480,
                            fremeRate: .fps30,
                            renderQuality: .high,
                            bitRate: 700)
    }
}
