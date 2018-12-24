package com.koredev.snap.ui.snap

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.koredev.snap.R
import com.koredev.snap.di.Injectable
import com.koredev.snap.util.hideSystemUi
import com.koredev.snap.util.requestPermission
import com.koredev.snapcamera.SnapCameraView
import kotlinx.android.synthetic.main.snap_fragment.*
import javax.inject.Inject

class SnapFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var snapViewModel: SnapViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        snapViewModel = ViewModelProviders.of(this, viewModelFactory).get(SnapViewModel::class.java)
        hideSystemUi()
        return inflater.inflate(R.layout.snap_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snapViewModel.action.observe(this, Observer {
            handleAction(it)
        })
        cameraButton.setOnClickListener {
            snapViewModel.handleViewAction(SnapViewAction.PhotoButtonPressed)
        }
        cameraView.addCallback(object: SnapCameraView.Callback() {
            override fun onPictureTaken(cameraView: SnapCameraView, data: ByteArray) {
                snapViewModel.handleViewAction(SnapViewAction.ImageReady(data))
            }
            override fun onFramePreview(cameraView: SnapCameraView, data: ByteArray, width: Int, height: Int, orientation: Int) {
                snapViewModel.handleViewAction(SnapViewAction.ProcessFrame(data, width, height, orientation))
            }
        })

        overlay.add(BoxGraphic())
    }

    override fun onResume() {
        super.onResume()
        requestPermission(
            permission = Manifest.permission.CAMERA,
            onSuccess = { cameraView.start() },
            onError = { cameraView.stop() }
        )
    }

    override fun onPause() {
        cameraView.stop()
        super.onPause()
    }

    private fun handleAction(action: SnapAction) {
        when (action) {
            is SnapAction.TakePhoto -> cameraView.takePicture()
            is SnapAction.CaptureImage -> Toast.makeText(context, "Image Captured", Toast.LENGTH_SHORT).show()
            is SnapAction.RequestRender -> overlay.render()
        }
    }
}