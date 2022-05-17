//
//  AgoraEditAvatarViewController.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/15.
//

#import <UIKit/UIKit.h>
@class AEABottomInfo, AEAViewController;

@protocol AEAViewControllerDelegate <NSObject>
- (void)aeaViewController:(AEAViewController * _Nonnull )viewController
    didSelectedColorIndex:(NSInteger )index
                   atInfo:(AEABottomInfo * _Nonnull)info;
- (void)aeaViewController:(AEAViewController * _Nonnull)viewController
     didSelectedItemIndex:(NSInteger )index
                   atInfo:(AEABottomInfo * _Nonnull)info;
- (void)aeaViewControllerDidTapSaveButton:(AEAViewController * _Nonnull)viewController;
- (void)aeaViewControllerDidTapQuitButton:(AEAViewController * _Nonnull)viewController;
@end

NS_ASSUME_NONNULL_BEGIN

@interface AEAViewController : UIViewController

- (instancetype)initWithInfos:(NSArray <AEABottomInfo *>*)infos;
@property (nonatomic, weak)id<AEAViewControllerDelegate> delegate;

/// for test
+ (NSArray <AEABottomInfo *>*)createTestData;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithCoder:(NSCoder *)coder NS_UNAVAILABLE;
- (instancetype)initWithNibName:(nullable NSString *)nibNameOrNil bundle:(nullable NSBundle *)nibBundleOrNil NS_UNAVAILABLE;
@end

NS_ASSUME_NONNULL_END
