//
//  LiveViewCotroller+Handle.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/10.
//

import Foundation

extension LiveViewCotroller {
    func joinSync() {
        if entryType == .fromCrateRoom {
            RoomManager.createAndJoin(info: info) { [weak self](members) in
                self?.handleMembersUpdate(members: members)
                self?.subscribeEvent()
                LogUtils.log(message: "createAndJoin success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
        else {
            RoomManager.join(roomId: info.roomId) { [weak self](members) in
                self?.handleMembersUpdate(members: members)
                self?.subscribeEvent()
                LogUtils.log(message: "join success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
    }
    
    func subscribeEvent() {
        if entryType == .fromJoinRoom {
            RoomManager.subscribeRoomDelete(roomId: info.roomId, onDeleted: handleRoomDelete)
        }
        
        RoomManager.subscribeMember(roomId: info.roomId,
                                    onUpdated: addOrUpdate(_:),
                                    onDeleted: remove(_:),
                                    fail: { [weak self](error) in
            self?.showHUDError(error: error.description)
        })
    }
    
    func addOrUpdate(_ member: Member) {
        let oldInfo = infos.first(where: { member.userId == $0.userId })
        if entryType == .fromCrateRoom, member.status == .inviting { /** 房主处理 **/
            showHUD(title: "举手了(\(member.userId))", duration: 4)
        }
        
        if entryType == .fromJoinRoom, member.status == .accept, member.userId == UserInfo.uid { /** 房客处理 **/
            showHUD(title: "房主接受您的举手", duration: 4)
        }
        
        if entryType == .fromJoinRoom,
           member.userId == UserInfo.uid,
           member.status == .end,
           let old = oldInfo,
           old.hasVideo { /** 房客处理 **/
            showHUD(title: "房主移出您的麦位", duration: 4)
        }
        
        if entryType == .fromJoinRoom,
           member.userId == UserInfo.uid {
            updateMicView(enable: member.hasAudio)
        }
        
        if member.status == .end {
            removeRenderView(member: member)
        }
        
        let info = Info(member: member)
        for i in 0..<infos.count {
            if infos[i].userId == member.userId { /** update info **/
                infos[i] = info
                updateView()
                return
            }
        }
        
        /// add info
        infos.append(info)
        updateView()
    }
    
    func update(members: [Member]) {
        let infos = members.map({ Info(member: $0) })
        self.infos = infos
        updateView()
    }
    
    func remove(_ member: Member) {
        infos.removeAll(where: { $0.userId == member.userId })
        removeRenderView(member: member)
        updateView()
    }
    
    func updateMicView(enable: Bool) {
        liveView.enableMic(enable: enable)
    }
}
