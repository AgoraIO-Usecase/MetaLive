//
//  UIViewController+Extension.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/7.
//

import UIKit

extension UIViewController {
    func showAlert(title: String? = nil, message: String, confirm: @escaping () -> Void) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let action = UIAlertAction(title: "Confirm".localized, style: .default) { _ in
            confirm()
        }
        let cancel = UIAlertAction(title: "Cancel".localized, style: .cancel, handler: nil)
        alertController.addAction(action)
        alertController.addAction(cancel)
        present(alertController, animated: true, completion: nil)
    }
}
