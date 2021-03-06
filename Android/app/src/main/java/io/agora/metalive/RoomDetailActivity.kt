package io.agora.metalive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.agora.metalive.databinding.RoomDetailActivityBinding
import io.agora.metalive.databinding.RoomDetailMessageListItemBinding
import io.agora.metalive.databinding.RoomDetailRaisehandItemBinding
import io.agora.metalive.manager.EditFaceManager
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.basic.BindingViewHolder
import io.agora.uiwidget.function.*
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil
import io.agora.uiwidget.utils.UIUtil
import java.lang.ref.WeakReference
import kotlin.random.Random

class RoomDetailActivity : AppCompatActivity() {

    companion object {
        private const val ACTIVITY_RESULT_CODE_FACE_EDIT = 1001
        private const val EXTRA_ROOM_INFO = "roomInfo"

        fun newIntent(context: Context, roomInfo: RoomManager.RoomInfo): Intent {
            return Intent(context, RoomDetailActivity::class.java).apply {
                putExtra(EXTRA_ROOM_INFO, roomInfo)
            }
        }
    }

    private val mBinding by lazy {
        RoomDetailActivityBinding.inflate(LayoutInflater.from(this))
    }
    private val mRoomInfo by lazy {
        intent.getSerializableExtra(EXTRA_ROOM_INFO) as RoomManager.RoomInfo
    }
    private val seatLayouts by lazy {
        arrayOf(mBinding.viewport1, mBinding.viewport2, mBinding.viewport3, mBinding.viewport4)
    }
    private val mMsgAdapter by lazy {
        object :
            LiveRoomMessageListView.AbsMessageAdapter<RoomManager.MessageInfo, RoomDetailMessageListItemBinding>() {

            override fun onItemUpdate(
                holder: BindingViewHolder<RoomDetailMessageListItemBinding>,
                item: RoomManager.MessageInfo,
                position: Int
            ) {
                holder.binding.ivAvatar.setImageDrawable(
                    UIUtil.getRoundDrawable(
                        holder.itemView.context,
                        RandomUtil.randomLiveRoomIcon(),
                        999.0f
                    )
                )
                holder.binding.tvName.text = item.userName
                holder.binding.tvContent.text = item.content
                holder.binding.ivContentGift.apply {
                    if (item.giftIcon == View.NO_ID) {
                        setImageDrawable(null)
                    } else {
                        setImageResource(item.giftIcon)
                    }
                }
            }
        }
    }
    private val userAddOrUpdateObserver by lazy {
        RoomManager.DataCallback<RoomManager.UserInfo> { runOnUiThread { updateUserView(it) } }
    }
    private val userDeleteObserver by lazy {
        RoomManager.DataCallback<RoomManager.UserInfo> {
            runOnUiThread {
                updateUserView(it)
                if (it.userId.equals(mRoomInfo.userId)) {
                    // ???????????????
                    showRoomOwnerExitDialog()
                }
            }
        }
    }

