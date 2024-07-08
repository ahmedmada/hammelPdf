
package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.model.Layer

class LoadLayerListCommand(loadedLayers: List<LayerContracts.Layer>) : Command {

    var loadedLayers = loadedLayers; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        loadedLayers.forEachIndexed { index, layer ->
            val currentLayer = Layer(layer.bitmap.copy(Bitmap.Config.ARGB_8888, true))
            currentLayer.opacityPercentage = layer.opacityPercentage
            layerModel.addLayerAt(index, currentLayer)
        }
        layerModel.currentLayer = layerModel.getLayerAt(0)
    }

    override fun freeResources() {
        // No resources to free
    }
}
