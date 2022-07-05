//
//  AEATopView.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import <UIKit/UIKit.h>
@class AEATopView;

@protocol AEATopViewDelegate <NSObject>

- (void)aeaTopViewDidTapSaveButton:(AEATopView * _Nonnull)view;
- (void)aeaTopViewDidTapQuitButton:(AEATopView * _Nonnull)view;

@end

NS_ASSUME_NONNULL_BEGIN

@interface AEATopView : UIView

@property (nonatomic, weak)id<AEATopViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
