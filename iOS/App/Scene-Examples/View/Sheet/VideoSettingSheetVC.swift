//
//  VideoSettingSheetVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/11.
//

import Foundation
import Presentr

protocol VideoSettingSheetVCDelegate: NSObjectProtocol {
    func videoSettingSheetVCDidTap(type: VideoSettingSheetVC.InfoType, value: Int)
    func videoSettingSheetVCDidValueChange(value: Int)
}

class VideoSettingSheetVC: UIViewController {
    private var presenter: Presentr?
    private var contentView: VideoSettingView!
    fileprivate var dataList = [Info]()
    fileprivate var videoInfo: VideoSetInfo!
    
    weak var delegate: VideoSettingSheetVCDelegate?
    
    init(videoInfo: VideoSetInfo) {
        self.videoInfo = videoInfo
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        contentView = VideoSettingView()
        setup()
        commonInit()
    }
    
    private func setup() {
        view.addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        contentView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        contentView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        contentView.heightAnchor.constraint(equalToConstant: VideoSettingView.getHeight).isActive = true
    }
    
    private func commonInit() {
        contentView.tableView.dataSource = self
        contentView.tableView.delegate = self
        contentView.slider.addTarget(self, action: #selector(valueChange(_:)), for: .valueChanged)
        
        dataList = [Info(infoType: .resolution, value: Resolution.v640x480.rawValue),
                    Info(infoType: .fremeRate, value: FremeRate.fps30.rawValue),
                    Info(infoType: .renderQuality, value: RenderQuality.high.rawValue)]
        contentView.tableView.reloadData()
    }
    
    func show(in vc: UIViewController) {
        let p = CGPoint(x: 0, y: Int(Float(UIScreen.main.bounds.height - VideoSettingView.getHeight)))
        presenter = Presentr(presentationType: .custom(width: .full,
                                                       height: .custom(size: Float(HandsUpView.getHeight)),
                                                       center: .customOrigin(origin: p)))
        vc.customPresentViewController(presenter!, viewController: self, animated: true, completion: nil)
    }
    
    fileprivate func showAlert(type: InfoType) {
        let vc = UIAlertController(title: type.description, message: nil, preferredStyle: .actionSheet)
        
        switch type {
        case .resolution:
            for val in Resolution.allCases {
                vc.addAction(.init(title: val.description, style: .default, handler: { [weak self](_) in
                    self?.udpate(type: type, value: val.rawValue)
                }))
            }
            break
        case .fremeRate:
            for val in FremeRate.allCases {
                vc.addAction(.init(title: val.description, style: .default, handler: { [weak self](_) in
                    self?.udpate(type: type, value: val.rawValue)
                }))
            }
            break
        case .renderQuality:
            for val in RenderQuality.allCases {
                vc.addAction(.init(title: val.description, style: .default, handler: { [weak self](_) in
                    self?.udpate(type: type, value: val.rawValue)
                }))
            }
            break
        }
        
        vc.addAction(.init(title: "取消", style: .cancel, handler: nil))
        present(vc, animated: true, completion: nil)
    }
    
    private func udpate(type: InfoType, value: Int) {
        delegate?.videoSettingSheetVCDidTap(type: type, value: value)
        switch type {
        case .resolution:
            dataList[0] = Info(infoType: .resolution, value: value)
            break
        case .fremeRate:
            dataList[1] = Info(infoType: .fremeRate, value: value)
            break
        case .renderQuality:
            dataList[2] = Info(infoType: .renderQuality, value: value)
            break
        }
        contentView.tableView.reloadData()
    }
    
    @objc func valueChange(_ sender: UISlider) {
        contentView.nameLabel.text = "码率  \(Int(sender.value)) kps"
        delegate?.videoSettingSheetVCDidValueChange(value: Int(sender.value))
    }
}



extension VideoSettingSheetVC: UITableViewDataSource, UITableViewDelegate {
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataList.count
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = UITableViewCell(style: .value1, reuseIdentifier: "cell")
        let info = dataList[indexPath.row]
        cell.textLabel?.text = info.title
        cell.detailTextLabel?.text = info.detail
        cell.accessoryType = .disclosureIndicator
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let info = dataList[indexPath.row]
        showAlert(type: info.infoType)
    }
}

class VideoSettingView: UIView {
    let tableView = UITableView()
    let titleLabel = UILabel()
    let slider = UISlider()
    var nameLabel = UILabel()
    
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
        titleLabel.text = "设置"
        tableView.separatorStyle = .none
        nameLabel.text = "码率 700"
        slider.minimumValue = 0
        slider.maximumValue = 2000
        slider.value = 700
        
        addSubview(titleLabel)
        addSubview(tableView)
        addSubview(nameLabel)
        addSubview(slider)
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        tableView.translatesAutoresizingMaskIntoConstraints = false
        nameLabel.translatesAutoresizingMaskIntoConstraints = false
        slider.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 22).isActive = true
        
        tableView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 10).isActive = true
        tableView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        tableView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        tableView.heightAnchor.constraint(equalToConstant: 181).isActive = true
        
        nameLabel.leftAnchor.constraint(equalTo: leftAnchor, constant: 16).isActive = true
        nameLabel.topAnchor.constraint(equalTo: tableView.bottomAnchor, constant: 13).isActive = true
        
        slider.leftAnchor.constraint(equalTo: leftAnchor, constant: 16).isActive = true
        slider.rightAnchor.constraint(equalTo: rightAnchor, constant: -16).isActive = true
        slider.topAnchor.constraint(equalTo: nameLabel.bottomAnchor, constant: 10).isActive = true
    }
    
    static var getHeight: CGFloat {
        return 398
    }
}

extension VideoSettingSheetVC {
    struct Info {
        let infoType: InfoType
        let value: Int
        
        var title: String {
            return infoType.description
        }
        
        var detail: String {
            switch infoType {
            case .resolution:
                return Resolution(rawValue: value)!.description
            case .fremeRate:
                return FremeRate(rawValue: value)!.description
            case .renderQuality:
                return RenderQuality(rawValue: value)!.description
            }
        }
    }
    
    enum InfoType: CustomStringConvertible {
        case resolution
        case fremeRate
        case renderQuality
        
        var description: String {
            switch self {
            case .resolution:
                return "分辨率"
            case .fremeRate:
                return "帧率"
            case .renderQuality:
                return "RenderQuality"
            }
        }
    }
    
    enum Resolution: Int, CustomStringConvertible, CaseIterable {
        case v320x240
        case v480x360
        case v640x360
        case v640x480
        case v960x549
        case v960x720
        case v1280x720
        
        var description: String {
            switch self {
            case .v320x240:
                return "320x240"
            case .v480x360:
                return "480x360"
            case .v640x360:
                return "640x360"
            case .v640x480:
                return "640x480"
            case .v960x549:
                return "960x549"
            case .v960x720:
                return "960x720"
            case .v1280x720:
                return "1280x720"
            }
        }
    }
    
    enum FremeRate: Int, CustomStringConvertible, CaseIterable {
        case fps15 = 15
        case fps24 = 24
        case fps30 = 30
        case fps60 = 60
        
        var description: String {
            return "\(rawValue)"
        }
    }
    
    enum RenderQuality: Int, CustomStringConvertible, CaseIterable {
        case high
        case Medium
        case low
        case ultra
        
        var description: String {
            switch self {
            case .high:
                return "high"
            case .Medium:
                return "Medium"
            case .low:
                return "low"
            case .ultra:
                return "ultra"
            }
        }
    }
}
