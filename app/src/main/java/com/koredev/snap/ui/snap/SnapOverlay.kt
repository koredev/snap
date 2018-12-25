package com.koredev.snap.ui.snap

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.koredev.snap.lib.FrameMetadata
import com.koredev.snap.lib.VisionImageProcessor
import com.koredev.snapcamera.SnapCameraView
import java.nio.ByteBuffer
import android.util.Log

/**
 * A simple View providing a render callback to other classes.
 */
class SnapOverlay(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var previewWidth = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight = 0
    private var heightScaleFactor = 1.0f
    private var facing = SnapCameraView.FACING_BACK
    private val graphics = mutableListOf<Graphic>()
    private var processor: VisionImageProcessor? = null

    fun render(data: ByteBuffer, width: Int, height: Int, rotation: Int, facing: Int) {
        val metadata = FrameMetadata(
            width = width,
            height = height,
            rotation = rotation,
            facing = facing
        )
        processor?.process(data, metadata, this)
        postInvalidate()
    }

    fun start(cameraView: SnapCameraView) {
        val size = cameraView.previewSize
        val min = Math.min(size.width, size.height)
        val max = Math.max(size.width, size.height)
        if (isPortraitMode()) {
            // Swap width and height sizes when in portrait, since it will be rotated by 90 degrees
            setCameraInfo(min, max, cameraView.facing)
        } else {
            setCameraInfo(max, min, cameraView.facing)
        }
        clear()
    }

    fun setMachineLearningFrameProcessor(processor: VisionImageProcessor) {
        clear()
        this.processor?.stop()
        this.processor = processor
    }

    fun clear() {
        graphics.clear()
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        graphics.add(graphic)
    }

    fun remove(graphic: Graphic) {
        graphics.remove(graphic)
        postInvalidate()
    }

    private fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        this.facing = facing
        postInvalidate()
    }

    private fun isPortraitMode(): Boolean {
        val orientation = context.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true
        }

        Log.d(TAG, "isPortraitMode returning false by default")
        return false
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (previewWidth != 0 && previewHeight != 0) {
            widthScaleFactor = width.toFloat() / previewWidth.toFloat()
            heightScaleFactor = height.toFloat() / previewHeight.toFloat()
        }

        graphics.forEach { it.render(canvas) }
    }

    abstract class Graphic(val overlay: SnapOverlay) {
        abstract fun render(canvas: Canvas)

        fun scaleX(horizontal: Float): Float {
            return horizontal * overlay.widthScaleFactor
        }

        fun scaleY(vertical: Float): Float {
            return vertical * overlay.heightScaleFactor
        }

        fun translateX(x: Float): Float {
            return if (overlay.facing == SnapCameraView.FACING_FRONT) {
                overlay.width - scaleX(x)
            } else {
                scaleX(x)
            }
        }

        fun translateY(y: Float): Float {
            return scaleY(y)
        }

        fun postInvalidate() {
            overlay.postInvalidate()
        }
    }

    companion object {
        private const val TAG = "SnapOverlay"
    }
}