package io.agora.metalive

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.agora.metalive.databinding.PreviewActivityBinding
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RoomManager.RoomInfo
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.utils.StatusBarUtil

class PreviewActivity : AppCompatActivity() {
    private val rtcManager = RtcManager.getInstance()
    private val mBinding by lazy {
        PreviewActivityBinding.inflate(LayoutInflater.from(this))
    }

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        StatusBarUtil.hideStatusBar(window, false)

        handler = Handler(mainLooper)
        initView()
        initPreview()
    }

    private fun initView() {
        mBinding.root.tag = true
        mBinding.root.setOnClickListener {
            val visible = !(mBinding.root.tag as Boolean)
            mBinding.root.tag = visible

            mBinding.previewControlView.isVisible = visible
            mBinding.cameraViewContainer.isVisible = visible
        }
        // Currently go to multi-host scene by default,
        // remove bottom tab layout.
        // Modify this if multi-scenes is supported.
        mBinding.tabLayout.isVisible = false

        mBinding.previewControlView.apply {
            setBackIcon(true) { onBackPressed() }

            setCameraIcon(false) {
                // No camera switching for this scene.
            }

            setBeautyIcon(true) {
                // By default do nothing for this callback
                // when entering this activity for the first
                // several seconds.
                showAvatarOptionDialog()
            }

            setSettingIcon(true) {
                DialogUtil.showSettingDialog(this@PreviewActivity, false)
            }

            setGoLiveBtn { _: View?, randomName: String? ->
                RoomManager.getInstance().createRoom(RoomInfo(randomName).apply {
                    roomType = RoomManager.RoomType.MULTI_HOST
                }) { data: RoomInfo ->
                    // Only multi-host room is supported now,
                    // remove if other room types are supported in future versions.
                    runOnUiThread { goToRoomDetail(data) }
                }
            }
        }
    }

    private fun goToRoomDetail(data: RoomInfo) {
        startActivity(RoomDetailActivity.newIntent(this, data))
        finish()
    }

    private fun showAvatarOptionDialog() {
        DialogUtil.showAvatarOptionDialog(this, false, {
            mBinding.cameraViewContainer.isVisible = false
        }, {
            mBinding.cameraViewContainer.isVisible = true
        })
    }

    private fun initPreview() {
        rtcManager.renderLocalAvatarVideo(mBinding.surfaceViewContainer)
        rtcManager.renderLocalCameraVideo(mBinding.cameraViewContainer)
    }

}