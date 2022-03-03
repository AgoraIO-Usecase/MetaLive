//
//  MainViewController.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/2/25.
//

import UIKit
import AgoraEditAvatar

class MainViewController: UIViewController {
    let tableView = UITableView()
    let versionLabel = UILabel()
    let createButton = UIButton()
    
    private var infos = [Info]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        commonInit()
    }
    
    private func setupUI() {
        title = "元直播"
        
        let refreshCtrl =  UIRefreshControl()
        refreshCtrl.addTarget(self, action: #selector(pullRefreshHandler), for: .valueChanged)
        tableView.refreshControl = refreshCtrl
        tableView.separatorStyle = .none
        tableView.backgroundColor = UIColor(hex: "FAF9FF")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)
        
        versionLabel.textColor = .gray
        versionLabel.font = UIFont.systemFont(ofSize: 10)
        versionLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(versionLabel)
        
        createButton.setImage(UIImage(named: "icon-create"), for: .normal)
        createButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(createButton)
        
        tableView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        tableView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        tableView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        
        versionLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor).isActive = true
        versionLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor).isActive = true
        versionLabel.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor).isActive = true
        versionLabel.centerXAnchor.constraint(equalTo: tableView.centerXAnchor).isActive = true
        
        createButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -35).isActive = true
        createButton.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        
        let dict = Bundle.main.infoDictionary
        let version = dict!["CFBundleShortVersionString"] as! String
        let build = dict!["CFBundleVersion"] as! String
        versionLabel.text = "\(version)(\(build))"
        versionLabel.textAlignment = .center
        
        let img = UIImage(named: "icon-magic")!.withRenderingMode(.alwaysOriginal)
        let rightButton = UIBarButtonItem(image: img,
                                          style: .plain,
                                          target: self,
                                          action: #selector(editButtonTap))
        
        navigationItem.rightBarButtonItem = rightButton
    }
    
    private func commonInit() {
        tableView.register(MainCell.self, forCellReuseIdentifier: "cell")
        tableView.dataSource = self
        tableView.delegate = self
        
        let infos = [String](repeating: "最长的电影", count: 30).map({ Info(title: $0) })
        update(infos: infos)
        
        createButton.addTarget(self, action: #selector(createButtonTap), for: .touchUpInside)
    }
    
    func update(infos: [Info]) {
        self.infos = infos
        tableView.reloadData()
    }
    
    @objc func editButtonTap() {
        let infos = AEAViewController.createTestData()
        let vc = AEAViewController(infos: infos)
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
    }
    
    @objc func createButtonTap() {
        let vc = TextInputAlertVC()
        vc.show(in: self)
    }
    
    @objc func pullRefreshHandler() {
        tableView.refreshControl?.endRefreshing()
    }
}

extension MainViewController: UITableViewDataSource, UITableViewDelegate {
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return infos.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath) as! MainCell
        let info = infos[indexPath.row]
        cell.set(title: info.title)
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 144+15
    }
}

extension MainViewController {
    struct Info {
        let title: String
    }
}