    private val giftReceiveObserver by lazy {
        RoomManager.DataCallback<RoomManager.GiftInfo> {
            runOnUiThread {
                // ??????????????????
                mMsgAdapter.addMessage(
                    RoomManager.MessageInfo(
                        it.userId,
                        getString(R.string.live_room_message_gift_prefix),
                        it.getIconId()
                    )
                )
                // ????????????
                GiftAnimPlayDialog(this@RoomDetailActivity)
                    .setAnimRes(it.getGifId())
                    .show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        initManager();
        initView()
    }

    private fun initManager() {
        EditFaceManager.getInstance().start()

        RoomManager.getInstance().joinRoom(mRoomInfo.roomId,
            if (isRoomOwner()) RoomManager.Status.ACCEPT else RoomManager.Status.END,
            {
                if (it.find { it.userId.equals(mRoomInfo.userId) } == null) {
                    showRoomOwnerExitDialog()
                    return@joinRoom
                }
                RoomManager.getInstance().subscribeUserChangeEvent(
                    mRoomInfo.roomId,
                    WeakReference(userAddOrUpdateObserver),
                    WeakReference(userDeleteObserver)
                )
                RoomManager.getInstance()
                    .subscribeGiftReceiveEvent(mRoomInfo.roomId, WeakReference(giftReceiveObserver))
                joinRtcChannel()
                it.forEach { userInfo -> runOnUiThread { updateUserView(userInfo) } }
            },
            {
                runOnUiThread {
                    Toast.makeText(this@RoomDetailActivity, it.message, Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        )
    }

    private fun joinRtcChannel() {
        RtcManager.getInstance().joinChannel(
            mRoomInfo.roomId,
            RoomManager.getCacheUserId(),
            getString(R.string.rtc_app_token),
            false,
            object : RtcManager.OnChannelListener {
                override fun onError(code: Int, message: String?) {
                    runOnUiThread {
                        Toast.makeText(this@RoomDetailActivity, message, Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onJoinSuccess(channelId: String?, uid: Int) {
                    runOnUiThread {
                        mMsgAdapter.addMessage(
                            RoomManager.MessageInfo(
                                uid.toString() + "",
                                getString(R.string.live_room_message_user_join_suffix)
                            )
                        )
                    }
                }

                override fun onUserJoined(channelId: String?, uid: Int) {
                    runOnUiThread {
                        mMsgAdapter.addMessage(
                            RoomManager.MessageInfo(
                                uid.toString() + "",
                                getString(R.string.live_room_message_user_join_suffix)
                            )
                        )
                    }
                }

                override fun onUserOffline(channelId: String?, uid: Int) {
                    runOnUiThread {
                        mMsgAdapter.addMessage(
                            RoomManager.MessageInfo(
                                uid.toString() + "",
                                getString(R.string.live_room_message_user_left_suffix)
                            )
                        )
                    }
                }
            })
    }

    private fun updateUserView(userInfo: RoomManager.UserInfo) {
        if (isDestroyed) {
            return
        }
        when (userInfo.status) {
            RoomManager.Status.RAISING -> {
                // ??????????????????????????????+1
                if (isRoomOwner()) {
                    mBinding.liveBottomView.setFun4Dot(true)
                } else {
                    mBinding.liveBottomView.isFun4Activated = true
                }
            }
            RoomManager.Status.ACCEPT -> {
                // ????????????????????????
                var targetViewBinding =
                    seatLayouts.firstOrNull { viewBinidng ->
                        (viewBinidng.root.tag as? RoomManager.UserInfo)?.userId?.equals(userInfo.userId)
                            ?: false
                    }
                if (targetViewBinding == null) {
                    targetViewBinding =
                        seatLayouts.firstOrNull { viewBinidng -> viewBinidng.root.tag == null }
                            ?: return
                    if (userInfo.userId == RoomManager.getCacheUserId()) {
                        RtcManager.getInstance()
                            .renderLocalAvatarVideo(targetViewBinding.videoContainer)
                        RtcManager.getInstance().setPublishTracks(mRoomInfo.roomId, true)
                    } else {
                        RtcManager.getInstance().renderRemoteVideo(
                            targetViewBinding.videoContainer,
                            mRoomInfo.roomId,
                            userInfo.userId.toInt()
                        )
                    }
                    targetViewBinding.root.tag = userInfo
                }

                targetViewBinding.ivMicOff.isVisible = !userInfo.hasAudio
                targetViewBinding.tvName.text = userInfo.userName

            }
            RoomManager.Status.REFUSE,
            RoomManager.Status.END -> {
                seatLayouts.forEach { viewBinding ->
                    (viewBinding.root.tag as? RoomManager.UserInfo)?.let {
                        if (it.userId.equals(userInfo.userId)) {
                            viewBinding.videoContainer.removeAllViews()
                            viewBinding.tvName.text = ""
                            viewBinding.ivMicOff.isVisible = false
                            if (userInfo.userId == RoomManager.getCacheUserId()) {
                                RtcManager.getInstance().setPublishTracks(mRoomInfo.roomId, false)
                            }
                            return@forEach
                        }
                    }
                }
                if (!isRoomOwner()) {
                    // ????????????
                    mBinding.liveBottomView.isFun4Activated = false
                }
            }
        }
    }

    private fun initView() {
        val isMultiHost = mRoomInfo.roomType == RoomManager.RoomType.MULTI_HOST

        // ?????????
        mBinding.ivBack.setOnClickListener { onBackPressed() }
        mBinding.tvRoomName.text = "${mRoomInfo.roomName}(${mRoomInfo.roomId})"
        mBinding.tvUserCount.text = Random(System.currentTimeMillis()).nextInt(30, 300).toString()

        // ????????????
        mBinding.viewport2.root.isVisible = isMultiHost
        mBinding.viewport3.root.isVisible = isMultiHost
        mBinding.viewport4.root.isVisible = isMultiHost

        // ????????????
        mBinding.liveMessageListView.adapter = mMsgAdapter

        // ?????????
        mBinding.liveBottomView.apply {
            // ?????????????????????
            setupCloseBtn(false, null)
            // ????????????
            setupInputText(true) {
                TextInputDialog(this@RoomDetailActivity, true)
                    .setOnSendClickListener { dialog, text ->
                        // ??????????????????
                        mMsgAdapter.addMessage(RoomManager.MessageInfo("111234", text))
                    }
                    .show()
            }
            // ?????????
            setFun1Visible(true)
            setFun1ImageResource(R.drawable.room_detail_icon_mic)
            setFun1Background(null)
            isFun1Activated = true
            setFun1ClickListener {
                RtcManager.getInstance().muteLocalAudio(isFun1Activated)
                isFun1Activated = !isFun1Activated
            }
            // ??????
            setFun2Visible(true)
            setFun2ImageResource(R.drawable.room_detail_icon_magic)
            setFun2Background(null)
            setFun2ClickListener {
                startActivityForResult(
                    Intent(
                        this@RoomDetailActivity,
                        FaceEditActivity::class.java
                    ), ACTIVITY_RESULT_CODE_FACE_EDIT
                )
            }
            // ??????
            setupMoreBtn(true) {
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
            // ??????
            setFun3Visible(!isRoomOwner())
            setFun3ImageResource(R.drawable.room_detail_icon_gift)
            setFun3ClickListener {
                showGiftDialog()
            }
            // ??????
            setFun4Visible(isMultiHost)
            setFun4ImageResource(R.drawable.room_detail_icon_raisehand)
            isFun4Activated = isRoomOwner()
            setFun4Background(null)
            setFun4ClickListener {
                if (!isRoomOwner()) {
                    // ??????????????????
                    if (!isFun4Activated) {
                        RoomManager.getInstance()
                            .raiseHand(mRoomInfo.roomId, RoomManager.getInstance().localUserInfo)
                    } else {
                        RoomManager.getInstance()
                            .endUser(mRoomInfo.roomId, RoomManager.getInstance().localUserInfo)
                    }
                } else {
                    // ????????????
                    setFun4Dot(false)
                    RoomManager.getInstance().getUserList(mRoomInfo.roomId) { list ->
                        list.filter { user ->
                            user.status == RoomManager.Status.RAISING || user.status == RoomManager.Status.ACCEPT && !user.userId.equals(
                                mRoomInfo.userId
                            )
                        }
                            .let { filterList ->
                                runOnUiThread {
                                    if (filterList.isNotEmpty()) {
                                        showRaiseHandleUserDialog(filterList)
                                    } else {
                                        Toast.makeText(
                                            this@RoomDetailActivity,
                                            "No one raise hand.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                            }
                    }
                }
            }
        }
    }

    private fun showRoomOwnerExitDialog() {
        if (isDestroyed) {
            return
        }
        AlertDialog.Builder(this@RoomDetailActivity)
            .setTitle(R.string.common_tip)
            .setMessage("Homeowner has left")
            .setPositiveButton(R.string.common_sure) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RESULT_CODE_FACE_EDIT) {
            seatLayouts.find { (it.root.tag as? RoomManager.UserInfo)?.userId?.equals(RoomManager.getCacheUserId()) ?: false }?.let {
                RtcManager.getInstance().renderLocalAvatarVideo(it.videoContainer)
            }
        }
    }

    private fun showGiftDialog() {
        GiftGridDialog(this@RoomDetailActivity, true)
            .setOnGiftSendClickListener { dialog, item, position ->
                // ????????????
                dialog.dismiss()
                val giftInfo = RoomManager.GiftInfo()
                giftInfo.setIconNameById(item.icon_res)
                giftInfo.setGifNameById(item.anim_res)
                giftInfo.title = getString(item.name_res)
                giftInfo.coin = item.coin_point
                giftInfo.userId = RoomManager.getCacheUserId()
                RoomManager.getInstance().sendGift(mRoomInfo.roomId, giftInfo)
            }
            .show()
    }

    private fun showRaiseHandleUserDialog(list: List<RoomManager.UserInfo>) {
        val dialog = OnlineUserListDialog(this@RoomDetailActivity, true)
        dialog.setListTitle(getString(R.string.room_detail_raisehand_title))
        dialog.setListAdapter(object :
            OnlineUserListDialog.AbsListItemAdapter<RoomManager.UserInfo, RoomDetailRaisehandItemBinding>() {

            override fun onItemUpdate(
                holder: BindingViewHolder<RoomDetailRaisehandItemBinding>,
                position: Int,
                item: RoomManager.UserInfo
            ) {
                holder.binding.ivAvatar.setImageDrawable(
                    UIUtil.getRoundDrawable(
                        holder.binding.root.context,
                        RandomUtil.randomLiveRoomIcon(),
                        999.0f
                    )
                )
                holder.binding.tvUserName.text = item.userName

                holder.binding.btnRefuse.setOnClickListener {
                    // ????????????
                    RoomManager.getInstance().refuseUser(mRoomInfo.roomId, item)
                    dialog.dismiss()
                }
                holder.binding.btnAccept.setOnClickListener {
                    // ????????????
                    RoomManager.getInstance().acceptUser(mRoomInfo.roomId, item)
                    dialog.dismiss()
                }
                holder.binding.btnOffline.setOnClickListener {
                    // ??????
                    RoomManager.getInstance().endUser(mRoomInfo.roomId, item)
                    dialog.dismiss()
                }

                if (item.status == RoomManager.Status.RAISING) {
                    holder.binding.btnRefuse.isVisible = true
                    holder.binding.btnAccept.isVisible = true
                    holder.binding.btnOffline.isVisible = false
                } else {
                    holder.binding.btnRefuse.isVisible = false
                    holder.binding.btnAccept.isVisible = false
                    holder.binding.btnOffline.isVisible = true
                }
            }
        }.apply { resetAll(list) })
        dialog.show()
    }

    override fun onDestroy() {
        EditFaceManager.getInstance().stop()
        RtcManager.getInstance().reset(true)
        if (isRoomOwner()) {
            RoomManager.getInstance().destroyRoom(mRoomInfo.roomId)
        } else {
            RoomManager.getInstance().leaveRoom(mRoomInfo.roomId)
        }
        super.onDestroy()
    }

    private fun isRoomOwner() = mRoomInfo.userId == RoomManager.getCacheUserId()
}