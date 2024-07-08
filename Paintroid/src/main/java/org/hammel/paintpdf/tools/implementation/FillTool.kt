package org.hammel.paintpdf.tools.implementation

import android.graphics.Canvas
import android.graphics.PointF
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.FillToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

private const val HUNDRED = 100f
const val DEFAULT_TOLERANCE_IN_PERCENT = 12
const val MAX_ABSOLUTE_TOLERANCE = 510

class FillTool(
    fillToolOptionsView: FillToolOptionsView,
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
    @VisibleForTesting
    @JvmField
    var colorTolerance = MAX_ABSOLUTE_TOLERANCE * DEFAULT_TOLERANCE_IN_PERCENT / HUNDRED

    init {
        fillToolOptionsView.setCallback(object : FillToolOptionsView.Callback {
            override fun onColorToleranceChanged(colorTolerance: Int) {
                updateColorTolerance(colorTolerance)
            }
        })
    }

    fun updateColorTolerance(colorToleranceInPercent: Int) {
        colorTolerance = getToleranceAbsoluteValue(colorToleranceInPercent)
    }

    fun getToleranceAbsoluteValue(toleranceInPercent: Int): Float =
        MAX_ABSOLUTE_TOLERANCE * toleranceInPercent / HUNDRED

    override fun handleDown(coordinate: PointF?): Boolean = false

    override fun handleMove(coordinate: PointF?, shouldAnimate: Boolean): Boolean = false

    override fun handleUp(coordinate: PointF?): Boolean {
        coordinate ?: return false
        if (!workspace.contains(coordinate)) {
            return false
        }

        val command = commandFactory.createFillCommand(
            coordinate.x.toInt(), coordinate.y.toInt(), toolPaint.paint, colorTolerance
        )
        commandManager.addCommand(command)
        return true
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    public override fun resetInternalState() = Unit

    override val toolType: ToolType = ToolType.FILL
    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun draw(canvas: Canvas) = Unit
}
