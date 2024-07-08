
package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class RotateCommand(rotateDirection: RotateDirection) : Command {

    var rotateDirection = rotateDirection; private set

    companion object {
        private const val ANGLE = 90f
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val rotateMatrix = Matrix().apply {
            when (rotateDirection) {
                RotateDirection.ROTATE_RIGHT -> postRotate(ANGLE)
                RotateDirection.ROTATE_LEFT -> postRotate(-ANGLE)
            }
        }
        val iterator: Iterator<LayerContracts.Layer> = layerModel.listIterator(0)
        while (iterator.hasNext()) {
            val currentLayer = iterator.next()
            val rotatedBitmap = Bitmap.createBitmap(
                currentLayer.bitmap, 0, 0,
                layerModel.width, layerModel.height, rotateMatrix, true
            )
            currentLayer.bitmap = rotatedBitmap
        }
        val tmpWidth = layerModel.width
        layerModel.width = layerModel.height
        layerModel.height = tmpWidth
    }

    override fun freeResources() {
        // No resources to free
    }

    enum class RotateDirection {
        ROTATE_LEFT, ROTATE_RIGHT
    }
}
