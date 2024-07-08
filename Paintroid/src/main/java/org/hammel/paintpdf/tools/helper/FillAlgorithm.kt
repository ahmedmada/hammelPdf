package org.hammel.paintpdf.tools.helper

import android.graphics.Bitmap
import android.graphics.Point

interface FillAlgorithm {
    fun setParameters(
        bitmap: Bitmap,
        clickedPixel: Point,
        targetColor: Int,
        replacementColor: Int,
        colorToleranceThreshold: Float
    )

    fun performFilling()
}
