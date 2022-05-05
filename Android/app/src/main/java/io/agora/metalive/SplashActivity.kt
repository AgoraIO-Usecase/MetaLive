package io.agora.metalive

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.agora.metalive.manager.AvatarConfigManager
import io.agora.metalive.manager.RoomManager
import io.agora.metalive.manager.RtcManager
import pub.devrel.easypermissions.EasyPermissions

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        private const val RC_CAMERA_AND_AUDIO = 100
        private const val RC_ROOM_LIST = 101
    }

    private var permissionGrandRun: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        doOnInitialized {
            startActivityForResult(Intent(this, RoomListActivity::class.java), RC_ROOM_LIST)
        }
    }

    private fun doOnInitialized(run: () -> Unit) {
        runOnPermissionGrand {
            var valid = true
            val rtmAppId: String = getString(R.string.rtm_app_id)
            if (rtmAppId.isBlank()) {
                valid = false
            }

            val rtcAppId: String = getString(R.string.rtc_app_id)
            if (rtcAppId.isBlank()) {
                valid = false
            }

            var rtmToken: String? = getString(R.string.rtm_app_token)
            if (rtmToken?.isBlank() == true) {
                rtmToken = null
            }

            if (valid) {
                RtcManager.getInstance()
                    .registerAvatarEventHandler(AvatarConfigManager.getInstance())
                RtcManager.getInstance().init(this, rtcAppId, null)

                if (!RoomManager.getInstance()
                        .init(this, rtmAppId, rtmToken) { runOnUiThread(run) }
                ) {
                    run.invoke()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle(R.string.common_tip)
                    .setMessage("RtcAppId and RtmAppId are needed!")
                    .setPositiveButton(R.string.common_sure) { dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()
            }
        }
    }

    private fun runOnPermissionGrand(run: () -> Unit) {
        permissionGrandRun = run
        requestPermission()
    }

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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // Some permissions have been granted
        if (requestCode == RC_CAMERA_AND_AUDIO) {
            permissionGrandRun?.invoke()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // Some permissions have been denied
        if (requestCode == RC_CAMERA_AND_AUDIO) {
            AlertDialog.Builder(this)
                .setTitle(R.string.common_tip)
                .setMessage("Camera and Audio Permissions are needed!")
                .setPositiveButton(R.string.common_sure) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    finish()
                }
                .show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ROOM_LIST) {
            finish()
        }
    }


}