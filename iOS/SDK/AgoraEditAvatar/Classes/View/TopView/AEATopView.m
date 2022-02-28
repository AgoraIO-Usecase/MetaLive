//
//  AEATopView.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import "AEATopView.h"
#import "UIColor+AEA.h"
#import "UIImage+AEA.h"

@interface AEATopView ()

@property (nonatomic, strong)UIButton *saveButton;
@property (nonatomic, strong)UIButton *quitButton;

@end

@implementation AEATopView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        _saveButton = [UIButton new];
        _quitButton = [UIButton new];
        [self setupUI];
        [self commonInit];
    }
    return self;
}

- (void)setupUI {
    self.backgroundColor = [UIColor colorWithHexColorString:@"FAF9FF"];
    _saveButton.backgroundColor = [UIColor colorWithHexColorString:@"6F57EB"];
    [_saveButton setTitle:@"保存" forState:UIControlStateNormal];
    [_saveButton setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
    
    [_quitButton setImage:[UIImage aeaImageName:@"icon-quit"] forState:UIControlStateNormal];
    
    [self addSubview:_saveButton];
    [self addSubview:_quitButton];
    _saveButton.translatesAutoresizingMaskIntoConstraints = NO;
    _quitButton.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[_saveButton.rightAnchor constraintEqualToAnchor:self.rightAnchor constant:-20] setActive:YES];
    [[_saveButton.widthAnchor constraintEqualToConstant:60] setActive:YES];
    [[_saveButton.heightAnchor constraintEqualToConstant:40] setActive:YES];
    [[_saveButton.bottomAnchor constraintEqualToAnchor:self.bottomAnchor constant:-55] setActive:YES];
    
    [[_quitButton.leftAnchor constraintEqualToAnchor:self.leftAnchor constant:10] setActive:YES];
    [[_quitButton.topAnchor constraintEqualToAnchor:self.safeAreaLayoutGuide.topAnchor constant:15] setActive:YES];
    
    _saveButton.layer.cornerRadius = 19.5;
}

- (void)commonInit {
    [_saveButton addTarget:self
                    action:@selector(buttonTap:)
          forControlEvents:UIControlEventTouchUpInside];
    [_quitButton addTarget:self
                    action:@selector(buttonTap:)
          forControlEvents:UIControlEventTouchUpInside];
}

- (void)buttonTap:(UIButton *)btn {
    
    if (btn == _saveButton) {
        if ([_delegate respondsToSelector:@selector(aeaTopViewDidTapSaveButton:)]) {
            [_delegate aeaTopViewDidTapSaveButton:self];
        }
        return;
    }
    
    if (btn == _quitButton) {
        if ([_delegate respondsToSelector:@selector(aeaTopViewDidTapQuitButton:)]) {
            [_delegate aeaTopViewDidTapQuitButton:self];
        }
        return;
    }
}

@end
