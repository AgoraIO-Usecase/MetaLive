//
//  PinchFaceSheetVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/12.
//

import Foundation
import AgoraEditAvatar
import Presentr

public class PinchFaceSheetVC: UIViewController {
    typealias Info = PinchFaceView.Info
    private var presenter: Presentr?
    private let contentView = PinchFaceView()
    weak var delegate: HandsUpSheetVCDelegate?
    var infos: [Info]!
    
    init(infos: [Info]) {
        super.init(nibName: nil, bundle: nil)
        self.infos = infos
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
        contentView.heightAnchor.constraint(equalToConstant: PinchFaceView.getHeight).isActive = true
    }
    
    private func commonInit() {
        
    }
    
    func show(in vc: UIViewController) {
        let p = CGPoint(x: 0, y: Int(Float(UIScreen.main.bounds.height - PinchFaceView.getHeight)))
        presenter = Presentr(presentationType: .custom(width: .full,
                                                       height: .custom(size: Float(PinchFaceView.getHeight)),
                                                       center: .customOrigin(origin: p)))
        vc.customPresentViewController(presenter!, viewController: self, animated: true, completion: nil)
    }
}



class PinchFaceView: UIView {
    let titleLabel = UILabel()
    let nameLabel = UILabel()
    let slider = UISlider()
    let valueLabel = UILabel()
    let titleView1 = AEATitleView(infos: [])
    let titleView2 = AEATitleView(infos: [])
    var dataList = [Info]()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
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
        titleLabel.text = "捏脸"
        nameLabel.font = .systemFont(ofSize: 14)
        nameLabel.text = "下巴前后"
        nameLabel.textColor = .gray
        valueLabel.font = .systemFont(ofSize: 14)
        valueLabel.text = "0.5"
        valueLabel.textColor = .gray
    
        addSubview(nameLabel)
        addSubview(slider)
        addSubview(valueLabel)
        addSubview(titleLabel)
        addSubview(titleView1)
        addSubview(titleView2)
        
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        slider.translatesAutoresizingMaskIntoConstraints = false
        valueLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        titleView1.translatesAutoresizingMaskIntoConstraints = false
        titleView2.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 22).isActive = true
        
        nameLabel.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 20).isActive = true
        nameLabel.leftAnchor.constraint(equalTo: leftAnchor, constant: 10).isActive = true
        
        slider.centerYAnchor.constraint(equalTo: nameLabel.centerYAnchor).isActive = true
        slider.leftAnchor.constraint(equalTo: nameLabel.rightAnchor, constant: 15).isActive = true
        slider.rightAnchor.constraint(equalTo: valueLabel.leftAnchor, constant: -15).isActive = true
        
        valueLabel.rightAnchor.constraint(equalTo: rightAnchor, constant: -10).isActive = true
        valueLabel.centerYAnchor.constraint(equalTo: nameLabel.centerYAnchor).isActive = true
        
        titleView1.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 10).isActive = true
        titleView1.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        titleView1.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        titleView1.heightAnchor.constraint(equalToConstant: 45).isActive = true
        
        titleView2.topAnchor.constraint(equalTo: titleView1.bottomAnchor, constant: 10).isActive = true
        titleView2.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        titleView2.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        titleView2.heightAnchor.constraint(equalToConstant: 45).isActive = true
    }
    
    private func commonInit() {
        titleView1.delegate = self
        titleView2.delegate = self
    }
    
    func update(infos: [Info]) {
        dataList = infos
        
    }
    
    static var getHeight: CGFloat {
        return UIScreen.main.bounds.size.height * 1/3 + 40
    }
}

extension PinchFaceView: AEATitleBarViewDelegate {
    public func editAvatarTitleBarView(_ view: AEATitleView, didSelectedAt index: Int) {
        if view == titleView1 {
            
        }
        else {
            
        }
    }
}

extension PinchFaceView {
    struct Info {
        let title: String
        let items: [Item]
    }
    
    struct Item {
        let title: String
        let value: Int
    }
}