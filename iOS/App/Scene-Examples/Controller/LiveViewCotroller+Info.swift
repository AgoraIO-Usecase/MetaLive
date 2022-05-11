//
//  LiveViewCotroller+Info.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/11.
//

import Foundation

extension LiveViewCotroller { /** info **/
    enum EntryType {
        /// 房主进入
        case fromCrateRoom
        /// 成员进入
        case fromJoinRoom
    }
    
    struct Info: Equatable {
        let uid: UInt
        let title: String
        let userId: String
        let hasAudio: Bool
        let hasVideo: Bool
        let member: Member
        
        init(member: Member) {
            self.uid = UInt(member.userId)!
            self.title = member.userName
            self.userId = member.userId
            self.hasAudio = member.hasAudio
            self.hasVideo = member.status == .accept
            self.member = member
        }
        
        static func == (lhs: Self, rhs: Self) -> Bool {
            return lhs.uid == rhs.uid &&
            lhs.title == rhs.title &&
            lhs.userId == rhs.userId &&
            lhs.hasAudio == rhs.hasAudio &&
            lhs.hasVideo == rhs.hasVideo
        }
        
        var isMe: Bool {
            return userId == UserInfo.uid
        }
    }
}
