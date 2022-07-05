//
//  AEAColorButtonWapper.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/17.
//

#import <UIKit/UIKit.h>
@class AEAColorButtonWapper;

NS_ASSUME_NONNULL_BEGIN

@protocol AEAColorButtonWapperDelegate <NSObject>

- (void)colorButtonWapperDidTap:(AEAColorButtonWapper *)wapper;

@end

@interface AEAColorButtonWapper : UIView

@property (nonatomic, weak)id<AEAColorButtonWapperDelegate> delegate;
- (void)setColor:(UIColor *)color;
- (void)setSelected:(BOOL)selected;

@end

NS_ASSUME_NONNULL_END
