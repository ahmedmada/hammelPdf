
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.helper.FillAlgorithmFactory

class FillCommand(private val fillAlgorithmFactory: FillAlgorithmFactory, clickedPixel: Point, paint: Paint, colorTolerance: Float) : Command {

    var clickedPixel = clickedPixel; private set
    var paint = paint; private set
    var colorTolerance = colorTolerance; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val currentLayer = layerModel.currentLayer
        currentLayer ?: return
        currentLayer.bitmap.let { bitmap ->
            val colorToBeReplaced = bitmap.getPixel(clickedPixel.x, clickedPixel.y)
            val fillAlgorithm = fillAlgorithmFactory.createFillAlgorithm()
            fillAlgorithm.setParameters(
                    bitmap,
                    clickedPixel,
                    paint.color,
                    colorToBeReplaced,
                    colorTolerance
            )
            fillAlgorithm.performFilling()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
