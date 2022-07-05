//
//  AgoraEditBottomView.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import <UIKit/UIKit.h>
@class AEATitleView, AEABottomView;
@class AEAColorSelectedView;
@class AEABottomInfo;

@protocol AEABottomViewDelegate <NSObject>

- (void)bottomView:(AEABottomView *_Nonnull)view
didSelectedItemIndex:(NSInteger)index
            atInfo:(AEABottomInfo * _Nonnull)info;

- (void)bottomView:(AEABottomView *_Nonnull)view
didSelectedColorIndex:(NSInteger)index
atInfo:(AEABottomInfo * _Nonnull)info;

@end

NS_ASSUME_NONNULL_BEGIN

@interface AEABottomView : UIView

@property (nonatomic, weak)id<AEABottomViewDelegate> delegate;
@property (nonatomic, strong)AEATitleView *titleView;
@property (nonatomic, strong)AEAColorSelectedView *colorSelectedView;
- (instancetype)initWithTitleInfos:(NSArray<AEABottomInfo *> *)infos;

@end

NS_ASSUME_NONNULL_END
