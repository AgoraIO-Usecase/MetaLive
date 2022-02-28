//
//  AEAColorButtonWapper.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/17.
//

#import "AEAColorButtonWapper.h"
#import "UIColor+AEA.h"

@interface AEAColorButtonWapper ()

@property (nonatomic, strong)UIButton *button;
@property (nonatomic, strong)UIView *colorView;
@property (nonatomic, strong)UIView *selectedView;

@end

@implementation AEAColorButtonWapper

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _button = [UIButton new];
        _colorView = [UIView new];
        _selectedView = [UIView new];
        [self setupUI];
        [self commomInit];
    }
    return self;
}

- (void)setupUI {
    [_selectedView setHidden:YES];
    _selectedView.backgroundColor = UIColor.clearColor;
    [_selectedView.layer setBorderColor:[UIColor colorWithHexColorString:@"6F57EB"].CGColor];
    [_selectedView.layer setBorderWidth:3];
    [_selectedView.layer setCornerRadius:34/2];
    [_colorView.layer setCornerRadius:34/2];
    
    [self addSubview:_colorView];
    [self addSubview:_selectedView];
    [self addSubview:_button];
    
    _button.translatesAutoresizingMaskIntoConstraints = NO;
    _colorView.translatesAutoresizingMaskIntoConstraints = NO;
    _selectedView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[_button.heightAnchor constraintEqualToConstant:34] setActive:YES];
    [[_button.widthAnchor constraintEqualToConstant:34] setActive:YES];
    [[_button.centerXAnchor constraintEqualToAnchor:self.centerXAnchor] setActive:YES];
    [[_button.centerYAnchor constraintEqualToAnchor:self.centerYAnchor] setActive:YES];
    
    [[_colorView.heightAnchor constraintEqualToConstant:34] setActive:YES];
    [[_colorView.widthAnchor constraintEqualToConstant:34] setActive:YES];
    [[_colorView.centerXAnchor constraintEqualToAnchor:self.centerXAnchor] setActive:YES];
    [[_colorView.centerYAnchor constraintEqualToAnchor:self.centerYAnchor] setActive:YES];

    [[_selectedView.heightAnchor constraintEqualToConstant:34] setActive:YES];
    [[_selectedView.widthAnchor constraintEqualToConstant:34] setActive:YES];
    [[_selectedView.centerXAnchor constraintEqualToAnchor:self.centerXAnchor] setActive:YES];
    [[_selectedView.centerYAnchor constraintEqualToAnchor:self.centerYAnchor] setActive:YES];
}

- (void)commomInit {
    [_button addTarget:self
                action:@selector(buttonTap:)
      forControlEvents:UIControlEventTouchUpInside];
}

- (void)buttonTap:(UIButton *)sender {
    if ([_delegate respondsToSelector:@selector(colorButtonWapperDidTap:)]) {
        [_delegate colorButtonWapperDidTap:self];
    }
}

- (void)setColor:(UIColor *)color {
    _colorView.backgroundColor = color;
}

- (void)setSelected:(BOOL)selected {
    [_selectedView setHidden:!selected];
}


@end
