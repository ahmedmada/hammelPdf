package org.hammel.paintpdf.tools.helper

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect

class SetCenterAlgorithm(javaCropAlgorithm: JavaCropAlgorithm) {
    private var cropAlgorithm: JavaCropAlgorithm = javaCropAlgorithm

    private lateinit var bounds: Rect
    private lateinit var position: PointF
    private lateinit var bitmap: Bitmap

    private fun getDistanceLeft(): Int {
        val leftmostCell = cropAlgorithm.getLeftmostCellWithOpaquePixel() ?: return Int.MIN_VALUE
        return this.position.x.toInt() - leftmostCell
    }

    private fun getDistanceRight(): Int {
        val rightmostCell = cropAlgorithm.getRightmostCellWithOpaquePixel() ?: return Int.MIN_VALUE
        return rightmostCell - this.position.x.toInt()
    }

    private fun getDistanceTop(): Int {
        val topmostCell = cropAlgorithm.getTopmostCellWithOpaquePixel() ?: return Int.MIN_VALUE
        return this.position.y.toInt() - topmostCell
    }

    private fun getDistanceBottom(): Int {
        val bottommostCell =
            cropAlgorithm.getBottommostCellWithOpaquePixel() ?: return Int.MIN_VALUE
        return bottommostCell - this.position.y.toInt()
    }

    private fun checkIfNothingFound(): Boolean =
        cropAlgorithm.getLeftmostCellWithOpaquePixel() == null

    fun crop(bitmap: Bitmap?, position: PointF?): Rect? {
        this.bitmap = bitmap ?: return null
        this.position = position ?: return null
        cropAlgorithm.init(bitmap)

        if (checkIfNothingFound()) return null

        val distanceLeft = getDistanceLeft()
        val distanceRight = getDistanceRight()
        val distanceTop = getDistanceTop()
        val distanceBottom = getDistanceBottom()

        bounds = Rect(0, 0, 0, 0)

        val distanceVertical = if (distanceTop > distanceBottom) distanceTop else distanceBottom
        val distanceHorizontal = if (distanceLeft > distanceRight) distanceLeft else distanceRight

        bounds.top = position.y.toInt() - distanceVertical
        bounds.bottom = position.y.toInt() + distanceVertical
        bounds.left = position.x.toInt() - distanceHorizontal
        bounds.right = position.x.toInt() + distanceHorizontal

        return bounds
    }
}
