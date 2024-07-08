package org.hammel.paintpdf.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class RectangleDrawable : ShapeDrawable {
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        canvas.drawRect(shapeRect, drawPaint)
    }
}
