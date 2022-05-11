//
//  RoomManager.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/7.
//

import Foundation
import AgoraSyncManager

class RoomManager {
    static let defaultChannelName = "MetaLiveCocos"
    static let usersKey = "Users"
    static let queue = DispatchQueue(label: "queue.RoomManager")
    static var currentMemberId: String?
    
    typealias SuccessBlockVoid = () -> ()
    typealias SuccessBlockRoomInfo = ([LiveRoomInfo]) -> ()
    typealias SuccessBlockMembers = ([Member]) -> ()
    typealias SuccessBlockMember = (Member) -> ()
    typealias FailBlockString = (String) -> ()
    
    static func initSyncManager(success: SuccessBlockVoid?,
                                fail: FailBlockString?) {
        SyncUtil.initSyncManager(sceneId: RoomManager.defaultChannelName) { (code) in
            code == 0 ? success?() : fail?("initSyncManager fail \(code)")
        }
    }
    
    static func getRooms(success: SuccessBlockRoomInfo?,
                         fail: FailBlock?) {
        SyncUtil.fetchAll(success: { objs in
            let infos = objs.compactMap({ $0.toJson() })
                .compactMap({ JSONObject.toModel(LiveRoomInfo.self, value: $0 )})
            success?(infos)
        }, fail: fail)
    }
    
    static func create(info: LiveRoomInfo,
                       success: SuccessBlockVoid?,
                       fail: FailBlock?) {
        let params = JSONObject.toJson(info)
        SyncUtil.createScene(id: info.roomId,
                             userId: info.userId,
                             property: params,
                             success: success,
                             fail: fail)
    }
    
    static func createAndJoin(info: LiveRoomInfo,
                              success: SuccessBlockMembers?,
                              fail: FailBlock?) {
        RoomManager.queue.async {
            let result = RoomManager.createAndJoinInternal(info: info)
            switch result {
            case .success(let members):
                success?(members)
            case .failure(let error):
                fail?(error)
            }
        }
    }
    
    static func deleteRoom(roomId: String) {
        currentMemberId = nil
        SyncUtil.deleteCollection(id: roomId,
                                  className: usersKey,
                                  success: nil,
                                  fail: nil)
        SyncUtil.delete(id: roomId,
                        success: nil,
                        fail: nil)
        SyncUtil.leaveScene(id: roomId)
    }
    
    static func leaveRoom(roomId: String) {
        deleteLocalUser()
        currentMemberId = nil
        SyncUtil.leaveScene(id: roomId)
    }
    
    fileprivate static func createAndJoinInternal(info: LiveRoomInfo) -> Result<[Member], SyncError> {
        let semp = DispatchSemaphore(value: 0)
        var error: SyncError?
        
        RoomManager.create(info: info) {
            semp.signal()
        } fail: { e in
            error = e
            semp.signal()
        }
        semp.wait()
        
        if let e = error { /** create fail **/
            return .failure(e)
        }
        
        let result = RoomManager.joinInternal(roomId: info.roomId, isHost: true)
        switch result {
        case .success(let members):
            return .success(members)
        case .failure(let e):
            return .failure(e)
        }
    }
    
    static func join(roomId: String,
                     success: SuccessBlockMembers?,
                     fail: FailBlock?) {
        RoomManager.queue.async {
            let result = RoomManager.joinInternal(roomId: roomId, isHost: false)
            switch result {
            case .success(let members):
                success?(members)
            case .failure(let error):
                fail?(error)
            }
        }
    }
    
