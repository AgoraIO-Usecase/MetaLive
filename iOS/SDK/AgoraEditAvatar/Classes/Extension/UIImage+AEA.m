//
//  UIImage+AEA.m
//  AgoraEditAvatar
//
//  Created by ZYP on 2022/2/25.
//

#import "UIImage+AEA.h"

@implementation UIImage (AEA)

+ (UIImage *)aeaImageName:(NSString *)name {
    return [UIImage imageNamed:name
                      inBundle:[UIImage meetingUIBundle]
 compatibleWithTraitCollection:nil];
}

+ (NSBundle *)meetingUIBundle {
    NSBundle *bundle = [NSBundle bundleForClass:[AgoraEditAvatarEmptyClass class]];
    NSString *path = [bundle pathForResource:@"AgoraEditAvatar" ofType:@"bundle"];
    return [NSBundle bundleWithPath:path];
}

@end

@implementation AgoraEditAvatarEmptyClass
@end
