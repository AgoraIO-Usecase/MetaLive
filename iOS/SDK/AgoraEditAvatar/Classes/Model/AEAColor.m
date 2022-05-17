//
//  AEAColor.m
//  Scene-Examples
//
//  Created by ZYP on 2022/3/16.
//

#import "AEAColor.h"

@interface AEAColor ()

@end

@implementation AEAColor

- (UIColor *)getColor {
    return [UIColor colorWithRed:self.r / 255.0  green:self.g / 255.0 blue:self.b / 255.0  alpha:1.0];
}

- (id)copyWithZone:(NSZone *)zone {
    AEAColor *info = [[AEAColor allocWithZone:zone] init];
    info.r = _r;
    info.g = _g;
    info.b = _b;
    info.intensity = _intensity;
    info.index = _index;
    return info;
}

@end
