//
//  Float+Extension.swift
//  Scene-Examples
//
//  Created by ZYP on 2022/5/19.
//

import Foundation
public extension Float {
    var keep0: Int {
        return Int(Darwin.round(self))
    }
    
    var keep1: Float {
        return Float(Darwin.round(self * 10)/10)
    }
    
    var keep2: Float {
        return Float(Darwin.round(self * 100)/100)
    }
    
    var keep3: Float {
        return Float(Darwin.round(self * 1000)/1000)
    }
}
