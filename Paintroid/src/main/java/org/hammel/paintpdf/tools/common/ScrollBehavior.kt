package org.hammel.paintpdf.tools.common

import android.graphics.Point

interface ScrollBehavior {
    fun getScrollDirection(pointX: Float, pointY: Float, viewWidth: Int, viewHeight: Int): Point
}
