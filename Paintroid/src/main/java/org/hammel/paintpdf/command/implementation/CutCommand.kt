package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class CutCommand(
    val toolPosition: Point,
    val boxWidth: Float,
    val boxHeight: Float,
    val boxRotation: Float
) : Command {
    private val boxRect = RectF(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f, boxHeight / 2f)
    private val paint: Paint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        alpha = 0
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.save()
        canvas.translate(toolPosition.x.toFloat(), toolPosition.y.toFloat())
        canvas.rotate(boxRotation)
        canvas.drawRect(boxRect, paint)
        canvas.restore()
    }

    override fun freeResources() {
        // No resources to free
    }
}
