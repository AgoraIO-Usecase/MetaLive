package io.agora.metalive

import android.Manifest
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import io.agora.metalive.databinding.RoomListActivityBinding
import io.agora.metalive.databinding.RoomListCreateDialogViewBinding
import io.agora.metalive.manager.EditFaceManager
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RtcManager
import io.agora.uiwidget.function.RoomListView
import io.agora.uiwidget.utils.RandomUtil
import io.agora.uiwidget.utils.StatusBarUtil
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class RoomListActivity : AppCompatActivity() {

    companion object{
        private const val RC_CAMERA_AND_AUDIO = 100
    }

    private val mBinding by lazy {
        RoomListActivityBinding.inflate(LayoutInflater.from(this))
    }

    private var permissionGrandRun : (()->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.hideStatusBar(window, true)
        setContentView(mBinding.root)

        doOnInitialized(null)
        initView()
    }

    private fun initView() {
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
                holder.roomName.text = item.roomName
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

        mBinding.ivCreate.setOnClickListener {
            val dialogViewBinding =
                RoomListCreateDialogViewBinding.inflate(LayoutInflater.from(this))
            dialogViewBinding.ivRandom.setOnClickListener {
                dialogViewBinding.etRoomName.setText(RandomUtil.randomLiveRoomName(this))
            }
            AlertDialog.Builder(this)
                .setTitle(R.string.room_list_create_dialog_title)
                .setView(dialogViewBinding.root)
                .setPositiveButton(R.string.common_sure) { dialog, _ ->
                    Intent(this@RoomListActivity, RoomDetailActivity::class.java).apply {
                        startActivity(this)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(R.string.common_cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        mBinding.ivEditFace.setOnClickListener {
            doOnInitialized {
                startActivity(Intent(this, EditFaceActivity::class.java))
            }
        }
    }

    private fun doOnInitialized(run: (()->Unit)?){
        runOnPermissionGrand {
            EditFaceManager.getInstance().initialize(this)
            RtcManager.getInstance().init(this, getString(R.string.rtc_app_id), null)
            run?.invoke()
        }
    }

    private fun runOnPermissionGrand(run: ()->Unit){
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