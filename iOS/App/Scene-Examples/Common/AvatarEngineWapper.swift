//
//  AvatarEngineWapper.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/14.
//


import AgoraRtcKit

protocol AvatarEngineWapperDelegate: NSObjectProtocol {
    func avatarEngineWapperDidRecvEvent(event: AvatarEngineWapper.Event)
    func avatarEngineWapperDidRecvDressList(list: [AvatarEngineWapper.DressInfo])
    func avatarEngineWapperDidRecvFaceUpList(list: [AvatarEngineWapper.FaceUpInfo])
}

class AvatarEngineWapper: NSObject {
    let engine: AvatarEngineProtocol
    weak var delegate: AvatarEngineWapperDelegate?
    var dressInfos: [AvatarEngineWapper.DressInfo]?
    var faceUpInfos: [AvatarEngineWapper.FaceUpInfo]?
    var didAvatarLoadSuccess = false
    var hasStartInit = false
    
    deinit {
        LogUtils.log(message: "AvatarEngineWapper deinit", level: .info)
    }
    
    init(engine: AvatarEngineProtocol) {
        self.engine = engine
        super.init()
    }
    
    func startInit() {
        hasStartInit = true
        let context = AgoraAvatarContext()
        context.aiAppId = KeyCenter.cocosAppId
        context.aiLicense = KeyCenter.cocosAppLicense
        let ret = engine.initialize(context)
        if ret != 0 {
            LogUtils.log(message: "initialize fail \(ret)", level: .info)
        }
        let avatarConfigs = AgoraAvatarConfigs()
        avatarConfigs.mode = .avatar
        avatarConfigs.mediaSource = .primaryCamera
        avatarConfigs.enable_face_detection = 1
        avatarConfigs.enable_human_detection = 0
        engine.enableOrUpdateLocalAvatarVideo(true, configs: avatarConfigs)
    }
    
    func setupLocalVideoCanvas(view: UIView?) {
        /// 渲染
        let canvas = AgoraRtcVideoCanvas()
        canvas.uid = 0
        canvas.renderMode = .hidden
        canvas.view = view
        engine.setupLocalVideoCanvas(canvas)
    }
    
//    func setupRemoteVideoCanvas(view: UIView?,
//                                uid: UInt,
//                                connection: AgoraRtcConnection) {
//        /// 渲染
//        let canvas = AgoraRtcVideoCanvas()
//        canvas.uid = uid
//        canvas.renderMode = .hidden
//        canvas.view = view
//        engine.setupRemoteVideoCanvas(canvas, connection: connection)
//    }
    
    func startDressUp() {
        guard didAvatarLoadSuccess else { return }
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("start_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions start_dress fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "startDressUp success", level: .info)
        }
    }
    
    func stopDressUp() {
        guard didAvatarLoadSuccess else { return }
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("stop_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions stop_dress fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "stopDressUp success", level: .info)
        }
    }
    
    func requestDressUpList() {
        guard didAvatarLoadSuccess else { return }        
        var rerult: NSString?
        var ret: Int32 = 0
        ret = engine.getLocalUserAvatarOptions("request_dresslist", args: "", result: &rerult)
        if ret != 0 {
            LogUtils.log(message: "getLocalUserAvatarOptions request_dresslist fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "requestDressUpList success", level: .info)
        }
    }
    
    func startFaceUp() {
        guard didAvatarLoadSuccess else { return }
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("start_faceedit", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions startFaceUp fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "startFaceUp success", level: .info)
        }
    }
    
