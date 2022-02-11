package io.agora.metalive

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.EditFaceActivityBinding
import io.agora.uiwidget.function.editface.tabs.*

class EditFaceActivity : AppCompatActivity() {

    private val mBinding by lazy {
        EditFaceActivityBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        mBinding.selectView.addTab(
            ImageTab(
                "选择形象",
                R.drawable.user_profile_image_10,
                R.drawable.user_profile_image_12,
                null
            )
        )
        mBinding.selectView.addTab(
            ColorItemTab(
                "脸"
            )
        )
        mBinding.selectView.addTab(
            ShapeTab(
                "脸型"
            )
        )
        mBinding.selectView.addTab(
            ItemTab(
                "嘴巴"
            )
        )
        mBinding.selectView.addTab(
            MakeUpTab(
                "美妆"
            )
        )
        mBinding.selectView.addTab(
            GlassesTab(
                "眼镜"
            )
        )
        mBinding.selectView.addTab(
            DecorationTab(
                "装饰"
            )
        )
    }
}