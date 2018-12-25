package com.koredev.snap.lib

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import com.koredev.snap.ui.snap.SnapOverlay

class CameraImageGraphic(overlay: SnapOverlay, private val bitmap: Bitmap) : SnapOverlay.Graphic(overlay) {

    override fun render(canvas: Canvas) {
        canvas.drawBitmap(bitmap, null, Rect(0, 0, canvas.width, canvas.height), null)
    }
}