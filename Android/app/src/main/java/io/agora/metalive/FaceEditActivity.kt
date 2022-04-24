package io.agora.metalive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.FaceEditActivityBinding
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.function.editface.tabs.ImageTab
import io.agora.uiwidget.utils.StatusBarUtil

class FaceEditActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_DATA_FROM = "from"
        const val FROM_ROOM_LIST = "fromRoomList"
        const val FROM_ROOM_DETAIL = "fromRoomDetail"
        const val FROM_ROOM_PREVIEW = "fromRoomPreview"

        fun launcher(context: ComponentActivity, onResult: (() -> Unit)? = null): ActivityResultLauncher<String> {
            return context.registerForActivityResult(object :
                ActivityResultContract<String, String>() {
                override fun createIntent(context: Context, input: String?): Intent {
                    return Intent(context, FaceEditActivity::class.java).apply {
                        putExtra(EXTRA_DATA_FROM, input)
                    }
                }

                override fun parseResult(resultCode: Int, intent: Intent?): String {
                    return "";
                }

            }) { onResult?.invoke() }
        }
    }

    private val mBinding by lazy {
        FaceEditActivityBinding.inflate(LayoutInflater.from(this))
    }
    private val rtcManager by lazy { RtcManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, false)
        setContentView(mBinding.root)

        mBinding.ivBack.setOnClickListener { onBackPressed() }

        rtcManager.init(this, getString(R.string.rtc_app_id), null)
        rtcManager.renderLocalAvatarVideo(mBinding.localContainer)
    }

    override fun onDestroy() {
        if (intent.getStringExtra(EXTRA_DATA_FROM) == FROM_ROOM_LIST) {
            rtcManager.destroy()
        }
        super.onDestroy()
    }

    private fun initSelectView() {
        with(mBinding) {
            selectView.addTab(
                ImageTab(
                    "选择形象",
                    R.drawable.user_profile_image_10,
                    R.drawable.user_profile_image_12,
                    null
                )
            )
//            selectView.addTab(
//                ColorItemTab(
//                    "头发",
//                    FilePathFactory.hairBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A?.hairIndex ?: 0,
//                    { lastPos, position -> true },
//
//                    ColorConstant.hair_color,
//                    mAvatarP2A?.hairColorValue?.toInt() ?: 0,
//                    { }
//                )
//            )
//            selectView.addTab(
//                SeekColorItemTab(
//                    "脸型",
//                    EditParamFactory.mEditParamFace.map { ItemInfo(it.resId) },
//                    0,
//                    { lastPos, position -> true },
//
//                    ColorPickGradient.getmColorArr(),
//                    0,
//                    { }
//                )
//            )
//            selectView.addTab(
//                ColorItemTab(
//                    "眼睛",
//                    EditParamFactory.mEditParamEye.map { ItemInfo(it.resId) },
//                    0,
//                    { lastPos, position -> true },
//
//                    ColorConstant.iris_color,
//                    mAvatarP2A?.irisColorValue?.toInt() ?: 0,
//                    { }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "嘴巴",
//                    EditParamFactory.mEditParamMouth.map { ItemInfo(it.resId) },
//                    0,
//                    { lastPos, position -> true },
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "鼻子",
//                    EditParamFactory.mEditParamNose.map { ItemInfo(it.resId) },
//                    0,
//                    { lastPos, position -> true },
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "胡子",
//                    FilePathFactory.beardBundleRes().map { ItemInfo(it.resId) },
//                    0,
//                    { lastPos, position -> true },
//                )
//            )
//
//            editFaceItemManager.initMakeUpList(mAvatarP2A!!)
//            selectView.addTab(
//                ColorMultipleItemTab(
//                    "美妆",
//                    editFaceItemManager.makeUpList.map {
//                        MultipleItemInfo(
//                            it.resId,
//                            it.type,
//                            it.name
//                        )
//                    },
//                    editFaceItemManager.markUpPairBeanMap,
//                    { i: Int, i1: Int, b: Boolean, i2: Int, i3: Int -> },
//
//                    ColorConstant.makeup_color,
//                    0,
//                    { }
//                )
//            )
//
//            selectView.addTab(
//                SwitchColorItemTab(
//                    "眼镜",
//                    FilePathFactory.glassesBundleRes().map { ItemInfo(it.resId) },
//                    0,
//                    { i: Int, i1: Int -> true },
//
//                    "镜框",
//                    ColorConstant.glass_color,
//                    mAvatarP2A.glassesColorValue.toInt(),
//                    {},
//
//                    "镜片",
//                    ColorConstant.glass_frame_color,
//                    mAvatarP2A.glassesFrameColorValue.toInt(),
//                    {}
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "帽子",
//                    FilePathFactory.hatBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.hatIndex,
//                    { i: Int, i1: Int -> true }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "套装",
//                    FilePathFactory.clothesBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.getClothesIndex(),
//                    { i: Int, i1: Int -> true }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "衣服",
//                    FilePathFactory.clothUpperBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.getClothesUpperIndex(),
//                    { i: Int, i1: Int -> true }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "裤子",
//                    FilePathFactory.clothLowerBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.getClothesLowerIndex(),
//                    { i: Int, i1: Int -> true }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "鞋子",
//                    FilePathFactory.shoeBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.getShoeIndex(),
//                    { i: Int, i1: Int -> true }
//                )
//            )
//
//            editFaceItemManager.initDecorationList(mAvatarP2A)
//            selectView.addTab(
//                MultipleItemTab(
//                    "饰品",
//                    editFaceItemManager.decorationList.map {
//                        MultipleItemInfo(
//                            it.resId,
//                            it.type,
//                            it.name
//                        )
//                    },
//                    editFaceItemManager.decorationPairBeanMap,
//                    { i: Int, i1: Int, b: Boolean, i2: Int, i3: Int -> }
//                )
//            )
//            selectView.addTab(
//                ItemTab(
//                    "背景",
//                    FilePathFactory.scenes2DBundleRes().map { ItemInfo(it.resId) },
//                    mAvatarP2A.getBackground2DIndex(),
//                    { i: Int, i1: Int -> true }
//                )
//            )
        }
    }
}