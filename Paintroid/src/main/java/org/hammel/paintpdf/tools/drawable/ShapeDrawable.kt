package org.hammel.paintpdf.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

interface ShapeDrawable {
    fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint)
}
