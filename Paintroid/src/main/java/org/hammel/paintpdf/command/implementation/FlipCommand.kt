
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class FlipCommand(flipDirection: FlipDirection) : Command {

    var flipDirection = flipDirection; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val flipMatrix = Matrix().apply {
            when (flipDirection) {
                FlipDirection.FLIP_HORIZONTAL -> {
                    setScale(1f, -1f)
                    postTranslate(0f, layerModel.height.toFloat())
                }
                FlipDirection.FLIP_VERTICAL -> {
                    setScale(-1f, 1f)
                    postTranslate(layerModel.width.toFloat(), 0f)
                }
            }
        }
        layerModel.currentLayer?.bitmap?.let { bitmap ->
            val bitmapCopy = bitmap.copy(bitmap.config, bitmap.isMutable)
            val flipCanvas = Canvas(bitmap)
            bitmap.eraseColor(Color.TRANSPARENT)
            flipCanvas.drawBitmap(bitmapCopy, flipMatrix, Paint())
        }
    }

    override fun freeResources() {
        // No resources to free
    }

    enum class FlipDirection {
        FLIP_HORIZONTAL, FLIP_VERTICAL
    }
}
