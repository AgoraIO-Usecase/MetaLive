//
//  AEAColorSelectedView.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/16.
//

#import "AEAColorSelectedView.h"
#import "AEAColorButtonWapper.h"

@interface AEAColorSelectedView ()<AEAColorButtonWapperDelegate>
@property (nonatomic, strong)UIScrollView *scrollView;

@property (nonatomic, copy)NSArray<UIColor *> *colors;
@property (nonatomic, assign)NSInteger selectedIndex;

@property (nonatomic, assign)CGFloat buttonWidth;
@property (nonatomic, assign)CGFloat buttonHeight;
@property (nonatomic, assign)CGFloat buttonGap;

@property (nonatomic, strong)AEAColorButtonWapper *selectedButton;
@property (nonatomic, copy)NSArray<AEAColorButtonWapper *> *buttons;
@end

@implementation AEAColorSelectedView

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
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
}

- (void)setColors:(NSArray<UIColor *> *)colors {
    _colors = colors;
    
    /// remove button
    for (AEAColorButtonWapper *btn in _buttons) {
        [btn removeFromSuperview];
    }
    
    /// set button in scrollView
    /// _scrollView.contentSize
    CGFloat screenWidth = UIScreen.mainScreen.bounds.size.width;
    CGFloat buttonsWidth = _colors.count * (_buttonWidth + _buttonGap) + _buttonGap;
    CGFloat contentWidth = MAX(screenWidth, buttonsWidth);
    _scrollView.contentSize = CGSizeMake(contentWidth, _buttonHeight);
    
    /// buttons init
    AEAColorButtonWapper *currentButton;
    NSMutableArray *array = @[].mutableCopy;
    for (NSInteger i = 0; i < _colors.count; i++) {
        /// init
        UIColor *color = _colors[i];
        AEAColorButtonWapper *button = [AEAColorButtonWapper new];
        button.tag = i;
        [button setColor:color];
        [_scrollView addSubview:button];
        
        /// layout
        button.frame = CGRectMake(i * (_buttonWidth + _buttonGap), 0, _buttonWidth, _buttonHeight);
        
        /// set index 0 for selected
        if (currentButton == nil) {
            _selectedButton = button;
            [button setSelected:YES];
        }
        currentButton = button;
        [array addObject:button];
        /// observer event
        button.delegate = self;
    }
    
    _buttons = array.copy;
}

- (void)configSelectedIndex:(NSInteger)selectedIndex {
    if (_buttons.count == 0 || selectedIndex < 0 || selectedIndex > _buttons.count - 1) { return; }
    _selectedIndex = selectedIndex;
    
    [_selectedButton setSelected:NO];
    _selectedButton = _buttons[selectedIndex];
    [_selectedButton setSelected:YES];
}

#pragma mark - AEAColorButtonWapperDelegate

- (void)colorButtonWapperDidTap:(AEAColorButtonWapper *)wapper {
    [self configSelectedIndex:wapper.tag];
    
    SEL selector = @selector(colorSelectedBarView:didSelectedAtIndex:);
    if ([_delegate respondsToSelector:selector]) {
        [_delegate colorSelectedBarView:self
                     didSelectedAtIndex:wapper.tag];
    }
}

@end
