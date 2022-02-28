//
//  AEACollectionViewCell.h
//  Scene-Examples
//
//  Created by ZYP on 2022/1/19.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface AEACollectionViewCell : UICollectionViewCell

- (void)setSelectedFlag:(BOOL)selected;
- (void)setImageName:(NSString *)name;

@end

NS_ASSUME_NONNULL_END