    func stopFaceUp() {
        guard didAvatarLoadSuccess else { return }
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("stop_faceedit", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions stopFaceUp fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "stopFaceUp success", level: .info)
        }
    }
    
    func requestFaceUpList() {
        guard didAvatarLoadSuccess else { return }
        if let infos = faceUpInfos {
            invokeAvatarEngineWapperDidRecvFaceUpList(list: infos)
            return
        }
        var rerult: NSString?
        var ret: Int32 = 0
        ret = engine.getLocalUserAvatarOptions("request_felist", args: "", result: &rerult)
        if ret != 0 {
            LogUtils.log(message: "getLocalUserAvatarOptions requestFaceUpList fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "requestFaceUpList success",
                         level: .info)
        }
    }
    
    func updateFaceUp(id: String, value: Float) {
        guard didAvatarLoadSuccess else { return }
        let dict = [id : value]
        let encoder = JSONEncoder()
        let data = try! encoder.encode(dict)
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("send_felist",
                                               value: data)
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions updateFaceUp fail \(ret)", level: .info)
        }
        else {
            let str = String(data: data, encoding: .utf8)!
            LogUtils.log(message: "updateFaceUp sucess send_felist: \(str)", level: .info)
        }
    }
    
    func updateDerssUp(type: String, id: String) {
        guard didAvatarLoadSuccess else { return }
        var dict = ["type" : type]
        let encoder = JSONEncoder()
        var data = try! encoder.encode(dict)
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("send_showview",
                                               value: data)
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions updateDerssUp send_showview fail \(ret)", level: .info)
        }
        else {
            let str = String(data: data, encoding: .utf8)!
            LogUtils.log(message: "updateDerssUp type sucess send_showview: \(str)", level: .info)
        }
        
        if id == "0" { /** 移除 **/
            dict = ["type" : type]
            data = try! encoder.encode(dict)
            ret = engine.setLocalUserAvatarOptions("send_takeoff",
                                                   value: data)
            if ret != 0 {
                LogUtils.log(message: "setLocalUserAvatarOptions updateDerssUp send_takeoff fail \(ret)", level: .info)
            }
            else {
                let str = String(data: data, encoding: .utf8)!
                LogUtils.log(message: "updateDerssUp value sucess send_takeoff: \(str)", level: .info)
            }
        }
        else {
            dict = ["id" : id]
            data = try! encoder.encode(dict)
            ret = engine.setLocalUserAvatarOptions("send_dress",
                                                   value: data)
            if ret != 0 {
                LogUtils.log(message: "setLocalUserAvatarOptions updateDerssUp send_dress fail \(ret)", level: .info)
            }
            else {
                let str = String(data: data, encoding: .utf8)!
                LogUtils.log(message: "updateDerssUp value sucess send_dress: \(str)", level: .info)
            }
        }
    }
    
    func setQuality(quality: VideoSettingSheetVC.RenderQuality) {
        guard didAvatarLoadSuccess else { return }
        let value = "\(quality.rawValue)".data(using: .utf8)!
        let ret = engine.setLocalUserAvatarOptions("set_quality", value: value)
        if ret != 0 {
            LogUtils.log(message: "setQuality fail \(ret)", level: .info)
        }
        else {
            LogUtils.log(message: "setQuality success",
                         level: .info)
        }
    }
    
    func setDress() { /** 穿衣服 **/
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("start_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "start_dress fail \(ret)", level: .error)
        }
        
        var dict = ["type" : "73"]
        let encoder = JSONEncoder()
        var data = Data()
        
        data = try! encoder.encode(dict)
        ret = engine.setLocalUserAvatarOptions("send_showview", value: data)
        if ret != 0 {
            LogUtils.log(message: "send_showview fail \(ret)", level: .error)
        }
        
        dict = ["id" : "73001"]
        data = try! encoder.encode(dict)
        ret = engine.setLocalUserAvatarOptions("send_dress", value: data)
        if ret != 0 {
            LogUtils.log(message: "send_dress fail \(ret)", level: .error)
        }
        
        ret = engine.setLocalUserAvatarOptions("stop_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "stop_dress fail \(ret)", level: .error)
        }
    }
}

extension AvatarEngineWapper: AgoraAvatarEngineEventDelegate {
    func onLocalUserAvatarStarted(_ success: Bool, errorCode: Int, msg: String?) {
        LogUtils.log(message: "onLocalUserAvatarStarted \(errorCode) \(msg ?? "")", level: .info)
    }
    
