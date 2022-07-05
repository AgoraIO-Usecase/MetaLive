//
//  MoreSheetVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/11.
//

import Foundation
import Presentr

protocol MoreSheetVCDelegate: NSObjectProtocol {
    func moreSheetVCDidTap(action: MoreSheetVC.Action)
}

public class MoreSheetVC: UIViewController {
    enum Action {
        case dress
        case face
        case change
        case videoSet
    }
    
    struct Info {
        let title: String
        let action: Action
        
        init(action: Action) {
            switch action {
            case .dress:
                title = "换装"
                break
            case .face:
                title = "捏脸"
                break
            case .change:
                title = "切换"
                break
            case .videoSet:
                title = "设置"
                break
            }
            self.action = action
        }
    }
    private var presenter: Presentr?
    private var contentView: MoreView!
    private var infos = [Info]()
    
    weak var delegate: MoreSheetVCDelegate?
    
    init(infos: [Info]) {
        self.infos = infos
        let title1 = infos.first?.title ?? "none"
        let title2 = infos.last?.title ?? "none"
        contentView = MoreView(title1: title1, title2: title2)
        super.init(nibName: nil, bundle: nil)
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
        contentView.heightAnchor.constraint(equalToConstant: MoreView.getHeight).isActive = true
    }
    
    private func commonInit() {
        contentView.button1.addTarget(self,
                                      action: #selector(buttonTap(_:)),
                                      for: .touchUpInside)
        contentView.button2.addTarget(self,
                                      action: #selector(buttonTap(_:)),
                                      for: .touchUpInside)
    }
    
    @objc func buttonTap(_ sender: UIButton) {
        guard let action: Action = sender == contentView.button1 ? infos.first?.action : infos.last?.action else {
            return
        }
        dismiss(animated: true, completion: { [weak self] in
            self?.delegate?.moreSheetVCDidTap(action: action)
        })
    }
    
    func show(in vc: UIViewController) {
        let p = CGPoint(x: 0, y: Int(Float(UIScreen.main.bounds.height - MoreView.getHeight)))
        presenter = Presentr(presentationType: .custom(width: .full,
                                                       height: .custom(size: Float(HandsUpView.getHeight)),
                                                       center: .customOrigin(origin: p)))
        vc.customPresentViewController(presenter!, viewController: self, animated: true, completion: nil)
    }
}

class MoreView: UIView {
    fileprivate let titleLabel = UILabel()
    fileprivate let button1 = UIButton()
    fileprivate let button2 = UIButton()
    let title1: String
    let title2: String
    
    init(title1: String, title2: String) {
        self.title1 = title1
        self.title2 = title2
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
        titleLabel.text = "虚拟形象设置"
        
        button1.setImage(.init(named: "icon-setting"), for: .normal)
        button2.setImage(.init(named: "icon-setting"), for: .normal)
        let label1 = UILabel()
        label1.text = title1
        let label2 = UILabel()
        label2.text = title2
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        button1.translatesAutoresizingMaskIntoConstraints = false
        label1.translatesAutoresizingMaskIntoConstraints = false
        button2.translatesAutoresizingMaskIntoConstraints = false
        label2.translatesAutoresizingMaskIntoConstraints = false
        
        addSubview(titleLabel)
        addSubview(button1)
        addSubview(label1)
        addSubview(button2)
        addSubview(label2)
        
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 22).isActive = true
        
        button1.leftAnchor.constraint(equalTo: leftAnchor, constant: 30).isActive = true
        button1.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        label1.centerXAnchor.constraint(equalTo: button1.centerXAnchor).isActive = true
        label1.topAnchor.constraint(equalTo: button1.bottomAnchor, constant: 5).isActive = true
        
        button2.leftAnchor.constraint(equalTo: button1.rightAnchor, constant: 45).isActive = true
        button2.centerYAnchor.constraint(equalTo: centerYAnchor).isActive = true
        label2.centerXAnchor.constraint(equalTo: button2.centerXAnchor).isActive = true
        label2.topAnchor.constraint(equalTo: button2.bottomAnchor, constant: 5).isActive = true
    }
    
    static var getHeight: CGFloat {
        return 190
    }
}
