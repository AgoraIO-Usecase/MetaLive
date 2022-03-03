package io.agora.metalive

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.RoomDetailActivityBinding
import io.agora.metalive.manager.EditFaceManager
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.basic.BindingViewHolder
import io.agora.uiwidget.databinding.OnlineUserListDialogItemBinding
import io.agora.uiwidget.function.*
import io.agora.uiwidget.utils.ImageUtil
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil

class RoomDetailActivity : AppCompatActivity() {

    private val mBinding by lazy {
        RoomDetailActivityBinding.inflate(LayoutInflater.from(this))
    }

    private val mMsgAdapter by lazy {
        object :
            LiveRoomMessageListView.LiveRoomMessageAdapter<RoomManager.MessageInfo>(3) {

            override fun onItemUpdate(
                holder: LiveRoomMessageListView.MessageListViewHolder,
                item: RoomManager.MessageInfo,
                position: Int
            ) {
                holder.getViewById<ImageView>(R.id.iv_avatar)?.apply {
                    ImageUtil.setDrawableRound(
                        context,
                        this,
                        RandomUtil.randomLiveRoomIcon(),
                        999.0f
                    )
                }
                holder.getViewById<TextView>(R.id.tv_name)?.apply {
                    text = item.userName
                }
                holder.getViewById<TextView>(R.id.tv_content)?.apply {
                    text = item.content
                }
                holder.getViewById<ImageView>(R.id.iv_content_gift)?.apply {
                    if (item.giftIcon == View.NO_ID) {
                        setImageDrawable(null)
                    } else {
                        setImageResource(item.giftIcon)
                    }
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        mBinding.liveMessageListView.adapter = mMsgAdapter
        mMsgAdapter.addMessage(RoomManager.MessageInfo("111234", "123555556"))

        mBinding.liveBottomView
            .setupCloseBtn(false, null)
            // 文本输入
            .setupInputText(true) {
                TextInputDialog(this@RoomDetailActivity, true)
                    .setOnSendClickListener { dialog, text ->
                        // 发送本地文本
                        mMsgAdapter.addMessage(RoomManager.MessageInfo("111234", text))
                    }
                    .show()
            }
            // 麦克风
            .setFun1Visible(true)
            .setFun1ImageResource(R.drawable.room_detail_icon_mic)
            .setFun1Background(null)
            .setFun1Activated(true)
            .setFun1ClickListener {
                mBinding.liveBottomView.apply {
                    RtcManager.getInstance().muteLocalAudio(isFun1Activated)
                    isFun1Activated = !isFun1Activated
                }
            }
            // 特效
            .setFun2Visible(true)
            .setFun2ImageResource(R.drawable.room_detail_icon_magic)
            .setFun2Background(null)
            .setFun2ClickListener {
                Intent(this@RoomDetailActivity, EditFaceActivity::class.java).apply {
                    startActivity(this)
                }
            }
            // 更多
            .setupMoreBtn(true) {
                LiveToolsDialog(this@RoomDetailActivity, true)
                    .addToolItem(
                        LiveToolsDialog.TOOL_ITEM_ROTATE,
                        false
                    ) { view: View, toolItem: LiveToolsDialog.ToolItem -> }
                    .addToolItem(
                        LiveToolsDialog.TOOL_ITEM_VIDEO,
                        true
                    ) { view: View, toolItem: LiveToolsDialog.ToolItem -> }
                    .addToolItem(
                        LiveToolsDialog.TOOL_ITEM_SETTING,
                        true
                    ) { view: View, toolItem: LiveToolsDialog.ToolItem -> }
                    .show()
            }
            // 礼物
            .setFun3Visible(true)
            .setFun3ImageResource(R.drawable.room_detail_icon_gift)
            .setFun3ClickListener {
                GiftGridDialog(this@RoomDetailActivity, true)
                    .setOnGiftItemSelectListener { dialog, item, position ->
                        // 发送礼物
                    }
                    .show()
            }
            // 举手
            .setFun4Visible(true)
            .setFun4ImageResource(R.drawable.room_detail_icon_raisehand)
            .setFun4Activated(true)
            .setFun4Background(null)
            .setFun4ClickListener {
                mBinding.liveBottomView.apply {
                    isFun4Activated = !isFun4Activated
                }

                object: OnlineUserListDialog.DefaultListItemAdapter<RoomManager.UserInfo>(){

                    override fun onItemUpdate(
                        holder: BindingViewHolder<OnlineUserListDialogItemBinding>,
                        position: Int,
                        item: RoomManager.UserInfo?
                    ) {
                        holder.binding.ivIcon.apply {
                            ImageUtil.setDrawableRound(context, this, RandomUtil.randomLiveRoomIcon(), 999.0f)
                        }
                        holder.binding.tvName.apply {
                            text = RandomUtil.randomUserName(context)
                        }
                        holder.binding.tvStatus.apply {
                            isActivated = true
                            setText(R.string.online_user_list_dialog_invite)
                        }
                    }
                }.apply {
                    resetAll(listOf(RoomManager.UserInfo(), RoomManager.UserInfo()))
                    OnlineUserListDialog(this@RoomDetailActivity, true)
                        .setListAdapter(this)
                        .show()
                }


            }
    }

    override fun onResume() {
        super.onResume()
        EditFaceManager.getInstance().start()
        RtcManager.getInstance().renderLocalAvatarVideo(mBinding.viewport1.videoContainer)
    }

    override fun onDestroy() {
        EditFaceManager.getInstance().stop()
        RtcManager.getInstance().reset(true)
        super.onDestroy()
    }
}