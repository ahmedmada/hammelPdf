
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class SetDimensionCommand(width: Int, height: Int) : Command {

    var width = width; private set
    var height = height; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        layerModel.width = width
        layerModel.height = height
    }

    override fun freeResources() {
        // No resources to free
    }
}
