//
//  UIViewController+Extension.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/7.
//

import UIKit

extension UIViewController {
    func showAlert(title: String? = nil,
                   message: String,
                   showCancle: Bool = true,
                   confirm: @escaping () -> Void) {
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        let action = UIAlertAction(title: "Confirm".localized, style: .default) { _ in
            confirm()
        }
        alertController.addAction(action)
        if showCancle {
            let cancel = UIAlertAction(title: "Cancel".localized, style: .cancel, handler: nil)
            alertController.addAction(cancel)
        }
        present(alertController, animated: true, completion: nil)
    }
}
