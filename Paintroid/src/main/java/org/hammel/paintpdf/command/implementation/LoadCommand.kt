
package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.model.Layer

class LoadCommand(loadedBitmap: Bitmap) : Command {

    var loadedBitmap = loadedBitmap; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val currentLayer = Layer(loadedBitmap.copy(Bitmap.Config.ARGB_8888, true))
        layerModel.addLayerAt(0, currentLayer)
        layerModel.currentLayer = currentLayer
    }

    override fun freeResources() {
        // No resources to free
    }
}
