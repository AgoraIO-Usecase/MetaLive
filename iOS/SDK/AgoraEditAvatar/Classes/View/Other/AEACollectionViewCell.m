//
//  AEACollectionViewCell.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/19.
//

#import "AEACollectionViewCell.h"
#import "UIColor+AEA.h"

@interface AEACollectionViewCell ()

@property (nonatomic, strong)UIImageView *imageView;
@property (nonatomic, strong)UIView *selectedView;
@property (nonatomic, assign)BOOL selectedFlag;

@end

@implementation AEACollectionViewCell

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        [self setupUI];
        [self setSelected:NO];
    }
    return self;
}

- (void)setupUI {
    _imageView = [UIImageView new];
    _selectedView = [UIView new];
    
    _selectedView.backgroundColor = UIColor.clearColor;
    [_selectedView.layer setBorderColor:[UIColor colorWithHexColorString:@"6F57EB"].CGColor];
    [_selectedView.layer setBorderWidth:2];
    [_selectedView.layer setCornerRadius:10];
    [_selectedView setClipsToBounds:YES];
    
    [self.contentView addSubview:_selectedView];
    [self.contentView addSubview:_imageView];
    
    _selectedView.translatesAutoresizingMaskIntoConstraints = NO;
    _imageView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[_selectedView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor] setActive:YES];
    [[_selectedView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor] setActive:YES];
    [[_selectedView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor] setActive:YES];
    [[_selectedView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor] setActive:YES];
    
    [[_imageView.leftAnchor constraintEqualToAnchor:self.contentView.leftAnchor  constant:5] setActive:YES];
    [[_imageView.rightAnchor constraintEqualToAnchor:self.contentView.rightAnchor constant:-5] setActive:YES];
    [[_imageView.topAnchor constraintEqualToAnchor:self.contentView.topAnchor constant:5] setActive:YES];
    [[_imageView.bottomAnchor constraintEqualToAnchor:self.contentView.bottomAnchor constant:-5] setActive:YES];
}

- (void)setSelectedFlag:(BOOL)selected {
    _selectedFlag = selected;
    [_selectedView setHidden:!selected];
}

- (void)setImageName:(NSString *)name {
    _imageView.image = [UIImage imageNamed:name];
}

@end
