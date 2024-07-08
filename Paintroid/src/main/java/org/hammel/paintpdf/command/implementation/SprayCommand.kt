package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class SprayCommand(
    val sprayedPoints: FloatArray,
    val paint: Paint
) : Command {

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        canvas.drawPoints(sprayedPoints, paint)
    }

    override fun freeResources() {
        // nothing to free
    }
}
