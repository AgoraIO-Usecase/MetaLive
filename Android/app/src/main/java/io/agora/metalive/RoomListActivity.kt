package io.agora.metalive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.RoomListActivityBinding
import io.agora.metalive.databinding.RoomListItemBinding
import io.agora.metalive.manager.RoomManager
import io.agora.uiwidget.basic.BindingViewHolder
import io.agora.uiwidget.function.RoomListView
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil
import io.agora.uiwidget.utils.UIUtil


class RoomListActivity : AppCompatActivity() {

    private val mBinding by lazy {
        RoomListActivityBinding.inflate(LayoutInflater.from(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {
        mBinding.listView.setSpanCount(1)
        mBinding.listView.setListAdapter(object :
            RoomListView.CustRoomListAdapter<RoomManager.RoomInfo, RoomListItemBinding>() {
            override fun onItemUpdate(
                holder: BindingViewHolder<RoomListItemBinding>,
                item: RoomManager.RoomInfo
            ) {
                val profileIvs = arrayOf(
                    holder.binding.ivProfile01,
                    holder.binding.ivProfile02,
                    holder.binding.ivProfile03,
                    holder.binding.ivProfile04
                )
                val profileSize = RandomUtil.randomId() % profileIvs.size + 1
                var index = 0
                while (index < profileIvs.size) {
                    if (index < profileSize) {
                        profileIvs[index].setImageDrawable(
                            UIUtil.getRoundDrawable(
                                holder.itemView.context,
                                RandomUtil.randomLiveRoomIcon(),
                                999.0f
                            )
                        )
                    } else {
                        profileIvs[index].setImageDrawable(null)
                    }

                    index++;
                }

                holder.binding.roomListItemRoomName.text = "${item.roomName}(${item.roomId})"
                holder.binding.roomListItemParticipantCount.text = profileSize.toString()
                holder.binding.root.setOnClickListener {
                    startActivity(RoomDetailActivity.newIntent(this@RoomListActivity, item))
                }
            }

            override fun onRefresh() {
                RoomManager.getInstance().getAllRooms {
                    runOnUiThread {
                        removeAll()
                        insertAll(it)
                        triggerDataListUpdateRun()
                    }
                }
            }

            override fun onLoadMore() {

            }
        })

        mBinding.ivCreate.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }

    }

    override fun finish() {
        setResult(0)
        super.finish()
    }


}