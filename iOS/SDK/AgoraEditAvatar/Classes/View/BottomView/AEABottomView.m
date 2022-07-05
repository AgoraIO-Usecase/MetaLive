//
//  AgoraEditBottomView.m
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import "AEABottomView.h"
#import "AEATitleView.h"
#import "AEAColorSelectedView.h"
#import "AEABottomInfo.h"
#import "AEACollectionViewCell.h"

@interface AEABottomView ()<UICollectionViewDataSource, UICollectionViewDelegate, AEATitleBarViewDelegate, AEAColorSelectedViewDelegate>

@property (nonatomic, strong)UICollectionView *collectionView;
@property (nonatomic, strong)UICollectionViewFlowLayout *horizontalLayout;
@property (nonatomic, strong)UICollectionViewFlowLayout *verticalLayout;
@property (nonatomic, copy)NSArray<AEABottomInfo *> *infos;
@property (nonatomic, strong)NSLayoutConstraint *colorSelectedViewHeightConstraint;
@property (nonatomic, assign)CGFloat colorSelectedViewHeight;
@property (nonatomic, strong)AEABottomInfo *currentInfo;
@property (nonatomic, assign)NSInteger currentSelectedIndex;
@property (nonatomic, assign)AEABottomItemSizeType itemSizeType;

@end

@implementation AEABottomView

- (instancetype)initWithTitleInfos:(NSArray<AEABottomInfo *> *)infos {
    self = [super initWithFrame:CGRectZero];
    if (self) {
        self.infos = infos;
        self.colorSelectedViewHeight = 45;
        
        /// title view
        NSMutableArray *titleInfos = [NSMutableArray new];
        for (AEABottomInfo *info in infos) {
            [titleInfos addObject:info.title];
        }
        _titleView = [[AEATitleView alloc] initWithInfos:titleInfos.copy];
        
        _colorSelectedView = [AEAColorSelectedView new];
        
        /// coloectionView
        _horizontalLayout = [UICollectionViewFlowLayout new];
        _horizontalLayout.scrollDirection = UICollectionViewScrollDirectionHorizontal;
        _horizontalLayout.itemSize = CGSizeMake(145, 145);
        _verticalLayout = [UICollectionViewFlowLayout new];
        _verticalLayout.scrollDirection = UICollectionViewScrollDirectionVertical;
        _verticalLayout.itemSize = CGSizeMake(60, 60);
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero
                                             collectionViewLayout:_verticalLayout];
        _collectionView.contentInset = UIEdgeInsetsMake(5, 5, 5, 5);
        [self setupUI];
        [self commonInit];
    }
    return self;
}

