package io.agora.metalive

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import io.agora.metalive.databinding.RoomListActivityBinding
import io.agora.metalive.databinding.RoomListCreateDialogViewBinding
import io.agora.uiwidget.function.RoomListView
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil
import java.util.*

class RoomListActivity : AppCompatActivity() {

    private val mBinding by lazy {
        RoomListActivityBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        initListView()
        mBinding.ivCreate.setOnClickListener {
            val dialogViewBinding = RoomListCreateDialogViewBinding.inflate(LayoutInflater.from(this))
            dialogViewBinding.ivRandom.setOnClickListener {
                dialogViewBinding.etRoomName.setText(RandomUtil.randomLiveRoomName(this))
            }
            AlertDialog.Builder(this)
                .setTitle(R.string.room_list_create_dialog_title)
                .setView(dialogViewBinding.root)
                .setPositiveButton(R.string.common_sure) { dialog, _ ->
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun initListView() {
        val listAdapter = object :
            RoomListView.AbsRoomListAdapter<RoomManager.RoomInfo>() {

            override fun onItemUpdate(
                holder: RoomListView.RoomListItemViewHolder,
                item: RoomManager.RoomInfo
            ) {
                holder.itemView.findViewById<ImageView>(R.id.iv_profile_01).setImageDrawable(RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, RandomUtil.randomLiveRoomIcon())).apply { isCircular = true })
                holder.itemView.findViewById<ImageView>(R.id.iv_profile_02).setImageDrawable(RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, RandomUtil.randomLiveRoomIcon())).apply { isCircular = true })
                holder.itemView.findViewById<ImageView>(R.id.iv_profile_03).setImageDrawable(RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, RandomUtil.randomLiveRoomIcon())).apply { isCircular = true })
                holder.itemView.findViewById<ImageView>(R.id.iv_profile_04).setImageDrawable(RoundedBitmapDrawableFactory.create(resources, BitmapFactory.decodeResource(resources, RandomUtil.randomLiveRoomIcon())).apply { isCircular = true })

                holder.participantsCount.text = "4"
                holder.roomName.text = item.name
            }

            override fun onRefresh() {
                addAll(Arrays.asList(
                    RoomManager.RoomInfo("11111"),
                    RoomManager.RoomInfo("222222"),
                    RoomManager.RoomInfo("33333333"),
                    RoomManager.RoomInfo("44444"),
                    RoomManager.RoomInfo("55555"),
                    RoomManager.RoomInfo("666666"),
                ))
            }

            override fun onLoadMore() {

            }
        }
        mBinding.listView.setListAdapter(listAdapter, 1)

    }


}