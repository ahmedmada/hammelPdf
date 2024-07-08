package org.hammel.paintpdf.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class OvalDrawable : ShapeDrawable {
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        canvas.drawOval(shapeRect, drawPaint)
    }
}
