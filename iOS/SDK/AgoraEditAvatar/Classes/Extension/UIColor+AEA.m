//
//  UIColor+AEA.m
//  AgoraEditAvatar
//
//  Created by ZYP on 2022/2/25.
//

#import "UIColor+AEA.h"

@implementation UIColor (AEA)

/// 十六进制颜色
+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString {
    return [self colorWithHexColorString:hexColorString alpha:1.0f];
}

/// 十六进制颜色
+ (UIColor *)colorWithHexColorString:(NSString *)hexColorString alpha:(float)alpha{
    unsigned int red, green, blue;
    
    NSRange range;
    range.length =2;
    range.location =0;
    
    [[NSScanner scannerWithString:[hexColorString substringWithRange:range]]scanHexInt:&red];
    
    range.location =2;
    [[NSScanner scannerWithString:[hexColorString substringWithRange:range]]scanHexInt:&green];
    
    range.location =4;
    [[NSScanner scannerWithString:[hexColorString substringWithRange:range]]scanHexInt:&blue];
    
    UIColor *color = [UIColor colorWithRed:(float)(red/255.0f)green:(float)(green/255.0f)blue:(float)(blue/255.0f)alpha:alpha];
    return color;
}

@end
