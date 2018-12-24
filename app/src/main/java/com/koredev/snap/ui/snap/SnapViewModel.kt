package com.koredev.snap.ui.snap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.koredev.snap.data.SnapRaw
import com.koredev.snap.data.source.SnapsRepository
import com.koredev.snapcamera.SnapCameraView
import timber.log.Timber
import javax.inject.Inject

class SnapViewModel @Inject constructor(
    val repo: SnapsRepository
) : ViewModel() {

    private var _action: MutableLiveData<SnapAction> = MutableLiveData()
    val action: LiveData<SnapAction>
        get() = _action

    private var isProcessing = false

    fun handleViewAction(action: SnapViewAction) {
        when (action) {
            is SnapViewAction.PhotoButtonPressed -> _action.postValue(SnapAction.TakePhoto)
            is SnapViewAction.ImageReady -> save(action.data)
            is SnapViewAction.ProcessFrame -> process(action.data, action.width, action.height, action.orientation)
        }
    }

    private fun save(image: ByteArray) {
        repo.saveSnap(SnapRaw(image))
        _action.postValue(SnapAction.CaptureImage)
    }

    private fun process(data: ByteArray, width: Int, height: Int, orientation: Int) {
        if (isProcessing) {
            Timber.w("Dropping frame!")
            return
        }

        val rgbFrameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        _action.postValue(SnapAction.RequestRender)
    }
}

sealed class SnapViewAction {
    object PhotoButtonPressed : SnapViewAction()
    data class ImageReady(val data: ByteArray) : SnapViewAction()
    data class ProcessFrame(val data: ByteArray, val width: Int, val height: Int, val orientation: Int) : SnapViewAction()
}

sealed class SnapAction {
    object TakePhoto : SnapAction()
    object CaptureImage : SnapAction()
    object RequestRender : SnapAction()
}
