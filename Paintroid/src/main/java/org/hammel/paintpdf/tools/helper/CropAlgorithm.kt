package org.hammel.paintpdf.tools.helper

import android.graphics.Bitmap
import android.graphics.Rect

interface CropAlgorithm {
    fun crop(bitmap: Bitmap?): Rect?
}
