package com.koredev.snap.lib

import android.graphics.Bitmap
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.koredev.snap.ui.snap.SnapOverlay
import com.koredev.snap.util.BitmapUtil
import java.nio.ByteBuffer

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(FirebaseVisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T> : VisionImageProcessor {
    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null
    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null
    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null
    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    @Synchronized
    override fun process(data: ByteBuffer, metadata: FrameMetadata, overlay: SnapOverlay) {
        latestImage = data
        latestImageMetaData = metadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(overlay)
        }
    }

    // Bitmap version
    override fun process(bitmap: Bitmap, overlay: SnapOverlay) {
        detectInVisionImage(null, FirebaseVisionImage.fromBitmap(bitmap), null, overlay)
    }

    @Synchronized
    private fun processLatestImage(overlay: SnapOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null) {
            processImage(processingImage!!, processingMetaData!!, overlay)
        }
    }

    private fun processImage(data: ByteBuffer, metadata: FrameMetadata, overlay: SnapOverlay) {
        val fireMetadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(metadata.width)
            .setHeight(metadata.height)
            .setRotation(metadata.rotation)
            .build()
        val bitmap = BitmapUtil.getBitmap(data, metadata)
        detectInVisionImage(bitmap, FirebaseVisionImage.fromByteBuffer(data, fireMetadata), metadata, overlay)
    }

    private fun detectInVisionImage(
        originalCameraImage: Bitmap?,
        image: FirebaseVisionImage,
        metadata: FrameMetadata?,
        overlay: SnapOverlay
    ) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                onSuccess(originalCameraImage, results, metadata!!, overlay)
                processLatestImage(overlay)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun stop() {}

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>

    /**
     * Callback that executes with a successful detection result.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background
     * image.
     */
    protected abstract fun onSuccess(
        originalCameraImage: Bitmap?,
        results: T,
        metadata: FrameMetadata,
        overlay: SnapOverlay
    )

    protected abstract fun onFailure(e: Exception)
}