//
//  MainViewController.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/2/25.
//

import UIKit
import AgoraEditAvatar
import AgoraRtcKit

class MainViewController: UIViewController {
    let tableView = UITableView()
    let versionLabel = UILabel()
    let createButton = UIButton()
    
    private var infos = [Info]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        commonInit()
        initSync()
    }
    
    private func setupUI() {
        title = "元直播"
        
        let refreshCtrl =  UIRefreshControl()
        refreshCtrl.addTarget(self, action: #selector(pullRefreshHandler), for: .valueChanged)
        tableView.refreshControl = refreshCtrl
        tableView.separatorStyle = .none
        tableView.backgroundColor = UIColor(hex: "FAF9FF")
        tableView.contentInset = .init(top: 15, left: 0, bottom: 0, right: 0)
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
    }
    
    private func commonInit() {
        tableView.register(MainCell.self, forCellReuseIdentifier: "cell")
        tableView.dataSource = self
        tableView.delegate = self
        createButton.addTarget(self, action: #selector(createButtonTap), for: .touchUpInside)
    }
    
    private func update(infos: [Info]) {
        self.infos = infos
        tableView.reloadData()
    }
    
    private func initSync() {
        RoomManager.initSyncManager { [weak self] in
            self?.loadData()
        } fail: { [weak self](str) in
            self?.showHUDError(error: str)
        }
    }
    
    private func loadData() {
        RoomManager.getRooms { [weak self](rooms) in
            let infos = rooms.map({ Info(info: $0) })
            self?.update(infos: infos)
            self?.tableView.refreshControl?.endRefreshing()
        } fail: { [weak self](error) in
            self?.showHUDError(error: error.description)
            self?.tableView.refreshControl?.endRefreshing()
        }
    }
    
    @objc func editButtonTap() {
        let infos = AEAViewController.createTestData()
        let vc = AEAViewController(infos: infos)
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
    }
    
    @objc func createButtonTap() {
        let model = infos.filter({ $0.owneUserId == UserInfo.uid }).first
        if let info = model?.liveInfo {
            showAlert(title: "you_have_created_the_room_will_jump_into_you".localized,
                      message: "") { [weak self] in
                let engine = CreateLiveController.createEngine()
                let avaterEngineWapper = CreateLiveController.createAvaterEngineWapper(agoraKit: engine)
                let vc = LiveViewCotroller(info: info, agoraKit: engine, avaterEngineWapper: avaterEngineWapper)
                self?.navigationController?.pushViewController(vc, animated: true)
            }
            return
        }
        
        let vc = CreateLiveController()
        vc.modalPresentationStyle = .fullScreen
        present(vc, animated: true, completion: nil)
        vc.delegate = self
    }
    
    @objc func pullRefreshHandler() {
        loadData()        
//        let vc = DressUpSheetVC(infos: [])
//        vc.show(in: self)
        
//        let info = PinchFaceSheetVC.Info(title: "123", items: ["1111", "222", "333"])
//        let info2 = PinchFaceSheetVC.Info(title: "456", items: ["999", "0909", "6767"])
//        let vc = PinchFaceSheetVC(infos: [info, info2, info, info2, info, info2, info])
//        vc.show(in: self)
    }
}

// MARK: - UITableViewDataSource, UITableViewDelegate
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
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let info = infos[indexPath.row].liveInfo
        let vc = LiveViewCotroller(info: info, agoraKit: nil, avaterEngineWapper: nil)
        navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - CreateLiveControllerDelegate
extension MainViewController: CreateLiveControllerDelegate {
    func createLiveControllerDidStartButtonTap(info: LiveRoomInfo,
                                               agoraKit: AgoraRtcEngineKit,
                                               avaterEngineWapper: AvatarEngineWapper) {
        let vc = LiveViewCotroller(info: info, agoraKit: agoraKit, avaterEngineWapper: avaterEngineWapper)
        navigationController?.pushViewController(vc, animated: true)
    }
}

// MARK: - Info
extension MainViewController {
    struct Info {
        let title: String
        let roomId: String
        let owneUserId: String
        let liveInfo: LiveRoomInfo
        
        init(info: LiveRoomInfo) {
            self.title = info.roomName
            self.roomId = info.roomId
            self.owneUserId = info.userId
            self.liveInfo = info
        }
    }
}
