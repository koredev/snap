package com.koredev.snap.lib

import android.graphics.Bitmap
import com.koredev.snap.ui.snap.SnapOverlay
import java.nio.ByteBuffer

interface VisionImageProcessor {
    // Process images with ml
    fun process(data: ByteBuffer, metadata: FrameMetadata, overlay: SnapOverlay)

    // Process bitmap images
    fun process(bitmap: Bitmap, overlay: SnapOverlay)

    fun stop()
}