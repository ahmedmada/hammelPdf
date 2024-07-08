package org.hammel.paintpdf.controller

import android.graphics.Bitmap
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.ToolType
import java.util.HashSet

interface ToolController {
    val isDefaultTool: Boolean
    val toolType: ToolType?
    val toolColor: Int?
    val currentTool: Tool?
    val toolList: HashSet<ToolType>
        get() = hashSetOf(
            ToolType.TEXT,
            ToolType.TRANSFORM,
            ToolType.IMPORTPNG,
            ToolType.SHAPE,
            ToolType.LINE
        )

    fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener)

    fun switchTool(toolType: ToolType)

    fun hideToolOptionsView()

    fun hideToolOptionsViewForShapeTools()

    fun showToolOptionsView()

    fun toolOptionsViewVisible(): Boolean

    fun resetToolInternalState()

    fun resetToolInternalStateOnImageLoaded()

    fun disableToolOptionsView()

    fun disableHideOption()

    fun enableHideOption()

    fun enableToolOptionsView()

    fun createTool()

    fun adaptLayoutForFullScreen()

    fun toggleToolOptionsView()

    fun hasToolOptionsView(): Boolean

    fun setBitmapFromSource(bitmap: Bitmap?)

    fun adjustClippingToolOnBackPressed(backPressed: Boolean)
}
