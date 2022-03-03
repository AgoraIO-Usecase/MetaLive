//
//  NotiView.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/3/1.
//

import UIKit

class NotiView: UIView {
    let tableView = UITableView()
    var infos = [Info]()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        tableView.estimatedRowHeight = 61
        tableView.rowHeight = UITableView.automaticDimension
        tableView.separatorStyle = .none
        tableView.translatesAutoresizingMaskIntoConstraints = false
        addSubview(tableView)
        tableView.leftAnchor.constraint(equalTo: leftAnchor).isActive = true
        tableView.rightAnchor.constraint(equalTo: rightAnchor).isActive = true
        tableView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        tableView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
    }
    
    private func commonInit() {
        tableView.register(NotiCell.self, forCellReuseIdentifier: "cell")
        tableView.dataSource = self
        tableView.delegate = self
    }
    
    func append(info: Info) {
        infos.append(info)
        tableView.reloadData()
        let indexPath = IndexPath(row: infos.count - 1, section: 0)
        tableView.scrollToRow(at: indexPath, at: .bottom, animated: false)
    }
}

extension NotiView: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return infos.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! NotiCell
        let info = infos[indexPath.row]
        cell.set(imageName: info.imageName,
                 title: info.title,
                 subtitle: info.subtitle)
        return cell
    }
    
//    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
//        return 70
//    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        return UIView()
    }
}

extension NotiView {
    struct Info {
        let imageName: String
        let title: String
        let subtitle: String
    }
}
