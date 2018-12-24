package com.koredev.snap.ui.snap

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class BoxGraphic : Graphic {

    private val paint = Paint()

    override fun render(canvas: Canvas) {
        paint.color = Color.RED
        canvas.drawRect(100f,100f,200f,200f, paint)
    }
}