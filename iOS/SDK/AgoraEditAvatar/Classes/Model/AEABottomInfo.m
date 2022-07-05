//
//  AEABottomInfo.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/18.
//

#import "AEABottomInfo.h"

@implementation AEABottomInfo

- (id)copyWithZone:(NSZone *)zone {
    AEABottomInfo *info = [[AEABottomInfo allocWithZone:zone] init];
    info.title = self.title.mutableCopy;
    info.colors = self.colors.mutableCopy;
    info.selectedItemIndex = self.selectedItemIndex;
    info.selectedColorIndex = self.selectedColorIndex;
    info.itemSizeType = self.itemSizeType;
    info.items = self.items.mutableCopy;
    return info;
}

@end

@implementation AEABottomInfoItem

- (id)copyWithZone:(NSZone *)zone {
    AEABottomInfoItem *info = [[AEABottomInfoItem allocWithZone:zone] init];
    info.imageName = self.imageName.copy;
    return info;
}

@end
