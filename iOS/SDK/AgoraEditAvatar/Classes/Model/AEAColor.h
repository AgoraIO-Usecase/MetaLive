//
//  AEAColor.h
//  Scene-Examples
//
//  Created by ZYP on 2022/3/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface AEAColor : NSObject

@property (nonatomic, assign) double r;
@property (nonatomic, assign) double b;
@property (nonatomic, assign) double g;
@property (nonatomic, assign) double intensity;
@property (nonatomic, assign) NSInteger index;

- (UIColor *)getColor;

@end

NS_ASSUME_NONNULL_END
