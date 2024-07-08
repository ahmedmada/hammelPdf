package org.hammel.paintpdf.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 8f
private const val CONSTANT_2 = 1.8f
private const val CONSTANT_3 = 3f
private const val CONSTANT_4 = 2f

class StarDrawable : ShapeDrawable {
    private val path = Path()
    private val paint = Paint()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        paint.set(drawPaint)
        val midWidth = shapeRect.width() / 2
        val midHeight = shapeRect.height() / 2
        val height = shapeRect.height()
        val width = shapeRect.width()
        path.run {
            reset()
            moveTo(midWidth, 0f)
            lineTo(midWidth + width / CONSTANT_1, midHeight - height / CONSTANT_1)
            lineTo(width, midHeight - height / CONSTANT_1)
            lineTo(midWidth + CONSTANT_2 * width / CONSTANT_1, midHeight + height / CONSTANT_1)
            lineTo(midWidth + CONSTANT_3 * width / CONSTANT_1, height)
            lineTo(midWidth, midHeight + CONSTANT_4 * height / CONSTANT_1)
            lineTo(midWidth - CONSTANT_3 * width / CONSTANT_1, height)
            lineTo(midWidth - CONSTANT_2 * width / CONSTANT_1, midHeight + height / CONSTANT_1)
            lineTo(0f, midHeight - height / CONSTANT_1)
            lineTo(midWidth - width / CONSTANT_1, midHeight - height / CONSTANT_1)
            lineTo(midWidth, 0f)
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, paint)
    }
}
