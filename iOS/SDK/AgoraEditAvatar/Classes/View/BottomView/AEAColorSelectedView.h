//
//  AEAColorSelectedView.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/16.
//

#import <UIKit/UIKit.h>
@class AEAColorSelectedView;

NS_ASSUME_NONNULL_BEGIN

@protocol AEAColorSelectedViewDelegate <NSObject>

/// invoke if tap a button
-(void)colorSelectedBarView:(AEAColorSelectedView *)view
         didSelectedAtIndex:(NSInteger)index;

@end

@interface AEAColorSelectedView : UIView

@property (nonatomic, weak)id<AEAColorSelectedViewDelegate> delegate;
- (void)setColors:(NSArray<UIColor *> *)colors;
- (void)configSelectedIndex:(NSInteger)selectedIndex;

@end

NS_ASSUME_NONNULL_END
