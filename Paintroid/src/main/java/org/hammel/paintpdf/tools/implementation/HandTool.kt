package org.hammel.paintpdf.tools.implementation

import android.graphics.Canvas
import android.graphics.PointF
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

class HandTool(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseTool(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {
    override val toolType: ToolType
        get() = ToolType.HAND

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun draw(canvas: Canvas) = Unit

    override fun resetInternalState() = Unit

    override fun handleDown(coordinate: PointF?): Boolean = true

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean = true

    override fun handleUp(coordinate: PointF?): Boolean = true

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    override fun handToolMode(): Boolean = true
}
