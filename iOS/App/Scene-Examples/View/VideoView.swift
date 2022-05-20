//
//  VideoView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//

import UIKit

protocol VideoViewDelegate: NSObjectProtocol {
    func videoViewShouldRender(info: VideoCell.Info, renderView: UIView)
}

class VideoView: UIView {
    var collectionView: UICollectionView!
    var infos = [VideoCell.Info]()
    weak var delegate: VideoViewDelegate?
    private static let space: CGFloat = 10
    private static let itemWidth: CGFloat = UIScreen.main.bounds.size.width/2 - 2*space
    private static let itemHeight: CGFloat = itemWidth + 23
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        let layout = UICollectionViewFlowLayout()
        layout.itemSize = CGSize(width: VideoView.itemWidth, height: VideoView.itemHeight)
        layout.sectionInset = .init(top: VideoView.space,
                                    left: VideoView.space,
                                    bottom: VideoView.space,
                                    right: VideoView.space)
        layout.minimumLineSpacing = VideoView.space
        layout.minimumInteritemSpacing = VideoView.space
        collectionView = UICollectionView(frame: .zero, collectionViewLayout: layout)
        setup()
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        collectionView.backgroundColor = .white
        addSubview(collectionView)
        
        let height = (VideoView.itemHeight * 2) + 3*VideoView.space
        collectionView.translatesAutoresizingMaskIntoConstraints = false
        collectionView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        collectionView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        collectionView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        collectionView.heightAnchor.constraint(equalToConstant: height ).isActive = true
    }
    
    private func commonInit() {
        collectionView.register(VideoCell.self, forCellWithReuseIdentifier: "VideoCell")
        collectionView.dataSource = self
    }
    
    func update(infos: [VideoCell.Info]) {
        if infos.count > 4 {
            fatalError()
        }
        self.infos = infos + [VideoCell.Info].init(repeating: .empty, count: 4-infos.count)
        collectionView.reloadData()
    }
}

extension VideoView: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return infos.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "VideoCell", for: indexPath) as! VideoCell
        let info = infos[indexPath.row]
        cell.update(info: info)
        if info.hasVideo {
            LogUtils.log(message: "需要渲染 \(indexPath.row)",
                         level: .info)
            delegate?.videoViewShouldRender(info: info,
                                            renderView: cell.renderView)
        }
        return cell
    }
}


