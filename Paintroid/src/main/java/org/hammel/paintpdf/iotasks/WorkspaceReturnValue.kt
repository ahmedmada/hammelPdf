package org.hammel.paintpdf.iotasks

import org.hammel.paintpdf.colorpicker.ColorHistory
import org.hammel.paintpdf.model.CommandManagerModel

data class WorkspaceReturnValue(
    val commandManagerModel: CommandManagerModel?,
    val colorHistory: ColorHistory?
)
