package org.hammel.paintpdf

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolReference
import org.hammel.paintpdf.ui.Perspective

class PaintroidApplicationFragment : Fragment() {
    var commandManager: CommandManager? = null
    var currentTool: ToolReference? = null
    var perspective: Perspective? = null
    var layerModel: LayerContracts.Model? = null
    var toolPaint: ToolPaint? = null
    override fun onCreate(savedInstance: Bundle?) {
        super.onCreate(savedInstance)
        retainInstance = true
    }
}
