package io.agora.metalive

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.agora.metalive.component.AvatarOptionDialogUtil
import io.agora.metalive.databinding.PreviewActivityBinding
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RoomManager.RoomInfo
import io.agora.metalive.manager.RtcManager
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.VideoDimensions
import io.agora.uiwidget.function.VideoSettingDialog
import io.agora.uiwidget.utils.StatusBarUtil
import java.util.*

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
                    roomType = RoomManager.RoomType.SINGLE_HOST
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
        AvatarOptionDialogUtil().show(this)
    }

    private fun showSettingDialog() {
        val dimensionsOptions = arrayListOf<String>()
        var dimensionDefault = 0
        RtcManager.sVideoDimensions.forEachIndexed { index: Int, item: VideoDimensions ->
            if (item.width == RtcManager.encoderConfiguration.dimensions.width && item.height == RtcManager.encoderConfiguration.dimensions.height) {
                dimensionDefault = index
            }
            dimensionsOptions.add(String.format(Locale.US, "%dx%d", item.width, item.height))
        }

        val frameRateOptions = arrayListOf<String>()
        var frameRateDefault = 0
        RtcManager.sFrameRates.forEachIndexed { index: Int, item: VideoEncoderConfiguration.FRAME_RATE ->
            if (item.value == RtcManager.encoderConfiguration.frameRate) {
                frameRateDefault = index
            }
            frameRateOptions.add(String.format(Locale.US, "%d", item.value))
        }

        val renderQualityOptions = arrayListOf<String>()
        var renderQualityDefault = 0
        RtcManager.sRenderQuality.forEachIndexed { index: Int, item: RtcManager.AvatarRenderQuality ->
            renderQualityOptions.add(item.name)
            if(RtcManager.currRenderQuality.stringId.equals(item.stringId)){
                renderQualityDefault = index
            }
        }

        VideoSettingDialog(this)
            .addTextItem(
                getString(R.string.video_setting_dialog_title_resolution),
                dimensionsOptions,
                dimensionDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance().setCameraCaptureResolution(RtcManager.sVideoDimensions[position])
            }
            .addTextItem(
                getString(R.string.video_setting_dialog_title_framerate),
                frameRateOptions,
                frameRateDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance().setEncoderVideoFrameRate(RtcManager.sFrameRates[position])
            }
            .addTextItem(
                "RenderQuality",
                renderQualityOptions,
                renderQualityDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance().setLocalAvatarQuality(RtcManager.sRenderQuality[position])
            }
            .addProgressItem(
                getString(R.string.video_setting_dialog_title_bitrate),
                0,
                2000,
                RtcManager.encoderConfiguration.bitrate,
                "%s kps",
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        RtcManager.encoderConfiguration.bitrate = progress
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }

                })
            .show()
    }

    private fun initPreview() {
        rtcManager.renderLocalAvatarVideo(mBinding.surfaceViewContainer)
        rtcManager.renderLocalCameraVideo(mBinding.cameraViewContainer)

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