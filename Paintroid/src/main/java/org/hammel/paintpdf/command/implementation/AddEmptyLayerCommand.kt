package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.common.CommonFactory
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.model.Layer

class AddEmptyLayerCommand(private val commonFactory: CommonFactory) : Command {

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val layer = Layer(
            commonFactory.createBitmap(
                layerModel.width, layerModel.height,
                Bitmap.Config.ARGB_8888
            )
        )
        layerModel.addLayerAt(0, layer)
        layerModel.currentLayer = layer
    }

    override fun freeResources() {
        // No resources to free
    }
}
