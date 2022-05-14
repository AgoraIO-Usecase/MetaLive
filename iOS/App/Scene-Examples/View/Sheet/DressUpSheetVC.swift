//
//  DressUpSheetVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/12.
//

import UIKit
import AgoraEditAvatar
import Presentr

protocol DressUpSheetVCDelegate: NSObjectProtocol {
    func dressUpSheetVCDidSelectedItem(index: Int, info: AEABottomInfo)
}

public class DressUpSheetVC: UIViewController {
    typealias Info = AEABottomInfo
    private var presenter: Presentr?
    private var contentView: DressUpView!
    private var dataList = [Info]()
    weak var delegate: DressUpSheetVCDelegate?
    
    init(infos: [Info]) {
        super.init(nibName: nil, bundle: nil)
        dataList = infos
        contentView = DressUpView(infos: infos)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        setup()
        commonInit()
    }
    
    private func setup() {
        view.addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        contentView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        contentView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        contentView.heightAnchor.constraint(equalToConstant: DressUpView.getHeight).isActive = true
    }
    
    private func commonInit() {
        contentView.bottomView.delegate = self
    }
    
    func show(in vc: UIViewController) {
        let p = CGPoint(x: 0, y: Int(Float(UIScreen.main.bounds.height - DressUpView.getHeight)))
        presenter = Presentr(presentationType: .custom(width: .full,
                                                       height: .custom(size: Float(DressUpView.getHeight)),
                                                       center: .customOrigin(origin: p)))
        vc.customPresentViewController(presenter!, viewController: self, animated: true, completion: nil)
    }
}

extension DressUpSheetVC: AEABottomViewDelegate {
    public func bottomView(_ view: AEABottomView, didSelectedItemIndex index: Int, at info: AEABottomInfo) {
        delegate?.dressUpSheetVCDidSelectedItem(index: index, info: info)
    }
    
    public func bottomView(_ view: AEABottomView, didSelectedColorIndex index: Int, at info: AEABottomInfo) {}
}

class DressUpView: UIView {
    let titleLabel = UILabel()
    var bottomView: AEABottomView!
    
    init(infos: [AEABottomInfo]) {
        self.bottomView = AEABottomView(titleInfos: infos)
        super.init(frame: .zero)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        layer.cornerRadius = 40
        layer.masksToBounds = true
        backgroundColor = .white
        titleLabel.font = .systemFont(ofSize: 17)
        titleLabel.text = "换装"
        
        
        addSubview(titleLabel)
        addSubview(bottomView)
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        bottomView.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 22).isActive = true
        
        bottomView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 10).isActive = true
        bottomView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        bottomView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        bottomView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        
    }
    
    static var getHeight: CGFloat {
        return UIScreen.main.bounds.size.height * 1/3 + 40
    }
}
