package org.hammel.paintpdf.tools

import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

interface ToolFactory {
    fun createTool(
        toolType: ToolType,
        toolOptionsViewController: ToolOptionsViewController,
        commandManager: CommandManager,
        workspace: Workspace,
        idlingResource: CountingIdlingResource,
        toolPaint: ToolPaint,
        contextCallback: ContextCallback,
        onColorPickedListener: OnColorPickedListener
    ): Tool
}
