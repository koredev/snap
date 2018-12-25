package com.koredev.snap.ui.snap.facedetection

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.koredev.snap.lib.CameraImageGraphic
import com.koredev.snap.lib.FrameMetadata
import com.koredev.snap.lib.VisionProcessorBase
import com.koredev.snap.ui.snap.SnapOverlay
import java.io.IOException

class FaceDetectionProcessor : VisionProcessorBase<List<FirebaseVisionFace>>() {

    private val detector: FirebaseVisionFaceDetector

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        results: List<FirebaseVisionFace>,
        metadata: FrameMetadata,
        overlay: SnapOverlay
    ) {
        overlay.clear()
        val imageGraphic = CameraImageGraphic(overlay, originalCameraImage!!)
        overlay.add(imageGraphic)
        for (i in results.indices) {
            val face = results[i]
            val cameraFacing = metadata.facing
            val faceGraphic = FaceGraphic(overlay, face, cameraFacing)
            overlay.add(faceGraphic)
        }
        overlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private val TAG = "FaceDetectionProcessor"
    }
}