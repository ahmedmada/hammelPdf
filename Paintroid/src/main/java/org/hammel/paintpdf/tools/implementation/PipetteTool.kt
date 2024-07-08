package org.hammel.paintpdf.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.MainActivity
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

class PipetteTool(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    private val listener: OnColorPickedListener,
    private val mainActivity: MainActivity
) : BaseTool(contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager) {
    private var surfaceBitmap: Bitmap? = null

    override val toolType: ToolType
        get() = ToolType.PIPETTE

    override var drawTime: Long = 0
    override fun handleUpAnimations(coordinate: PointF?) {
      super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    init {
        updateSurfaceBitmap()
    }

    override fun draw(canvas: Canvas) = Unit

    override fun handleDown(coordinate: PointF?): Boolean = setColor(coordinate)

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean = setColor(coordinate)

    override fun handleUp(coordinate: PointF?): Boolean = setColor(coordinate, true)

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun resetInternalState() {
        updateSurfaceBitmap()
    }

    private fun setColor(coordinate: PointF?, saveCommand: Boolean = false): Boolean {
        if (coordinate == null || !workspace.contains(coordinate)) {
            return false
        }
        val color =
            surfaceBitmap?.getPixel(coordinate.x.toInt(), coordinate.y.toInt()) ?: return false
        listener.colorChanged(color)
        changePaintColor(color)
        if (saveCommand) {
            val command = commandFactory.createColorChangedCommand(this, mainActivity, color)
            mainActivity.model.colorHistory.addColor(color)
            mainActivity.commandManager.addCommand(command)
        }
        return true
    }

    private fun updateSurfaceBitmap() {
        surfaceBitmap = workspace.bitmapOfAllLayers
    }
}
