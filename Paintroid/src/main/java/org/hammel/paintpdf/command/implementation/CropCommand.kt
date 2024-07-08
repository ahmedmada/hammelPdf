
package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class CropCommand(
    resizeCoordinateXLeft: Int,
    resizeCoordinateYTop: Int,
    resizeCoordinateXRight: Int,
    resizeCoordinateYBottom: Int,
    maximumBitmapResolution: Int
) : Command {

    var resizeCoordinateXLeft = resizeCoordinateXLeft; private set
    var resizeCoordinateYTop = resizeCoordinateYTop; private set
    var resizeCoordinateXRight = resizeCoordinateXRight; private set
    var resizeCoordinateYBottom = resizeCoordinateYBottom; private set
    var maximumBitmapResolution = maximumBitmapResolution; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        if (resizeCoordinateXRight < resizeCoordinateXLeft || resizeCoordinateYBottom < resizeCoordinateYTop) {
            return
        }
        if (resizeCoordinateXLeft >= layerModel.width || resizeCoordinateXRight < 0 || resizeCoordinateYTop >= layerModel.height || resizeCoordinateYBottom < 0) {
            return
        }
        if (resizeCoordinateXLeft == 0 && resizeCoordinateXRight == layerModel.width - 1 && resizeCoordinateYBottom == layerModel.height - 1 && resizeCoordinateYTop == 0) {
            return
        }
        if ((resizeCoordinateXRight + 1 - resizeCoordinateXLeft) * (resizeCoordinateYBottom + 1 - resizeCoordinateYTop) > maximumBitmapResolution) {
            return
        }
        val width = resizeCoordinateXRight + 1 - resizeCoordinateXLeft
        val height = resizeCoordinateYBottom + 1 - resizeCoordinateYTop
        val iterator = layerModel.listIterator(0)
        while (iterator.hasNext()) {
            val currentLayer = iterator.next()
            val currentBitmap = currentLayer.bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val resizedBitmap = Bitmap.createBitmap(width, height, currentBitmap.config)
            val resizedCanvas = Canvas(resizedBitmap)
            resizedCanvas.drawBitmap(currentBitmap, -resizeCoordinateXLeft.toFloat(), -resizeCoordinateYTop.toFloat(), null)
            currentLayer.bitmap = resizedBitmap
        }
        layerModel.height = height
        layerModel.width = width
    }

    override fun freeResources() {
        // No resources to free
    }
}
