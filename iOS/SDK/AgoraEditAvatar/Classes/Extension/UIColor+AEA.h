//
//  UIColor+AEA.h
//  AgoraEditAvatar
//
//  Created by ZYP on 2022/2/25.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIColor (AEA)

+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString;
+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString alpha:(float)alpha;

@end

NS_ASSUME_NONNULL_END
