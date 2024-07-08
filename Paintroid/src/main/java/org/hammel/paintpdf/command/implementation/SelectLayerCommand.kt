
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class SelectLayerCommand(position: Int) : Command {

    var position = position; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        layerModel.currentLayer = layerModel.layers[position]
    }

    override fun freeResources() {
        // No resources to free
    }
}
