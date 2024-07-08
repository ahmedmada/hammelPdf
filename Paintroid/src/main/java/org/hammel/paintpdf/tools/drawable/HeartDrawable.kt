package org.hammel.paintpdf.tools.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

private const val CONSTANT_1 = 8f
private const val CONSTANT_2 = 0.2f
private const val CONSTANT_3 = 0.8f
private const val CONSTANT_4 = 1.2f
private const val CONSTANT_5 = 1.5f
private const val CONSTANT_6 = 4.5f
private const val CONSTANT_7 = 7.2f

class HeartDrawable : ShapeDrawable {
    private val path = Path()
    private val paint = Paint()
    override fun draw(canvas: Canvas, shapeRect: RectF, drawPaint: Paint) {
        paint.set(drawPaint)
        val midWidth = shapeRect.width() / 2
        val height = shapeRect.height()
        val width = shapeRect.width()
        path.run {
            reset()
            moveTo(midWidth, height)
            var x1 = -CONSTANT_2 * width
            var x2 = CONSTANT_3 * width / CONSTANT_1
            val y1 = CONSTANT_6 * height / CONSTANT_1
            val y2 = -CONSTANT_5 * height / CONSTANT_1
            cubicTo(
                x1, y1,
                x2, y2,
                midWidth, -y2
            )
            x1 = CONSTANT_7 * width / CONSTANT_1
            x2 = CONSTANT_4 * width
            cubicTo(
                x1, y2,
                x2, y1,
                midWidth, height
            )
            close()
            offset(shapeRect.left, shapeRect.top)
        }
        canvas.drawPath(path, paint)
    }
}
