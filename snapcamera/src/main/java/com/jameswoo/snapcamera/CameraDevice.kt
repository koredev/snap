package com.jameswoo.snapcamera

import android.hardware.camera2.CameraDevice
import android.view.Surface
import kotlin.coroutines.coroutineContext

suspend fun CameraDevice.createAndUseSession(
    outputs: List<Surface>,
    block: suspend (SnapCameraCaptureSession) -> Unit
) {
    val handler = coroutineContext.requireHandler()
    SnapCameraCaptureSession(this, handler).use {
        createCaptureSession(outputs, it.sessionStateCallback, handler)
        block(it)
    }
}