    fileprivate static func joinInternal(roomId: String, isHost: Bool) -> Result<[Member], SyncError> {
        let semp = DispatchSemaphore(value: 0)
        var error: SyncError?
        var members: [Member]?
        
        /// join
        SyncUtil.joinScene(id: roomId) {
            semp.signal()
        } fail: { e in
            error = e
            semp.signal()
        }
        semp.wait()
        
        if let err = error { /** join fail **/
            return .failure(err)
        }
        
        /// fetchCollection
        SyncUtil.fetchCollection(id: roomId,
                                 className: RoomManager.usersKey) { objs in
            let infos = objs.compactMap({ $0.toJson() })
                .compactMap({ JSONObject.toModel(Member.self, value: $0 )})
            members = infos
            semp.signal()
        } fail: { e in
            error = e
            semp.signal()
        }
        semp.wait()
        
        if let err = error { /** fetchCollection fail **/
            return .failure(err)
        }
        
        guard var members = members else {
            fatalError()
        }
        
        if let memberForMe = members.first(where: { $0.userId == UserInfo.uid }) {
            RoomManager.currentMemberId = memberForMe.objectId
        }
        else { /** add member **/
            if members.count >= 4 {
                let e = SyncError(message: "房间已满人", code: -100001)
                return .failure(e)
            }
            
            var member = Member(objectId: "",
                                avatar: "",
                                userId: UserInfo.uid,
                                userName: "User-\(UserInfo.uid)",
                                status: isHost ? .accept : .end,
                                hasAudio: false)
            var params = JSONObject.toJson(member)
            params.removeValue(forKey: "objectId")
            SyncUtil.addCollection(id: roomId,
                                   className: RoomManager.usersKey,
                                   params: params) { obj in
                let objId = obj.getId()
                RoomManager.currentMemberId = objId
                member.objectId = objId
                semp.signal()
            } fail: { e in
                error = e
                semp.signal()
            }
            semp.wait()
            
            members.append(member)
        }
        
        return .success(members)
    }
    
    static func subscribeRoomDelete(roomId: String, onDeleted: OnSubscribeBlockVoid?) {
        SyncUtil.subscribeScene(id: roomId,
                                onDeleted: onDeleted,
                                fail: nil)
    }
    
    static func unsubscribeRoomDelete(roomId: String) {
        SyncUtil.unsubscribeScene(id: roomId, fail: nil)
    }
    
    static func deleteLocalUser() {
        guard let objectId = RoomManager.currentMemberId else {
            return
        }
        SyncUtil.deleteDocument(id: defaultChannelName,
                                className: usersKey,
                                objectId: objectId,
                                success: nil,
                                fail: nil)
    }
    
    static func updateMember(roomId: String,
                             objectId: String,
                             member: Member,
                             success: SuccessBlockVoid?,
                             fail: FailBlock?) {
        let params = JSONObject.toJson(member)
        SyncUtil.updateCollection(id: roomId,
                                  className: usersKey,
                                  objectId: objectId,
                                  params: params,
                                  success: success,
                                  fail: fail)
    }
    
    static func getMembers(roomId: String, success: SuccessBlockMembers?, fail: FailBlock?) {
        SyncUtil.fetchCollection(id: roomId, className: RoomManager.usersKey, success: { objs in
            let infos = objs.compactMap({ $0.toJson() })
                .compactMap({ JSONObject.toModel(Member.self, value: $0 )})
            success?(infos)
        }, fail: fail)
    }
    
    static func subscribeMember(roomId: String,
                                onUpdated: SuccessBlockMember?,
                                onDeleted: SuccessBlockMember?,
                                fail: FailBlock?) {
        SyncUtil.subscribeCollection(id: roomId,
                                     className: usersKey,
                                     documentId: nil,
                                     onCreated: { obj in
            guard let member = JSONObject.toModel(Member.self, value: obj.toJson() ) else {
                fatalError()
            }
            onUpdated?(member)
        },onUpdated: { obj in
            guard let member = JSONObject.toModel(Member.self, value: obj.toJson() ) else {
                fatalError()
            }
            onUpdated?(member)
        },onDeleted: { obj in
            guard let member = JSONObject.toModel(Member.self, value: obj.toJson() ) else {
                fatalError()
            }
            onDeleted?(member)
        },onSubscribed: nil, fail: fail)
    }
    
    static func unsubscribeMember(roomId: String) {
        SyncUtil.unsubscribeCollection(id: roomId, className: usersKey)
    }
}
