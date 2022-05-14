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
    
    init(engine: AvatarEngineProtocol) {
        self.engine = engine
        super.init()
    }
    
    func startDressUp() {
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("start_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions start_dress fail \(ret)", level: .info)
        }
    }
    
    func stopDressUp() {
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("stop_dress", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions stop_dress fail \(ret)", level: .info)
        }
    }
    
    func requestDressUpList() {
        if let infos = dressInfos {
            invokeAvatarEngineWapperDidRecvDressList(list: infos)
            return
        }
        
        var rerult: NSString?
        var ret: Int32 = 0
        ret = engine.getLocalUserAvatarOptions("request_dresslist", args: "", result: &rerult)
        if ret != 0 {
            LogUtils.log(message: "getLocalUserAvatarOptions request_dresslist fail \(ret)", level: .info)
        }
    }
    
    func startFaceUp() {
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("start_faceedit", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions startFaceUp fail \(ret)", level: .info)
        }
    }
    
    func stopFaceUp() {
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("stop_faceedit", value: Data())
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions stopFaceUp fail \(ret)", level: .info)
        }
    }
    
    func requestFaceUpList() {
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
    }
    
    func updateFaceUp(id: String, value: Float) {
        let dict = [id : value]
        let encoder = JSONEncoder()
        let data = try! encoder.encode(dict)
        var ret: Int32 = 0
        ret = engine.setLocalUserAvatarOptions("send_felist",
                                         value: data)
        if ret != 0 {
            LogUtils.log(message: "setLocalUserAvatarOptions updateFaceUp fail \(ret)", level: .info)
        }
    }
    
    func setQuality(quality: VideoSettingSheetVC.RenderQuality) {
        let value = "\(quality.rawValue)".data(using: .utf8)!
        let ret = engine.setLocalUserAvatarOptions("set_quality", value: value)
        if ret != 0 {
            LogUtils.log(message: "setQuality fail \(ret)", level: .info)
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
            
            var items = [AvatarEngineWapper.DressItem]()
            if let itemDicts = dic.value as? [[String : Any]] {
                for itemDict in itemDicts {
                    if let item = JSONObject.toModel(AvatarEngineWapper.DressItem.self,
                                                     value: itemDict) {
                        items.append(item)
                    }
                    
                }
                let info = AvatarEngineWapper.DressInfo(type: type,
                                                        name: type.name,
                                                        items: items)
                infoxs.append(info)
            }
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

