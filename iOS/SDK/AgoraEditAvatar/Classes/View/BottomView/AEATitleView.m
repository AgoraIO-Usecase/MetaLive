//
//  AEATitleView.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import "AEATitleView.h"
#import "AEATitleButtonWapper.h"

@interface AEATitleView ()<AEATitleButtonWapperDelegate>
@property (nonatomic, strong)UIScrollView *scrollView;

@property (nonatomic, copy)NSArray<NSString *> *infos;
@property (nonatomic, assign)NSInteger selectedIndex;

@property (nonatomic, assign)CGFloat buttonWidth;
@property (nonatomic, assign)CGFloat buttonHeight;
@property (nonatomic, assign)CGFloat buttonGap;

@property (nonatomic, strong)AEATitleButtonWapper *selectedButton;
@end

@implementation AEATitleView

- (instancetype)initWithInfos:(NSArray<NSString *> *)infos {
    self = [super initWithFrame:CGRectZero];
    if (self) {
        self.infos = infos;
        [self setupUI];
    }
    return self;
}

- (void)setupUI {
    self.buttonGap = 10;
    self.buttonWidth = 45;
    self.buttonHeight = 45;
    
    /// _scrollView
    _scrollView = [UIScrollView new];
    [self addSubview:_scrollView];
    _scrollView.translatesAutoresizingMaskIntoConstraints = NO;
    [[_scrollView.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_scrollView.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    [[_scrollView.topAnchor constraintEqualToAnchor:self.topAnchor] setActive:YES];
    [[_scrollView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor] setActive:YES];
    
    _scrollView.scrollEnabled = YES;
    _scrollView.showsVerticalScrollIndicator = NO;
    _scrollView.showsHorizontalScrollIndicator = NO;
    _scrollView.alwaysBounceHorizontal = YES;
    
    /// _scrollView.contentSize
    CGFloat screenWidth = UIScreen.mainScreen.bounds.size.width;
    CGFloat buttonsWidth = _infos.count * (_buttonWidth + _buttonGap) + _buttonGap;
    CGFloat contentWidth = MAX(screenWidth, buttonsWidth);
    _scrollView.contentSize = CGSizeMake(contentWidth, _buttonHeight);
    
    /// buttons init
    AEATitleButtonWapper *currentButton;
    for (NSInteger i = 0; i < _infos.count; i++) {
        /// init
        NSString *info = _infos[i];
        AEATitleButtonWapper *button = [AEATitleButtonWapper new];
        button.tag = i;
        [button setTitle:info];
        
        [_scrollView addSubview:button];
        
        /// layout
        button.frame = CGRectMake(i * (_buttonWidth + _buttonGap), 0, _buttonWidth, _buttonHeight);
        
        /// set index 0 for selected
        if (currentButton == nil) {
            _selectedButton = button;
            [button setSelected:YES];
        }
        currentButton = button;
        
        /// observer event
        button.delegate = self;
    }
}

#pragma mark -- AEATitleButtonWapperDelegate
- (void)titleButtonWapperDidTap:(AEATitleButtonWapper *)wapper {
    [_selectedButton setSelected:NO];
    [wapper setSelected:YES];
    _selectedButton = wapper;
    SEL selector = @selector(editAvatarTitleBarView:didSelectedAtIndex:);
    if ([_delegate respondsToSelector:selector]) {
        [_delegate editAvatarTitleBarView:self
                       didSelectedAtIndex:wapper.tag];
    }
}
@end
