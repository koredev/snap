package com.jameswoo.snapcamera

sealed class CameraStateException(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

class SnapCameraStateException(val nonOpenState: SnapCameraState) : CameraStateException() {

    init {
        require(nonOpenState != SnapCameraState.Opened)
    }

    override val message = "$nonOpenState"
}

class CameraCaptureSessionStateException(val state: SnapCameraCaptureSession.State.Closed) : CameraStateException() {
    override val message = "$state"
}