    func onLocalUserAvatarEvent(_ key: String?, buf: Data?) {
        LogUtils.log(message: "onLocalUserAvatarEvent \(key ?? "") ", level: .info)
        guard let key = key else {
            return
        }
        if let event = Event(rawValue: key) {
            if event == .avatarSetSuccess {
                didAvatarLoadSuccess = true
            }
            invokeAvatarEngineWapperDidRecvEvent(event: event)
            return
        }
        
        if key == "request_dresslist", let data = buf {
            if let string = String(data: data, encoding: .utf8) {
                let infos = parseDress(str: string)
                invokeAvatarEngineWapperDidRecvDressList(list: infos)
            }
            return
        }
        
        if key == "request_felist", let data = buf {
            if let string = String(data: data, encoding: .utf8) {
                let infos = parseFaceUp(str: string)
                invokeAvatarEngineWapperDidRecvFaceUpList(list: infos)
            }
            return
        }
    }
    
    func onLocalUserAvatarError(_ errorCode: Int, msg: String?) {
        LogUtils.log(message: "onLocalUserAvatarError \(errorCode) \(msg ?? "")", level: .info)
    }
    
    fileprivate func parseDress(str: String) -> [DressInfo] {
        let dict = JSONObject.toDictionary(jsonString: str)
        var infoxs = [AvatarEngineWapper.DressInfo]()
        for dic in dict {
            guard let intValue = Int(dic.key),
                  let type = AvatarEngineWapper.DressType(rawValue: intValue) else {
                      continue
                  }
            guard type != .shangZhuang,
                  type != .xiaZhuang,
                  type != .xieZi,
                  type != .peiShi,
                  type != .lianYiQun else { /** 去掉这些类型 **/
                      continue
                  }
            
            var items = [AvatarEngineWapper.DressItem]()
            if let itemDicts = dic.value as? [[String : Any]] {
                if let num = Int(dic.key) {
                    if num >= 60, num != 66 { /** 可以脱装 **/
                        items.append(.takeOffItem)
                    }
                }
                
                for itemDict in itemDicts {
                    if let item = JSONObject.toModel(AvatarEngineWapper.DressItem.self,
                                                     value: itemDict) {
                        items.append(item)
                    }
                }
                items = items.sorted(by: { (lsh, rsh) in
                    return Int(lsh.id)! < Int(rsh.id)!
                })
                let info = AvatarEngineWapper.DressInfo(type: type,
                                                        name: type.name,
                                                        items: items)
                infoxs.append(info)
            }
        }
        infoxs = infoxs.sorted { lsh, rsh in
            return lsh.type.rawValue < rsh.type.rawValue
        }
        return infoxs
    }
    
    func parseFaceUp(str: String) -> [FaceUpInfo] {
        guard let dict = JSONObject.toDictionary(jsonString: str) as? [String : Float] else {
            return []
        }
        var infos = [FaceUpInfo]()
        for dic in dict {
            guard let (firstName, lastName) = FaceUpHelper.getName(id: dic.key) else {
                continue
            }
            
            let value = dic.value
            let item = FaceUpItem(id: dic.key,
                                  title: lastName,
                                  value: value)
            
            var isAdd = false
            for index in 0..<infos.count { /** update **/
                if infos[index].title == firstName {
                    infos[index].items.append(item)
                    isAdd = true
                    break
                }
            }
            
            if !isAdd { /** add **/
                let info = FaceUpInfo(title: firstName, items: [item])
                infos.append(info)
            }
        }
        
        return infos
    }
}

extension AvatarEngineWapper {
    fileprivate func invokeAvatarEngineWapperDidRecvEvent(event: Event) {
        if Thread.isMainThread {
            delegate?.avatarEngineWapperDidRecvEvent(event: event)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.delegate?.avatarEngineWapperDidRecvEvent(event: event)
        }
    }
    
    fileprivate func invokeAvatarEngineWapperDidRecvDressList(list: [AvatarEngineWapper.DressInfo]) {
        dressInfos = list
        if Thread.isMainThread {
            delegate?.avatarEngineWapperDidRecvDressList(list: list)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.delegate?.avatarEngineWapperDidRecvDressList(list: list)
        }
    }
    
    fileprivate func invokeAvatarEngineWapperDidRecvFaceUpList(list: [AvatarEngineWapper.FaceUpInfo]) {
        faceUpInfos = list
        if Thread.isMainThread {
            delegate?.avatarEngineWapperDidRecvFaceUpList(list: list)
            return
        }
        
        DispatchQueue.main.async { [weak self] in
            self?.delegate?.avatarEngineWapperDidRecvFaceUpList(list: list)
        }
    }
}

