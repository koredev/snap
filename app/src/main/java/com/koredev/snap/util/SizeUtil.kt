package com.koredev.snap.util

import android.util.Size
import com.koredev.snap.util.PreviewSize.DESIRED_PREVIEW_SIZE
import com.koredev.snap.util.PreviewSize.MINIMUM_PREVIEW_SIZE
import java.util.Collections

object PreviewSize {
    val DESIRED_PREVIEW_SIZE = Size(640, 480)
    const val MINIMUM_PREVIEW_SIZE = 320
}

fun Array<Size>.chooseOptimalSize(): Size {
    val minSize = Math.max(
        Math.min(
            DESIRED_PREVIEW_SIZE.width,
            DESIRED_PREVIEW_SIZE.height
        ), MINIMUM_PREVIEW_SIZE
    )

    // Collect the supported resolutions that are at least as big as the preview Surface
    val bigEnough = ArrayList<Size>()
    for (option in this) {
        if (option == DESIRED_PREVIEW_SIZE) {
            return DESIRED_PREVIEW_SIZE
        }

        if (option.height >= minSize && option.width >= minSize) {
            bigEnough.add(option)
        }
    }

    // Pick the smallest of those, assuming we found any
    return if (bigEnough.size > 0) {
        Collections.min(bigEnough) { lhs, rhs ->
            // We cast here to ensure the multiplications won't overflow
            java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }
    } else {
        this[0]
    }
}
