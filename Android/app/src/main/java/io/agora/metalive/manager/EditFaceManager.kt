package io.agora.metalive.manager

import android.app.Application
import android.content.Context
import io.agora.metalive.manager.editface.constant.ColorConstant
import io.agora.metalive.manager.editface.core.client.PTAClientWrapper
import io.agora.metalive.manager.editface.shape.EditParamFactory

object EditFaceManager {

    var context : Application?= null

    @Volatile
    private var initialized = false

    fun init(context: Context){
        if(initialized){
            return
        }
        this.context = context.applicationContext as Application?

        //初始化 core data 数据---捏脸
        PTAClientWrapper.setupData(context)
        PTAClientWrapper.setupStyleData(context)

        //风格选择后初始化 P2A client
        ColorConstant.init(context)
        EditParamFactory.init(context)

        initialized = true
    }
}