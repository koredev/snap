package com.koredev.snap.util

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Fragment.requestCameraPermissions() {
    GlobalScope.launch {
        val granted = suspendCoroutine<Boolean> {
            Dexter.withActivity(activity)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        it.resume(true)
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        val builder = AlertDialog.Builder(activity)
                        builder.setTitle("Need Permissions")
                        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                        builder.setPositiveButton("GOTO SETTINGS") { dialog, _ ->
                            dialog.cancel()
                            openSettings()
                        }
                        builder.setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                            it.resume(false)
                        }
                        builder.show()
                        if (response.isPermanentlyDenied) {
                            openSettings()
                            it.resume(false)
                        }
                    }
                }).check()
        }

        if (!granted) {
            activity?.finish()
        }
    }
}

private fun Fragment.openSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context?.packageName, null)
    intent.data = uri
    startActivityForResult(intent, 101)
}