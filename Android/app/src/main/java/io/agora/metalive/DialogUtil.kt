package io.agora.metalive

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.SeekBar
import io.agora.metalive.component.AvatarDressDialog
import io.agora.metalive.component.AvatarFaceEditDialog
import io.agora.metalive.manager.RtcManager
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.uiwidget.function.LiveToolsDialog
import io.agora.uiwidget.function.VideoSettingDialog
import java.util.*

object DialogUtil {

    fun showAvatarOptionDialog(context: Context, statusTextDart: Boolean = true, showRun: (()->Unit)? = null, dismissRun: (()->Unit)? = null): LiveToolsDialog {
        return LiveToolsDialog(context, statusTextDart).apply {
            addToolItem(
                LiveToolsDialog.ToolItem(
                    R.string.avatar_option_name_dress,
                    io.agora.uiwidget.R.drawable.live_tool_icon_setting
                ), false
            ) { _: View, _: LiveToolsDialog.ToolItem ->
                dismiss()
                AvatarDressDialog(context, statusTextDart).let {
                    it.setOnDismissListener {
                        show()
                        dismissRun?.invoke()
                    }
                    it.show()
                    showRun?.invoke()
                }
            }
            addToolItem(
                LiveToolsDialog.ToolItem(
                    R.string.avatar_option_name_face,
                    io.agora.uiwidget.R.drawable.live_tool_icon_setting
                ), false
            ) { _: View, _: LiveToolsDialog.ToolItem ->
                dismiss()
                AvatarFaceEditDialog(context, statusTextDart).let {
                    it.setOnDismissListener {
                        show()
                        dismissRun?.invoke()
                    }
                    it.show()
                    showRun?.invoke()
                }
            }
            show()
        }

    }

    fun showSettingDialog(context: Context, statusTextDart: Boolean = true): VideoSettingDialog {
        val dimensionsOptions = arrayListOf<String>()
        var dimensionDefault = 0
        RtcManager.sVideoDimensions.forEachIndexed { index: Int, item: VideoEncoderConfiguration.VideoDimensions ->
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
            if (RtcManager.currRenderQuality.stringId.equals(item.stringId)) {
                renderQualityDefault = index
            }
        }

        return VideoSettingDialog(context, statusTextDart).apply {
            addTextItem(
                context.getString(R.string.video_setting_dialog_title_resolution),
                dimensionsOptions,
                dimensionDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance()
                    .setCameraAndEncoderResolution(RtcManager.sVideoDimensions[position])
            }
            addTextItem(
                context.getString(R.string.video_setting_dialog_title_framerate),
                frameRateOptions,
                frameRateDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance().setEncoderVideoFrameRate(RtcManager.sFrameRates[position])
            }
            addTextItem(
                "RenderQuality",
                renderQualityOptions,
                renderQualityDefault
            ) { _: DialogInterface, position: Int ->
                RtcManager.getInstance().setLocalAvatarQuality(RtcManager.sRenderQuality[position])
            }
            addProgressItem(
                context.getString(R.string.video_setting_dialog_title_bitrate),
                0,
                2000,
                RtcManager.encoderConfiguration.bitrate,
                "%s kbps",
                object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        RtcManager.getInstance().setEncoderVideoBitrate(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {

                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {

                    }

                })
            show()
        }
    }
}