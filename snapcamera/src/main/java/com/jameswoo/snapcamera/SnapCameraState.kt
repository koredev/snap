package com.jameswoo.snapcamera

import android.hardware.camera2.CameraDevice

sealed class SnapCameraState {
    object Opened : SnapCameraState()
    class Error(val errorCode: Int) : SnapCameraState() {
        fun errorString(): String = when (errorCode) {
            CameraDevice.StateCallback.ERROR_CAMERA_IN_USE -> "ERROR_CAMERA_IN_USE"
            CameraDevice.StateCallback.ERROR_MAX_CAMERAS_IN_USE -> "ERROR_MAX_CAMERAS_IN_USE"
            CameraDevice.StateCallback.ERROR_CAMERA_DISABLED -> "ERROR_CAMERA_DISABLED"
            CameraDevice.StateCallback.ERROR_CAMERA_DEVICE -> "ERROR_CAMERA_DEVICE"
            CameraDevice.StateCallback.ERROR_CAMERA_SERVICE -> "ERROR_CAMERA_SERVICE"
            else -> "Unknown error state: $errorCode"
        }

        override fun toString() = errorString()
    }

    object Disconnected : SnapCameraState()
    object Closed : SnapCameraState()
}