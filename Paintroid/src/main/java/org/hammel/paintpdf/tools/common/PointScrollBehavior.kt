package org.hammel.paintpdf.tools.common

import android.graphics.Point

class PointScrollBehavior(private val scrollTolerance: Int) : ScrollBehavior {
    override fun getScrollDirection(
        pointX: Float,
        pointY: Float,
        viewWidth: Int,
        viewHeight: Int
    ): Point {
        var deltaX = 0
        var deltaY = 0
        if (pointX < scrollTolerance) {
            deltaX = 1
        }
        if (pointX > viewWidth - scrollTolerance) {
            deltaX = -1
        }
        if (pointY < scrollTolerance) {
            deltaY = 1
        }
        if (pointY > viewHeight - scrollTolerance) {
            deltaY = -1
        }
        return Point(deltaX, deltaY)
    }
}
