
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class PointCommand(var paint: Paint, point: PointF) : Command {

    var point = point; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.drawPoint(point.x, point.y, paint)
    }

    override fun freeResources() {
        // No resources to free
    }
}
