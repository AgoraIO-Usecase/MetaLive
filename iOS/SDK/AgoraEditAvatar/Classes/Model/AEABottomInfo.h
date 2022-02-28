//
//  AEABottomInfo.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/18.
//

#import <Foundation/Foundation.h>
@class AEABottomInfoItem;

typedef NS_ENUM(NSUInteger, AEABottomItemSizeType) {
    AEABottomItemSizeTypeSmall,
    AEABottomItemSizeTypeBig
};

NS_ASSUME_NONNULL_BEGIN

@interface AEABottomInfo : NSObject<NSCopying>

@property (nonatomic, copy)NSString *title;
@property (nonatomic, copy)NSArray<UIColor *> *colors;
@property (nonatomic, copy)NSArray<AEABottomInfoItem *> *items;
@property (nonatomic, assign)NSInteger selectedItemIndex;
@property (nonatomic, assign)NSInteger selectedColorIndex;
@property (nonatomic, assign)AEABottomItemSizeType itemSizeType;

- (id)copyWithZone:(nullable NSZone *)zone;

@end

@interface AEABottomInfoItem : NSObject<NSCopying>

@property (nonatomic, copy)NSString *imageName;

- (id)copyWithZone:(nullable NSZone *)zone;

@end

NS_ASSUME_NONNULL_END
