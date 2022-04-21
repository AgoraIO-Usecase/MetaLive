package io.agora.metalive

import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.PreviewActivityBinding
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RoomManager.RoomInfo
import io.agora.metalive.manager.RtcManager
import io.agora.rtc2.video.VideoEncoderConfiguration.VideoDimensions
import io.agora.uiwidget.function.VideoSettingDialog
import io.agora.uiwidget.function.VideoSettingDialog.OnValuesChangeListener
import io.agora.uiwidget.utils.StatusBarUtil

class PreviewActivity : AppCompatActivity() {
    private val rtcManager = RtcManager.getInstance()
    private val mBinding by lazy {
        PreviewActivityBinding.inflate(LayoutInflater.from(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        StatusBarUtil.hideStatusBar(window, false)
        initView()
        initPreview()
    }

    private fun initView() {
        mBinding.previewControlView.apply {
            setBackIcon(true) { v: View? -> onBackPressed() }
            setCameraIcon(true) { v: View? -> }
            setBeautyIcon(false, null)
            setSettingIcon(true) { v: View? ->
                // 视频参数设置弹窗
                showSettingDialog()
            }
            setGoLiveBtn { _: View?, randomName: String? ->
                RoomManager.getInstance().createRoom(RoomInfo(randomName).apply {
                    roomType = mBinding.tabLayout.selectedTabPosition + 1
                }) { data: RoomInfo ->
                    runOnUiThread { goToRoomDetail(data) }
                }
            }
        }
    }

    private fun goToRoomDetail(data: RoomInfo) {
        startActivity(RoomDetailActivity.newIntent(this, data))
        finish()
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
        rtcManager.init(this, getString(R.string.rtc_app_id), null)
        val surfaceViewContainer = findViewById<FrameLayout>(R.id.surface_view_container)
        rtcManager.renderLocalCameraVideo(surfaceViewContainer)
    }

    override fun onBackPressed() {
        rtcManager.reset(true)
        super.onBackPressed()
    }
}