- (void)setupUI {
    [self.layer setMasksToBounds:YES];
    self.layer.cornerRadius = 40;
    self.backgroundColor = UIColor.whiteColor;
    
    _collectionView.backgroundColor = [UIColor whiteColor];
    _collectionView.showsHorizontalScrollIndicator = YES;
    
    [self addSubview:_titleView];
    [self addSubview:_colorSelectedView];
    [self addSubview:_collectionView];
    
    _titleView.translatesAutoresizingMaskIntoConstraints = NO;
    _colorSelectedView.translatesAutoresizingMaskIntoConstraints = NO;
    _collectionView.translatesAutoresizingMaskIntoConstraints = NO;
    
    [[_titleView.topAnchor constraintEqualToAnchor:self.topAnchor constant:17] setActive:YES];
    [[_titleView.leftAnchor constraintEqualToAnchor:self.leftAnchor constant:15] setActive:YES];
    [[_titleView.rightAnchor constraintEqualToAnchor:self.rightAnchor constant:-15] setActive:YES];
    [[_titleView.heightAnchor constraintEqualToConstant:45] setActive:YES];
    
    [[_colorSelectedView.topAnchor constraintEqualToAnchor:_titleView.bottomAnchor] setActive:YES];
    [[_colorSelectedView.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_colorSelectedView.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    _colorSelectedViewHeightConstraint = [_colorSelectedView.heightAnchor constraintEqualToConstant:0];
    [_colorSelectedViewHeightConstraint setActive:YES];
    
    [[_collectionView.topAnchor constraintEqualToAnchor:_colorSelectedView.bottomAnchor] setActive:YES];
    [[_collectionView.leftAnchor constraintEqualToAnchor:self.leftAnchor] setActive:YES];
    [[_collectionView.rightAnchor constraintEqualToAnchor:self.rightAnchor] setActive:YES];
    [[_collectionView.bottomAnchor constraintEqualToAnchor:self.safeAreaLayoutGuide.bottomAnchor] setActive:YES];
    
    [_collectionView registerClass:AEACollectionViewCell.class forCellWithReuseIdentifier:@"cell"];
}

- (void)commonInit {
    _itemSizeType = AEABottomItemSizeTypeBig;
    
    _collectionView.dataSource = self;
    _collectionView.delegate = self;
    _titleView.delegate = self;
    
    if (_infos.count > 0) {
        _currentInfo =  _infos[0];
        [_colorSelectedView setColors:_currentInfo.colors];
        [_colorSelectedView configSelectedIndex:_currentInfo.selectedColorIndex];
        [_collectionView reloadData];
    }
    
    _colorSelectedView.delegate = self;
}

#pragma mark -- UICollectionViewDataSource & UICollectionViewDelegate

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return _currentInfo.items.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    AEACollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"cell" forIndexPath:indexPath];
    AEABottomInfoItem *item = _currentInfo.items[indexPath.row];
    BOOL selected = _currentInfo.selectedItemIndex == indexPath.row;
    [cell setSelectedFlag:selected];
    if ([item.imageName isEqualToString:@""]) {
        [cell setImageName:@"none"];
    }
    else {
        [cell setImageUrlString:item.imageName];
    }
    return cell;
}

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    _currentInfo.selectedItemIndex = indexPath.row;
    [collectionView reloadData];
    
    if ([_delegate respondsToSelector:@selector(bottomView:didSelectedItemIndex:atInfo:)]) {
        [_delegate bottomView:self
         didSelectedItemIndex:_currentSelectedIndex
                       atInfo:_currentInfo];
    }
}

#pragma mark -- AEATitleViewDelegate
- (void)editAvatarTitleBarView:(AEATitleView *)view
            didSelectedAtIndex:(NSInteger)index {
    if (index >= _infos.count + 1 || index < 0) { return; }
    _currentSelectedIndex = index;
    /// save data
    _currentInfo = _infos[index];
    _itemSizeType = _currentInfo.itemSizeType;
    BOOL isBigType = _infos[index].itemSizeType == AEABottomItemSizeTypeBig;
    [_collectionView reloadData];
    
    /// show color bar
    BOOL showColor = _currentInfo.colors.count > 0;
    [_colorSelectedView setColors:_currentInfo.colors];
    [_colorSelectedView configSelectedIndex:_currentInfo.selectedColorIndex];
    _colorSelectedViewHeightConstraint.constant =  showColor ? _colorSelectedViewHeight :  0;
    [self layoutIfNeeded];
    
    /// size type change
    UICollectionViewFlowLayout *layout = isBigType ? _horizontalLayout : _verticalLayout;
    [_collectionView setCollectionViewLayout:layout animated:YES];
    _collectionView.showsVerticalScrollIndicator = !isBigType;
    _collectionView.showsHorizontalScrollIndicator = isBigType;
    
    /// scorll to selected index
    if (_currentInfo.selectedItemIndex >0) {
        NSInteger selectedIndex = _currentInfo.selectedItemIndex;
        NSIndexPath *indexPath = [NSIndexPath indexPathForRow:selectedIndex inSection:0];
        UICollectionViewScrollPosition position = isBigType ? UICollectionViewScrollPositionCenteredHorizontally : UICollectionViewScrollPositionCenteredVertically;
        [_collectionView scrollToItemAtIndexPath:indexPath
                                atScrollPosition:position
                                        animated:NO];
    }
}

#pragma mark -- AEAColorSelectedViewDelegate

-(void)colorSelectedBarView:(AEAColorSelectedView *)view
         didSelectedAtIndex:(NSInteger)index {
    _currentInfo.selectedColorIndex = index;
    
    if ([_delegate respondsToSelector:@selector(bottomView:didSelectedColorIndex:atInfo:)]) {
        [_delegate bottomView:self
        didSelectedColorIndex:index
                       atInfo:_currentInfo];
    }
}

@end
