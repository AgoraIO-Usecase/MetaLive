//
//  AEAViewController.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import "AEAViewController.h"
#import "AEAView.h"
#import "AEABottomInfo.h"
#import "AEABottomView.h"
#import "AEATopView.h"

@interface AEAViewController ()<AEABottomViewDelegate, AEATopViewDelegate>

@property (nonatomic, strong)AEAView *mainView;
@property (nonatomic, strong)NSArray <AEABottomInfo *> *infos;

@end

@implementation AEAViewController

- (instancetype)initWithInfos:(NSArray <AEABottomInfo *>*)infos {
    if (self = [super initWithNibName:nil bundle:nil]) {
        _infos = infos;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    [self setupUI];
    [self commonInit];
}

- (void)setupUI {
    _mainView = [[AEAView alloc] initWithInfos:_infos];
    _mainView.translatesAutoresizingMaskIntoConstraints = false;
    [self.view addSubview:_mainView];
    
    [[_mainView.leftAnchor constraintEqualToAnchor:self.view.leftAnchor] setActive:true];
    [[_mainView.rightAnchor constraintEqualToAnchor:self.view.rightAnchor] setActive:true];
    [[_mainView.topAnchor constraintEqualToAnchor:self.view.topAnchor] setActive:true];
    [[_mainView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor] setActive:true];
}

- (void)commonInit {
    _mainView.topView.delegate = self;
    _mainView.bottomView.delegate = self;
}

+ (NSArray <AEABottomInfo *>*)createTestData {
    NSMutableArray *infos = [NSMutableArray new];
    
    NSArray <NSString *>*titles = @[@"形象", @"发型", @"头型", @"脸型", @"眼睛", @"眉毛", @"鼻子", @"上装", @"下装", @"套装", @"鞋子", @"配饰"];
    for (NSInteger i = 0; i<12; i++) {
        AEABottomInfo *info = [AEABottomInfo new];
        info.title = titles[i];
        
        if (i == 1 || i == 2 || i == 3 || i == 5) {
            info.colors = @[];
        }
        else {
            info.colors = @[];
        }
        
        info.itemSizeType = i == 0 ? AEABottomItemSizeTypeBig : AEABottomItemSizeTypeSmall;
        
        NSMutableArray *items = @[].mutableCopy;
        for (NSInteger j = 0; j < 30; j++) {
            AEABottomInfoItem *item = [AEABottomInfoItem new];
            item.imageName = @"kuzi_changku_1";
            [items addObject:item];
        }
        
        info.items = items.copy;
        info.selectedItemIndex = 0;
        info.selectedColorIndex = 0;
        [infos addObject:info];
    }
    
    return infos.copy;
}

#pragma mark -- AEABottomViewDelegate

- (void)bottomView:(AEABottomView *_Nonnull)view
didSelectedItemIndex:(NSInteger)index
            atInfo:(AEABottomInfo * _Nonnull)info {
    if ([_delegate respondsToSelector:@selector(aeaViewController:didSelectedItemIndex:atInfo:)]) {
        [_delegate aeaViewController:self
                didSelectedItemIndex:index
                              atInfo:info.copy];
    }
}

- (void)bottomView:(AEABottomView *_Nonnull)view
didSelectedColorIndex:(NSInteger)index
            atInfo:(AEABottomInfo * _Nonnull)info {
    if ([_delegate respondsToSelector:@selector(aeaViewController:didSelectedColorIndex:atInfo:)]) {
        [_delegate aeaViewController:self
               didSelectedColorIndex:index
                              atInfo:info.copy];
    }
}

#pragma mark -- AEATopViewDelegate

- (void)aeaTopViewDidTapSaveButton:(AEATopView * _Nonnull)view {
    if ([_delegate respondsToSelector:@selector(aeaViewControllerDidTapSaveButton:)]) {
        [_delegate aeaViewControllerDidTapSaveButton:self];
    }
}

- (void)aeaTopViewDidTapQuitButton:(AEATopView * _Nonnull)view {
    if ([_delegate respondsToSelector:@selector(aeaViewControllerDidTapQuitButton:)]) {
        [_delegate aeaViewControllerDidTapQuitButton:self];
    }
    
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end
