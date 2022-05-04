package io.agora.metalive

import android.os.Bundle
import android.os.Handler
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.agora.metalive.databinding.PreviewActivityBinding
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RoomManager.RoomInfo
import io.agora.metalive.manager.RtcManager
import io.agora.rtc2.video.VideoEncoderConfiguration.VideoDimensions
import io.agora.metalive.component.AvatarOptionDialogUtil
import io.agora.uiwidget.function.VideoSettingDialog
import io.agora.uiwidget.function.VideoSettingDialog.OnValuesChangeListener
import io.agora.uiwidget.utils.StatusBarUtil

class PreviewActivity : AppCompatActivity() {
    private val avatarButtonDelay = 10000L
    private val rtcManager = RtcManager.getInstance()
    private val mBinding by lazy {
        PreviewActivityBinding.inflate(LayoutInflater.from(this))
    }

    private lateinit var handler: Handler

    // Show face edit button after a short while, since we have
    // no callbacks of the completion of avatar model loading,
    // Take this as a workaround before the callback is given
    // in some future sdk version.
    // Furthermore, the delay should be set as late as it can be
    // accepted, because of the uncertainty of the valid starting
    // point of the avatar processing.
    private val showAvatarButtonRunnable = Runnable {
        mBinding.previewControlView.setBeautyIcon(true, avatarButtonClickListener)

        RtcManager.getInstance().let {
            // Workaround of new avatar APIs.
            // Currently, the inner process session begins during the
            // start/stop interval, where we are able to get dressing
            // list or face edit effect list, or even perform any of
            // the avatar parameter setting.
            it.startDressing()
            it.requestDressOptionList()
            it.stopDressing()

            it.startFaceEdit()
            it.requestFaceEditOptionList()
            it.stopFaceEdit()
        }
    }

    private val avatarButtonClickListener = View.OnClickListener {
        showAvatarOptionDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        StatusBarUtil.hideStatusBar(window, true)

        handler = Handler(mainLooper)
        initView()
        initPreview()
    }

    private fun initView() {
        // Currently go to multi-host scene by default,
        // remove bottom tab layout.
        // Modify this if multi-scenes is supported.
        mBinding.tabLayout.isVisible = false

        mBinding.previewControlView.apply {
            setBackIcon(true) { onBackPressed() }

            setCameraIcon(false) {
                // No camera switching for this scene.
            }

            setBeautyIcon(false) {
                // By default do nothing for this callback
                // when entering this activity for the first
                // several seconds.
            }

            setSettingIcon(true) {
                showSettingDialog()
            }

            setGoLiveBtn { _: View?, randomName: String? ->
                RoomManager.getInstance().createRoom(RoomInfo(randomName).apply {
                    roomType = mBinding.tabLayout.selectedTabPosition + 1
                }) { data: RoomInfo ->
                    // Only multi-host room is supported now,
                    // remove if other room types are supported in future versions.
                    data.roomType = RoomManager.RoomType.MULTI_HOST
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
        AvatarOptionDialogUtil().show(this)
    }

    private fun showSettingDialog() {
        val resolutions: MutableList<Size> = ArrayList()
        for (sVideoDimension in RtcManager.sVideoDimensions) {
            resolutions.add(Size(sVideoDimension.width, sVideoDimension.height))
        }
        val frameRates: MutableList<Int> = ArrayList()
        for (sFrameRate in RtcManager.sFrameRates) {
            frameRates.add(sFrameRate.value)
        }
        VideoSettingDialog(this@PreviewActivity)
            .setResolutions(resolutions)
            .setFrameRates(frameRates)
            .setBitRateRange(0, 2000)
            .setDefaultValues(
                Size(
                    RtcManager.encoderConfiguration.dimensions.width,
                    RtcManager.encoderConfiguration.dimensions.height
                ),
                RtcManager.encoderConfiguration.frameRate,
                RtcManager.encoderConfiguration.bitrate
            )
            .setOnValuesChangeListener(object : OnValuesChangeListener {
                override fun onResolutionChanged(resolution: Size) {
                    RtcManager.encoderConfiguration.dimensions =
                        VideoDimensions(resolution.width, resolution.height)
                }

                override fun onFrameRateChanged(framerate: Int) {
                    RtcManager.encoderConfiguration.frameRate = framerate
                }

                override fun onBitrateChanged(bitrate: Int) {
                    RtcManager.encoderConfiguration.bitrate = bitrate
                }
            })
            .show()
    }

    private fun initPreview() {
        rtcManager.renderLocalAvatarVideo(mBinding.surfaceViewContainer)

        // Wait until the avatar loading is possibly completed,
        // then we should acquire the information of current
        // avatar model.
        // The amount of delay is totally empirical, and is as
        // late as we could accept.
        handler.postDelayed(showAvatarButtonRunnable, avatarButtonDelay)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(showAvatarButtonRunnable)
    }
}