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
}

enum Status: Int, Codable {
    // 邀请中
    case inviting = 1
    // 已接受
    case accept = 2
    // 已拒绝
    case refuse = 3
    // 已结束
    case end = 4
    // 举手中
    case raising = 5
}
