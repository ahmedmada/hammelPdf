
package org.hammel.paintpdf.command

import android.graphics.Canvas
import org.hammel.paintpdf.contract.LayerContracts

interface Command {
    fun run(canvas: Canvas, layerModel: LayerContracts.Model)
    fun freeResources()
}
