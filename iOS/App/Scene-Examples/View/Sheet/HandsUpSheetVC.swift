//
//  HandsUpSheetVC.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/2.
//
import UIKit
import Presentr

protocol HandsUpSheetVCDelegate: NSObjectProtocol {
    func handsUpSheetVC(vc: HandsUpSheetVC, didTap action: HandsUpCell.Action, at index: Int)
}

public class HandsUpSheetVC: UIViewController {
    typealias Info = HandsUpCell.Info
    private var presenter: Presentr?
    private var contentView: HandsUpView!
    private var dataList = [Info]()
    weak var delegate: HandsUpSheetVCDelegate?
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        contentView = HandsUpView()
        setup()
        commonInit()
    }
    
    private func setup() {
        view.addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        contentView.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        contentView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        contentView.heightAnchor.constraint(equalToConstant: HandsUpView.getHeight).isActive = true
    }
    
    private func commonInit() {
        contentView.tableView.register(HandsUpCell.self, forCellReuseIdentifier: "cell")
        contentView.tableView.dataSource = self
        contentView.tableView.delegate = self
    }
    
    func show(in vc: UIViewController, list: [Info]) {
        dataList = list
        let p = CGPoint(x: 0, y: Int(Float(UIScreen.main.bounds.height - HandsUpView.getHeight)))
        presenter = Presentr(presentationType: .custom(width: .full,
                                                       height: .custom(size: Float(HandsUpView.getHeight)),
                                                       center: .customOrigin(origin: p)))
        vc.customPresentViewController(presenter!, viewController: self, animated: true, completion: nil)
        contentView.tableView.reloadData()
    }
}

extension HandsUpSheetVC: UITableViewDataSource, UITableViewDelegate {
    public func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 60
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataList.count
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! HandsUpCell
        let info = dataList[indexPath.row]
        cell.set(info: info, indexPath: indexPath)
        cell.delegate = self
        return cell
    }
}

extension HandsUpSheetVC: HandsUpCellDelegate {
    func handsUpCell(cell: HandsUpCell, didTap action: HandsUpCell.Action) {
        delegate?.handsUpSheetVC(vc: self, didTap: action, at: cell.indexPath.row)
        dismiss(animated: true, completion: nil)
    }
}

class HandsUpView: UIView {
    let tableView = UITableView()
    let titleLabel = UILabel()
    
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
        titleLabel.text = "举手列表"
        tableView.separatorStyle = .none
        
        addSubview(titleLabel)
        addSubview(tableView)
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        tableView.translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel.centerXAnchor.constraint(equalTo: centerXAnchor).isActive = true
        titleLabel.topAnchor.constraint(equalTo: topAnchor, constant: 22).isActive = true
        
        tableView.topAnchor.constraint(equalTo: titleLabel.bottomAnchor, constant: 10).isActive = true
        tableView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        tableView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        tableView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    
    static var getHeight: CGFloat {
        return 398
    }
}
