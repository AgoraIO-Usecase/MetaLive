//
//  Info+Extension.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/14.
//

import Foundation
import AgoraEditAvatar

extension DressUpSheetVC.Info {
    convenience init(info: AvatarEngineWapper.DressInfo) {
        self.init()
        self.title = info.name
        self.itemSizeType = .small
        self.colors = []
        selectedItemIndex = -1
        for index in 0..<info.items.count {
            let temp = info.items[index]
            if temp.isUsing == 1 {
                selectedItemIndex = index
                break
            }
        }
        self.items = info.items.map({ .init(info: $0) })
    }
}

extension AEABottomInfoItem {
    convenience init(info: AvatarEngineWapper.DressItem) {
        self.init()
        self.imageName = info.icon
    }
}

extension PinchFaceSheetVC.Info {
    init(info: AvatarEngineWapper.FaceUpInfo) {
        self.title = info.title
        self.items = info.items.map({ .init(info: $0) })
    }
}

extension PinchFaceSheetVC.Item {
    init(info: AvatarEngineWapper.FaceUpItem) {
        self.title = info.title
        self.value = info.value
    }
}
