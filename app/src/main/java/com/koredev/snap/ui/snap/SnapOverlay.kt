package com.koredev.snap.ui.snap

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.koredev.snapcamera.SnapCameraView

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

    fun render() {
        postInvalidate()
    }

    fun scaleX(horizontal: Float): Float {
        return horizontal * widthScaleFactor
    }

    fun scaleY(vertical: Float): Float {
        return vertical * heightScaleFactor
    }

    fun translateX(x: Float): Float {
        return if (facing == SnapCameraView.FACING_FRONT) {
            width - scaleX(x)
        } else {
            scaleX(x)
        }
    }

    fun translateY(y: Float): Float {
        return scaleY(y)
    }


    @Synchronized
    fun clear() {
        graphics.clear()
        postInvalidate()
    }

    @Synchronized
    fun add(graphic: Graphic) {
        graphics.add(graphic)
    }

    @Synchronized
    fun remove(graphic: Graphic) {
        graphics.remove(graphic)
        postInvalidate()
    }

    @Synchronized
    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int) {
        this.previewWidth = previewWidth
        this.previewHeight = previewHeight
        this.facing = facing
        postInvalidate()
    }

    @Synchronized
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (previewWidth != 0 && previewHeight != 0) {
            widthScaleFactor = (canvas.width / previewWidth).toFloat()
            heightScaleFactor = (canvas.height / previewHeight).toFloat()
        }

        graphics.forEach { it.render(canvas) }
    }
}