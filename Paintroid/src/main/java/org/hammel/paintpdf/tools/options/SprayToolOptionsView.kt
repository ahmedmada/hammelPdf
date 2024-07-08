package org.hammel.paintpdf.tools.options

import android.graphics.Paint

interface SprayToolOptionsView {
    fun setCallback(callback: Callback?)

    fun setRadius(radius: Int)

    fun setCurrentPaint(paint: Paint)

    fun getRadius(): Float

    interface Callback {
        fun radiusChanged(radius: Int)
    }
}
