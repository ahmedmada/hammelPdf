
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class ResetCommand : Command {

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        layerModel.reset()
    }

    override fun freeResources() {
        // No resources to free
    }
}
