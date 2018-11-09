package com.jameswoo.snapcamera

import android.annotation.TargetApi
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraDevice.TEMPLATE_MANUAL
import android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW
import android.hardware.camera2.CameraDevice.TEMPLATE_RECORD
import android.hardware.camera2.CameraDevice.TEMPLATE_STILL_CAPTURE
import android.hardware.camera2.CameraDevice.TEMPLATE_VIDEO_SNAPSHOT
import android.hardware.camera2.CameraDevice.TEMPLATE_ZERO_SHUTTER_LAG
import android.hardware.camera2.CaptureRequest
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.O
import android.os.Handler
import android.view.Surface
import androidx.annotation.RequiresApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consume

class SnapCameraCaptureSession internal constructor(
    private val cameraDevice: CameraDevice,
    private val handler: Handler?
) : AutoCloseable {

    sealed class State {
        sealed class Configured : State() {
            companion object : Configured()
            sealed class InputQueueEmpty : Configured() {
                @TargetApi(O) companion object : InputQueueEmpty()
                object Ready : InputQueueEmpty()
            }

            object Active : Configured()
        }

        sealed class Closed : State() {
            companion object : Closed()
            object ConfigureFailed : Closed()
        }
    }

    val stateChannel = ConflatedBroadcastChannel<State>()
    private val preparedSurfaceChannel = Channel<Surface>()

    private var captureSession: CameraCaptureSession? = null

    @RequiresApi(M) suspend fun prepareSurface(surface: Surface) {
        captureSession?.prepare(surface) ?: noSessionException
        check(preparedSurfaceChannel.receive() === surface)
    }

    internal val sessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) = stateCallback(session, State.Configured)
        override fun onCaptureQueueEmpty(session: CameraCaptureSession) = stateCallback(session, State.Configured.InputQueueEmpty)
        override fun onReady(session: CameraCaptureSession) = stateCallback(session, State.Configured.InputQueueEmpty.Ready)
        override fun onActive(session: CameraCaptureSession) = stateCallback(session, State.Configured.Active)

        override fun onSurfacePrepared(session: CameraCaptureSession, surface: Surface) {
            preparedSurfaceChannel.offer(surface)
        }

        override fun onConfigureFailed(session: CameraCaptureSession) = stateCallback(session, State.Closed.ConfigureFailed)
        override fun onClosed(session: CameraCaptureSession) {
            stateCallback(session, State.Closed)
            stateChannel.close()
        }
    }

    /**
     * Resumes on any [CamCaptureSession.State.Configured] state, and throws a
     * [CamCaptureSessionStateException] if a [CamCaptureSession.State.Closed] is received instead.
     */
    @Throws(CameraCaptureSessionStateException::class)
    suspend fun awaitConfiguredState() {
        stateChannel.consume {
            loop@ for (state in this) {
                when (state) {
                    is SnapCameraCaptureSession.State.Configured -> break@loop
                    is SnapCameraCaptureSession.State.Closed -> throw CameraCaptureSessionStateException(state)
                }
            }
        }
    }

    inline fun createCaptureRequest(
        template: SnapCameraTemplate,
        block: (CaptureRequest.Builder) -> Unit
    ): CaptureRequest = createCaptureRequestBuilder(template).also(block).build()

    @PublishedApi
    internal fun createCaptureRequestBuilder(template: SnapCameraTemplate): CaptureRequest.Builder {
        return cameraDevice.createCaptureRequest(when (template) {
            SnapCameraTemplate.PREVIEW -> TEMPLATE_PREVIEW
            SnapCameraTemplate.STILL_CAPTURE -> TEMPLATE_STILL_CAPTURE
            SnapCameraTemplate.RECORD -> TEMPLATE_RECORD
            SnapCameraTemplate.VIDEO_SNAPSHOT -> TEMPLATE_VIDEO_SNAPSHOT
            SnapCameraTemplate.ZERO_SHUTTER_LAG -> TEMPLATE_ZERO_SHUTTER_LAG
            SnapCameraTemplate.MANUAL -> TEMPLATE_MANUAL
        })
    }

    fun setRepeatingRequest(
        request: CaptureRequest,
        captureCallback: CameraCaptureSession.CaptureCallback? = null
    ) = captureSession?.setRepeatingRequest(request, captureCallback, handler) ?: noSessionException

    fun stopRepeating() = captureSession?.stopRepeating() ?: noSessionException

    private fun stateCallback(session: CameraCaptureSession, newState: State) {
        check(session.device == cameraDevice) {
            "The same callback has been used for different cameras! Expected: $cameraDevice but " +
                "got :${session.device}"
        }
        captureSession = session
        stateChannel.offer(newState)
    }

    private val noSessionException: Nothing get() = throw IllegalStateException("No capture session!")

    override fun close() {
        captureSession?.close()
        preparedSurfaceChannel.close()
    }
}