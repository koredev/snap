package com.koredev.snap.ui.snap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.koredev.snap.data.SnapRaw
import com.koredev.snap.data.source.SnapsRepository
import javax.inject.Inject

class SnapViewModel @Inject constructor(
    val repo: SnapsRepository
) : ViewModel() {

    private var _action: MutableLiveData<SnapAction> = MutableLiveData()
    val action: LiveData<SnapAction>
        get() = _action

    fun handleViewAction(action: SnapViewAction) {
        when (action) {
            is SnapViewAction.PhotoButtonPressed -> _action.postValue(SnapAction.TakePhoto)
            is SnapViewAction.ImageReady -> save(action.data)
        }
    }

    private fun save(image: ByteArray) {
        repo.saveSnap(SnapRaw(image))
        _action.postValue(SnapAction.ImageCaptured)
    }
}

sealed class SnapViewAction {
    object PhotoButtonPressed : SnapViewAction()
    data class ImageReady(val data: ByteArray) : SnapViewAction()
}

sealed class SnapAction {
    object TakePhoto : SnapAction()
    object ImageCaptured : SnapAction()
}
