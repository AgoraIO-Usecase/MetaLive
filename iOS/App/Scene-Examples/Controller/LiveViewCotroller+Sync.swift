//
//  LiveViewCotroller+Handle.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/10.
//

import Foundation
import AgoraSyncManager

extension LiveViewCotroller {
    func joinSync() {
        if entryType == .fromCrateRoom {
            RoomManager.createAndJoin(info: info) { [weak self](members) in
                self?.update(members: members)
                self?.subscribeEventSync()
                LogUtils.log(message: "createAndJoin success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
        else {
            RoomManager.join(roomId: info.roomId) { [weak self](members) in
                self?.update(members: members)
                self?.subscribeEventSync()
                LogUtils.log(message: "join success", level: .info)
            } fail: { [weak self](error) in
                self?.showHUDError(error: error.description)
            }
        }
    }
    
    func subscribeEventSync() {
        if entryType == .fromJoinRoom {
            RoomManager.subscribeRoomDelete(roomId: info.roomId, onDeleted: showBeCloseAlert)
        }
        
        RoomManager.subscribeMember(roomId: info.roomId,
                                    onUpdated: addOrUpdate(_:),
                                    onDeleted: remove(_:),
                                    fail: { [weak self](error) in
            self?.showHUDError(error: error.description)
        })
    }
    
    func sendHandsupSync() { /** 举手 **/
        guard let objId = RoomManager.currentMemberId, let member = infos.first(where: { $0.isMe })?.member else {
            return
        }
        var temp = member
        temp.status = .inviting
        showWaitHUD(title: "举手中")
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: objId,
                                 member: temp) { [weak self] in
            self?.hideHUD()
            self?.showHUD(title: "举手成功", duration: 3)
        } fail: { [weak self](error) in
            self?.hideHUD()
            self?.showHUDError(error: error.description)
        }
    }
    
    func setMic() { /** 开麦关麦 **/
        guard let objId = RoomManager.currentMemberId, let member = infos.first(where: { $0.isMe })?.member else {
            return
        }
        let state = !openMic
        var temp = member
        temp.hasAudio = state
        showWaitHUD()
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: objId,
                                 member: temp) { [weak self] in
            self?.openMic = state
            self?.openAudio(open: state)
            self?.hideHUD()
        } fail: { [weak self](error) in
            self?.showHUDError(error: error.description)
        }
    }
    
    func getMembers(success: RoomManager.SuccessBlockMembers?,
                    fail: FailBlock?) {
        RoomManager.getMembers(roomId: info.roomId,
                               success: success,
                               fail: fail)
    }
    
    func updateMember(member: Member, action: HandsUpCell.Action) {
        var member = member
        switch action {
        case .reject:
            member.status = .refuse
            break
        case .dowm:
            member.status = .end
            break
        case .up:
            member.status = .accept
            break
        }
        RoomManager.updateMember(roomId: info.roomId,
                                 objectId: member.objectId,
                                 member: member,
                                 success: {
        }, fail: { [weak self](error) in
            self?.showHUDError(error: error.localizedDescription)
        })
    }
    
    func closeSync() {
        let roomId = info.roomId
        RoomManager.unsubscribeMember(roomId: roomId)
        RoomManager.unsubscribeRoomDelete(roomId: roomId)
        entryType == .fromCrateRoom ? RoomManager.deleteRoom(roomId: roomId) : RoomManager.leaveRoom(roomId: roomId)
    }
}

extension LiveViewCotroller { /** Handle member update **/
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
    
    func addOrUpdate(_ member: Member) {
        let info = Info(member: member)
        handleAddOrUpdateEvent(info: info)
        addOrUpdate(info)
        updateView()
    }
    
    private func handleAddOrUpdateEvent(info: Info) {
        let oldInfo = infos.first(where: { info.userId == $0.userId })
        
        switch entryType! {
        case .fromCrateRoom:
            if info.member.status == .inviting { /** 有房客举手了 **/
                showHUD(title: "举手了(\(info.userId))", duration: 2)
            }
            break
        case .fromJoinRoom:
            if info.userId == UserInfo.uid { /** 是本人更新 **/
                if info.member.status == .accept{ /** 房客举手被接受 **/
                    showHUD(title: "房主接受您的举手", duration: 2)
                }
                
                if info.member.status == .refuse{ /** 房客举手被拒绝 **/
                    showHUD(title: "房主拒绝您的举手", duration: 2)
                }
                
                if info.member.status == .end,
                   let old = oldInfo,
                   old.hasVideo { /** 房客被移出麦位 **/
                    showHUD(title: "房主移出您的麦位", duration: 4)
                }
            }
            break
        }
        
        if info.member.status == .end { /** 移除渲染 **/
            removeRenderView(member: info.member)
        }
    }
    
    private func addOrUpdate(_ info: Info) {
        for i in 0..<infos.count {
            if infos[i].userId == info.userId { /** update info **/
                infos[i] = info
                return
            }
        }
        
        /// add info
        infos.append(info)
    }
}
