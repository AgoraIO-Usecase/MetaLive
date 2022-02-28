//
//  AEATitleView.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import <UIKit/UIKit.h>
@class AEATitleView;

NS_ASSUME_NONNULL_BEGIN

@protocol AEATitleBarViewDelegate <NSObject>

/// invoke if tap a button
- (void)editAvatarTitleBarView:(AEATitleView *)view
                 didSelectedAtIndex:(NSInteger)index;

@end

@interface AEATitleView : UIView
@property (nonatomic, weak)id<AEATitleBarViewDelegate> delegate;
- (instancetype)initWithInfos:(NSArray<NSString *> *)infos;
@end

NS_ASSUME_NONNULL_END
