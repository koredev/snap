package com.koredev.snap.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.view.Surface
import com.koredev.snap.lib.FrameMetadata
import com.koredev.snapcamera.SnapCameraView
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object BitmapUtil {
    fun getBitmap(data: ByteBuffer, metadata: FrameMetadata): Bitmap? {
        data.rewind()
        val imageInBuffer = ByteArray(data.limit())
        data.get(imageInBuffer, 0, imageInBuffer.size)
        try {
            val image = YuvImage(imageInBuffer, ImageFormat.NV21, metadata.width, metadata.height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(Rect(0, 0, metadata.width, metadata.height), 80, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            stream.close()
            return rotateBitmap(bmp, metadata.rotation, metadata.facing)
        } catch (e: Exception) {
            Log.e("VisionProcessorBase", "Error: " + e.message)
        }

        return null
    }

    private fun rotateBitmap(bitmap: Bitmap, rotation: Int, facing: Int): Bitmap {
        val matrix = Matrix()
        val rotationDegree = when (rotation) {
            Surface.ROTATION_90 -> 90f
            Surface.ROTATION_180 -> 180f
            Surface.ROTATION_270 -> 270f
            else -> 0f
        }
        // Rotate the image back to straight.
        matrix.postRotate(rotationDegree)
        // Mirror the image along X axis for front-facing camera image.
        if (facing == SnapCameraView.FACING_FRONT) {
            matrix.postScale(-1.0f, 1.0f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}