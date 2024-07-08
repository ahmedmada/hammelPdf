package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts

class CompositeCommand : Command {

    var commands = mutableListOf<Command>(); private set

    fun addCommand(command: Command) {
        commands.add(command)
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        commands.forEach { command ->
            layerModel.currentLayer?.let { layer ->
                canvas.setBitmap(layer.bitmap)
            }
            command.run(canvas, layerModel)
        }
    }

    override fun freeResources() {
        commands.forEach { command ->
            command.freeResources()
        }
    }
}
