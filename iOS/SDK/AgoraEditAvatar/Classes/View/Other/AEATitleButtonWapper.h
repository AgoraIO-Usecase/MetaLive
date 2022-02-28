//
//  AEATitleButtonWapper.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/17.
//

#import <UIKit/UIKit.h>
@class AEATitleButtonWapper;

NS_ASSUME_NONNULL_BEGIN

@protocol AEATitleButtonWapperDelegate <NSObject>

- (void)titleButtonWapperDidTap:(AEATitleButtonWapper *)wapper;

@end

@interface AEATitleButtonWapper : UIView

- (void)setTitle:(NSString *)title;
- (void)setSelected:(BOOL)selected;

@property (nonatomic, weak)id<AEATitleButtonWapperDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
