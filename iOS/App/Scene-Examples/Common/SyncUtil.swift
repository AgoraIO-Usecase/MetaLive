//
//  SyncUtil.swift
//  BreakoutRoom
//
//  Created by zhaoyongqiang on 2021/11/3.
//

import UIKit
import AgoraSyncManager

class SyncUtil: NSObject {
    private static var manager: AgoraSyncManager?
    private override init() { }
    private static var sceneRefs: [String: SceneReference] = [String: SceneReference]()
    
    static func initSyncManager(sceneId: String, complete: @escaping SuccessBlockInt) {
        let config = AgoraSyncManager.RtmConfig(appId: KeyCenter.appId, channelName: sceneId)
        manager = AgoraSyncManager(config: config, complete: complete)
    }
    
    class func createScene(id: String,
                           userId: String,
                           property: [String: Any]?,
                           success: SuccessBlockVoid? = nil,
                           fail: FailBlock? = nil) {
        guard let manager = manager else { return }
        let jsonString = JSONObject.toJsonString(dict: property) ?? ""
        let params = JSONObject.toDictionary(jsonStr: jsonString)
        let scene = Scene(id: id, userId: userId, property: params)
        manager.createScene(scene: scene,
                            success: success,
                            fail: fail)
    }
    
    class func joinScene(id: String,
                         success: SuccessBlockVoid? = nil,
                         fail: FailBlock? = nil) {
        guard let manager = manager else { return }
        manager.joinScene(sceneId: id, success: { (ref) in
            sceneRefs[id] = ref
            success?()
        }, fail: fail)
    }
    
    class func fetchAll(success: SuccessBlock? = nil, fail: FailBlock? = nil) {
        manager?.getScenes(success: success, fail: fail)
    }
    
    class func fetch(id: String, key: String?, success: SuccessBlockObjOptional? = nil, fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        let key = key ?? "info"
        sceneRef?.get(key: key, success: success, fail: fail)
    }
    
    class func update(id: String,
                      key: String?,
                      params: [String: Any],
                      success: SuccessBlock? = nil,
                      fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        let key = key ?? "info"
        sceneRef?.update(key: key, data: params, success: success, fail: fail)
    }
    
    class func subscribe(id: String,
                         key: String?,
                         onCreated: OnSubscribeBlock? = nil,
                         onUpdated: OnSubscribeBlock? = nil,
                         onDeleted: OnSubscribeBlock? = nil,
                         onSubscribed: OnSubscribeBlockVoid? = nil,
                         fail: FailBlock? = nil) {
        let key = key ?? "info"
        let sceneRef = sceneRefs[id]
        sceneRef?.subscribe(key: key,
                            onCreated: onCreated,
                            onUpdated: onUpdated,
                            onDeleted: onDeleted,
                            onSubscribed: onSubscribed,
                            fail: fail)
    }
    
    class func subscribeScene(id: String,
                              onDeleted: OnSubscribeBlockVoid?,
                              fail: FailBlock?) {
        let sceneRef = sceneRefs[id]
        sceneRef?.subscribeScene(onDeleted: onDeleted, fail: fail)
    }
    
    class func unsubscribeScene(id: String, fail: FailBlock?) {
        let sceneRef = sceneRefs[id]
        sceneRef?.unsubscribeScene(fail: fail)
    }
    
    class func unsubscribe(id: String, key: String?) {
        let key = key ?? "info"
        let sceneRef = sceneRefs[id]
        sceneRef?.unsubscribe(key: key)
    }
    
    class func delete(id: String, success: SuccessBlock? = nil, fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        sceneRef?.delete(success: success, fail: fail)
    }
    
    class func fetchCollection(id: String,
                               className: String,
                               success: SuccessBlock? = nil,
                               fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        sceneRef?.collection(className: className).get(success: success, fail: fail)
    }
    
    class func addCollection(id: String,
                             className: String,
                             params: [String: Any],
                             success: SuccessBlockObj? = nil,
                             fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        sceneRef?.collection(className: className).add(data: params, success: success, fail: fail)
    }
    
    class func updateCollection(id: String,
                                className: String,
                                objectId: String,
                                params: [String: Any],
                                success: SuccessBlockVoid? = nil,
                                fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        sceneRef?.collection(className: className).update(id: objectId, data: params, success: success, fail: fail)
    }
    
    class func subscribeCollection(id: String,
                                   className: String,
                                   documentId: String? = nil,
                                   onCreated: OnSubscribeBlock? = nil,
                                   onUpdated: OnSubscribeBlock? = nil,
                                   onDeleted: OnSubscribeBlock? = nil,
                                   onSubscribed: OnSubscribeBlockVoid? = nil,
                                   fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        if documentId == nil {
            sceneRef?.collection(className: className)
                .document()
                .subscribe(key: "",
                           onCreated: onCreated,
                           onUpdated: onUpdated,
                           onDeleted: onDeleted,
                           onSubscribed: onSubscribed,
                           fail: fail)
        } else {
            sceneRef?.collection(className: className)
                .document(id: documentId ?? "")
                .subscribe(key: "",
                           onCreated: onCreated,
                           onUpdated: onUpdated,
                           onDeleted: onDeleted,
                           onSubscribed: onSubscribed,
                           fail: fail)
        }
    }
    
    class func unsubscribeCollection(id: String, className: String) {
        let sceneRef = sceneRefs[id]
        sceneRef?.collection(className: className).document().unsubscribe(key: "")
    }
    
    class func leaveScene(id: String) {
        sceneRefs.removeValue(forKey: id)
    }
    
    class func deleteDocument(id: String,
                              className: String,
                              objectId: String?,
                              success: SuccessBlockVoid? = nil,
                              fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        /// 删除单条数据
        sceneRef?.collection(className: className).delete(id: objectId ?? "", success: success, fail: fail)
    }
    
    class func deleteCollection(id: String,
                                className: String,
                                success: SuccessBlock? = nil,
                                fail: FailBlock? = nil) {
        let sceneRef = sceneRefs[id]
        /// 删除数据
        sceneRef?.collection(className: className).delete(success: success, fail: fail)
    }
}
