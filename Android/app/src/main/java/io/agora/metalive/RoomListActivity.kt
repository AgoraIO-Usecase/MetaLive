package io.agora.metalive

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.databinding.RoomListActivityBinding
import io.agora.metalive.databinding.RoomListItemBinding
import io.agora.metalive.manager.EditFaceManager
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.basic.BindingViewHolder
import io.agora.uiwidget.function.RoomListView
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil
import io.agora.uiwidget.utils.UIUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class RoomListActivity : AppCompatActivity() {

    companion object {
        private const val RC_CAMERA_AND_AUDIO = 100
    }

    private val mBinding by lazy {
        RoomListActivityBinding.inflate(LayoutInflater.from(this))
    }

    private var permissionGrandRun: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        doOnInitialized()
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
            doOnInitialized {
                startActivity(Intent(this, PreviewActivity::class.java))
            }
        }

        mBinding.ivEditFace.setOnClickListener {
            doOnInitialized {
                startActivity(Intent(this, FaceEditActivity::class.java))
            }
        }
    }

    private fun doOnInitialized(run: (() -> Unit)? = null) {
        RoomManager.getInstance()
            .init(this, getString(R.string.rtm_app_id), getString(R.string.rtm_app_token))
        runOnPermissionGrand {
            EditFaceManager.getInstance().initialize(this)
            RtcManager.getInstance().init(this, getString(R.string.rtc_app_id), null)
            run?.invoke()
        }
    }

    private fun runOnPermissionGrand(run: () -> Unit) {
        permissionGrandRun = run
        requestPermission()
    }

    @AfterPermissionGranted(RC_CAMERA_AND_AUDIO)
    private fun requestPermission() {
        val perms =
            arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            permissionGrandRun?.invoke()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, getString(R.string.room_list_permission_request_camera_audio),
                RC_CAMERA_AND_AUDIO, *perms
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


}