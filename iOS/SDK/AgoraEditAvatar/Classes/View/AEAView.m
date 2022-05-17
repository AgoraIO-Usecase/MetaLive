//
//  AgoraEditAvatarView.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import "AEAView.h"
#import "AEABottomView.h"
#import "AEATopView.h"
#import "AEABottomInfo.h"
@interface AEAView ()

@end

@implementation AEAView

- (instancetype)initWithInfos:(NSArray<AEABottomInfo *> *)infos
{
    self = [super initWithFrame:CGRectZero];
    if (self) {
        _topView = [AEATopView new];
        _bottomView = [[AEABottomView alloc] initWithTitleInfos:infos];
        [self setupUI];
    }
    return self;
}

- (void)setupUI {
    self.backgroundColor = [UIColor whiteColor];
    [self addSubview:_topView];
    [self addSubview:_bottomView];
    
    _topView.translatesAutoresizingMaskIntoConstraints = false;
    _bottomView.translatesAutoresizingMaskIntoConstraints = false;

    CGFloat bottomViewHeight = UIScreen.mainScreen.bounds.size.height * 1/3 + 40;
    
    [[_topView.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_topView.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    [[_topView.topAnchor constraintEqualToAnchor:self.topAnchor] setActive:YES];
    [[_topView.bottomAnchor constraintEqualToAnchor:_bottomView.topAnchor constant:40] setActive:YES];
    
    [[_bottomView.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_bottomView.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    [[_bottomView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor] setActive:YES];
    [[_bottomView.heightAnchor constraintEqualToConstant:bottomViewHeight] setActive:YES];
}


@end
