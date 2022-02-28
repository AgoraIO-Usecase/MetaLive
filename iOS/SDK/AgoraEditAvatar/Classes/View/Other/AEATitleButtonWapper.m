//
//  AEATitleButtonWapper.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/17.
//

#import "AEATitleButtonWapper.h"
#import "UIColor+AEA.h"

@interface AEATitleButtonWapper ()

@property (nonatomic, strong)UIButton *button;
@property (nonatomic, strong)UIView *inddicatedView;

@end

@implementation AEATitleButtonWapper

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        _button = [UIButton new];
        _inddicatedView = [UIView new];
        [self setupUI];
        [self commonInit];
    }
    return self;
}

- (void)setupUI {
    _button.titleLabel.font = [UIFont systemFontOfSize:14];
    
    _inddicatedView.backgroundColor = [UIColor colorWithHexColorString:@"6F57EB"];
    _inddicatedView.layer.masksToBounds = YES;
    _inddicatedView.layer.cornerRadius = 1.5;
    [_inddicatedView setHidden:YES];
    
    [self addSubview:_button];
    [self addSubview:_inddicatedView];
    
    _button.translatesAutoresizingMaskIntoConstraints = NO;
    _inddicatedView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[_button.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_button.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    [[_button.topAnchor constraintEqualToAnchor:self.topAnchor] setActive:YES];
    [[_button.heightAnchor constraintEqualToConstant:45] setActive:YES];
    
    [[_inddicatedView.widthAnchor constraintEqualToConstant:6] setActive:YES];
    [[_inddicatedView.heightAnchor constraintEqualToConstant:2] setActive:YES];
    [[_inddicatedView.centerXAnchor constraintEqualToAnchor:self.centerXAnchor] setActive:YES];
    [[_inddicatedView.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-5] setActive:YES];
}

- (void)commonInit {
    [_button addTarget:self
                action:@selector(buttonTap:)
      forControlEvents:UIControlEventTouchUpInside];
}

- (void)setTitle:(NSString *)title {
    [_button setTitle:title forState:UIControlStateNormal];
    [_button setTitle:title forState:UIControlStateSelected];
    [_button setTitleColor:[UIColor colorWithHexColorString:@"BFC1C4"] forState:UIControlStateNormal];
    [_button setTitleColor:[UIColor colorWithHexColorString:@"6F57EB"] forState:UIControlStateSelected];
}

- (void)setSelected:(BOOL)selected {
    [_button setSelected:selected];
    [_inddicatedView setHidden:!selected];
}

- (void)buttonTap:(UIButton *)sender {
    if ([_delegate respondsToSelector:@selector(titleButtonWapperDidTap:)]) {
        [_delegate titleButtonWapperDidTap:self];
    }
}

@end
