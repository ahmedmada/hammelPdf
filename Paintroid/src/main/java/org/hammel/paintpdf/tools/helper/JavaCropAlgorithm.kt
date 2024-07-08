package org.hammel.paintpdf.tools.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect

class JavaCropAlgorithm : CropAlgorithm {
    private lateinit var bounds: Rect
    private lateinit var bitmap: Bitmap

    private fun Rect.yRange() = this.top..this.bottom

    private fun Rect.xRange() = this.left..this.right

    private fun isOpaquePixel(x: Int, y: Int): Boolean = bitmap.getPixel(x, y) != Color.TRANSPARENT

    fun getTopmostCellWithOpaquePixel(): Int? =
        bounds.yRange().firstOrNull { y ->
            bounds.xRange()
                .firstOrNull { x -> isOpaquePixel(x, y) } != null
        }

    fun getBottommostCellWithOpaquePixel(): Int? =
        bounds.yRange().lastOrNull { y ->
            bounds.xRange()
                .firstOrNull { x -> isOpaquePixel(x, y) } != null
        }

    fun getLeftmostCellWithOpaquePixel(): Int? =
        bounds.xRange().firstOrNull { x ->
            bounds.yRange()
                .firstOrNull { y -> isOpaquePixel(x, y) } != null
        }

    fun getRightmostCellWithOpaquePixel(): Int? =
        bounds.xRange().lastOrNull { x ->
            bounds.yRange()
                .firstOrNull { y -> isOpaquePixel(x, y) } != null
        }

    fun init(bitmap: Bitmap) {
        this.bitmap = bitmap
        bounds = Rect(0, 0, bitmap.width - 1, bitmap.height - 1)
    }

    override fun crop(bitmap: Bitmap?): Rect? {
        this.bitmap = bitmap ?: return null
        bounds = Rect(0, 0, bitmap.width - 1, bitmap.height - 1)

        bounds.top = getTopmostCellWithOpaquePixel() ?: return null
        bounds.bottom = getBottommostCellWithOpaquePixel() ?: bounds.top - 1
        bounds.left = getLeftmostCellWithOpaquePixel() ?: bounds.right + 1
        bounds.right = getRightmostCellWithOpaquePixel() ?: bounds.left - 1

        return bounds
    }
}
