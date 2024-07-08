package org.hammel.paintpdf.tools.implementation

import android.graphics.BlurMaskFilter
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.BrushToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

const val MAX_ALPHA_VALUE = 255
const val MAX_NEW_RANGE = 150
const val MIN_NEW_RANGE = 20

class WatercolorTool(
    brushToolOptionsView: BrushToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    drawTime: Long
) : BrushTool(
    brushToolOptionsView,
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager,
    drawTime
) {
    override val toolType: ToolType
        get() = ToolType.WATERCOLOR

    init {
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        previewPaint.maskFilter = BlurMaskFilter(calcRange(previewPaint.alpha), BlurMaskFilter.Blur.INNER)
    }

    override fun changePaintColor(color: Int, invalidate: Boolean) {
        super.changePaintColor(color, invalidate)

        if (invalidate) brushToolOptionsView.invalidate()
        bitmapPaint.maskFilter = BlurMaskFilter(calcRange(bitmapPaint.alpha), BlurMaskFilter.Blur.INNER)
        previewPaint.maskFilter = BlurMaskFilter(calcRange(previewPaint.alpha), BlurMaskFilter.Blur.INNER)
    }
    companion object {
        fun calcRange(value: Int): Float {
            val oldRange = MAX_ALPHA_VALUE
            val newRange = MAX_NEW_RANGE - MIN_NEW_RANGE
            var newValue = value * newRange / oldRange + MIN_NEW_RANGE

            newValue = MAX_NEW_RANGE - newValue + MIN_NEW_RANGE
            return newValue.toFloat()
        }
    }
}
