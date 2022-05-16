//
//  AvatarEngineWapper+Extension.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/14.
//

import Foundation

extension AvatarEngineWapper {
    enum Event: String {
        case avatarSetSuccess = "set_avatar_success"
        case avatarSetFail = "set_avatar_failure"
    }
    
    struct DressInfo {
        let type: DressType
        let name: String
        let items: [DressItem]
    }
    
    struct DressItem: Codable {
        let status: Int
        let zOrder: Int
        let id: String
        let isUsing: Int
        let tag: Int
        let version: Int
        let name: String
        let icon: String
        
        static var takeOffItem: DressItem {
            DressItem(status: 0,
                      zOrder: 0,
                      id: "",
                      isUsing: 0,
                      tag: 0,
                      version: 0,
                      name: "empty",
                      icon: "")
        }
        
        /// 是否是移除装
        var isTakeOffItem: Bool {
            return name == "empty"
        }
    }
    
    enum DressType: Int {
        /// 脸
        case face = 50
        /// 眼
        case eye = 51
        /// 嘴
        case mount = 52
        /// 瞳孔
        case tongKong = 53
        /// 睫毛
        case jieMao = 54
        /// 眉毛
        case meiMao = 55
        /// 腮红
        case saiHong = 60
        /// 口红
        case kouHong = 61
        /// 眼影
        case yanYing = 62
        /// 眼线
        case yanXian = 63
        /// 胡须
        case huXu = 64
        /// 面部彩绘
        case caiHui = 65
        /// 肤色
        case fuSe = 66
        /// 发型
        case faXin = 70
        /// 上装
        case shangZhuang = 71
        /// 下装
        case xiaZhuang = 72
        /// 连衣裙
        case lianYiQun = 73
        /// 鞋子
        case xieZi = 74
        /// 帽子
        case maoZi = 75
        /// 眼镜
        case yanJing = 76
        /// 配饰
        case peiShi = 77
        
        var name: String {
            switch self {
            case .face:
                return "脸型"
            case .eye:
                return "眼型"
            case .mount:
                return "嘴型"
            case .tongKong:
                return "瞳孔"
            case .jieMao:
                return "睫毛"
            case .meiMao:
                return "眉毛"
            case .saiHong:
                return "腮红"
            case .kouHong:
                return "口红"
            case .yanYing:
                return "眼影"
            case .yanXian:
                return "眼线"
            case .huXu:
                return "胡须"
            case .caiHui:
                return "脸部彩绘"
            case .fuSe:
                return "肤色"
            case .faXin:
                return "发型"
            case .shangZhuang:
                return "上装"
            case .xiaZhuang:
                return "下装"
            case .lianYiQun:
                return "连衣裙"
            case .xieZi:
                return "鞋子"
            case .maoZi:
                return "帽子"
            case .yanJing:
                return "眼镜"
            case .peiShi:
                return "配饰"
            }
        }
    }
}

extension AvatarEngineWapper {
    struct FaceUpInfo {
        let title: String
        var items: [FaceUpItem]
    }
    
    struct FaceUpItem {
        let id: String
        let title: String
        var value: Float
    }
    
}

extension AvatarEngineWapper {
    enum FaceUpHelper {
        
        static var dict: [String : String] {
            ["100101" : "额头上下", "100103" : "额头⻆度",
             "100105" : "额头⻓度", "100106" : "额头饱满",
             "100201" : "颧⻣上下", "100202" : "颧⻣前后",
             "100204" : "颧⻣宽度", "100301" : "苹果肌上下",
             "100302" : "苹果肌前后", "100304" : "苹果肌宽度",
             "100402" : "下颚⻆上下", "100403" : "下颚⻆前后",
             "100407" : "下颚⻆宽度", "100602" : "下颚上下",
             "100603" : "下颚前后", "100607" : "下颚宽度",
             "100702" : "下唇肌上下", "100703" : "下唇肌前后",
             "100707" : "下唇肌宽度", "100801" : "下巴上下",
             "100802" : "下巴前后", "100804" : "下巴宽度",
             "110101" : "眼睛整体左右", "110102" : "眼睛整体上下",
             "110103" : "眼睛整体前后", "110104" : "眼睛整体⻆度",
             "110201" : "内上眼皮左右", "110202" : "内上眼皮上下",
             "110203" : "内上眼皮前后", "110204" : "内上眼皮⻆度",
             "110205" : "内上眼皮高低", "110206" : "内上眼皮倾斜",
             "110207" : "内上眼皮⻓度", "110208" : "内上眼皮宽度",
             "110209" : "内上眼皮饱满", "110301" : "外上眼皮左右",
             "110302" : "外上眼皮上下", "110303" : "外上眼皮前后",
             "110304" : "外上眼皮⻆度", "110305" : "外上眼皮高低",
             "110306" : "外上眼皮倾斜", "110307" : "外上眼皮⻓度",
             "110309" : "外上眼皮饱满", "110502" : "内眼⻆上下",
             "110602" : "外眼⻆上下", "120101" : "眉心上下",
             "120103" : "眉心⻆度", "120105" : "眉心厚度",
             "120201" : "眉头左右", "120203" : "眉头前后",
             "120205" : "眉头高低", "120207" : "眉头宽度",
             "120301" : "眉中左右","120303" : "眉中前后",
             "120305" : "眉中高低", "120307" : "眉中宽度",
             "120401" : "眉尾左右", "120403" : "眉尾前后",
             "120405" : "眉尾高低", "120407" : "眉尾宽度",
             "130202" : "鼻梁前后", "110308" : "外上眼皮宽度",
             "110501" : "内眼⻆左右", "110601" : "外眼⻆左右",
             "110701" : "瞳孔大小", "120102" : "眉心前后",
             "120104" : "眉心⻓度", "120106" : "眉心饱满",
             "120202" : "眉头上下", "120204" : "眉头⻆度",
             "120206" : "眉头⻓度", "120208" : "眉头饱满",
             "120302" : "眉中上下", "120304" : "眉中⻆度",
             "120306" : "眉中⻓度", "120308" : "眉中饱满",
             "120402" : "眉尾上下", "120404" : "眉尾⻆度",
             "120406" : "眉尾⻓度", "120408" : "眉尾饱满",
             "130401" : "鼻头上下", "130402" : "鼻头前后",
             "140102" : "嘴巴整体前后", "140202" : "嘴⻆上下",
             "140204" : "嘴⻆⻆度", "140206" : "嘴⻆外翻",
             "140208" : "嘴⻆厚度", "140301" : "上唇两侧左右",
             "140303" : "上唇两侧前后", "140305" : "上唇两侧倾斜",
             "140307" : "上唇两侧宽度", "140309" : "上唇两侧饱满",
             "140402" : "唇珠前后", "140404" : "唇珠宽度",
             "140406" : "唇珠饱满"]
        }
        
        static func getName(id: String) -> (String, String)? {
            guard let name = FaceUpHelper.dict[id] else {
                return nil
            }
            let firstName = name[0..<name.count-2]
            let lastName = name[name.count-2..<name.count]
            return (firstName, lastName)
        }
    }
}
