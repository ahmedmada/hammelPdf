package org.hammel.paintpdf.tools.implementation

import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.MainActivity
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.ToolFactory
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ToolOptionsViewController
import org.hammel.paintpdf.ui.tools.DefaultBrushToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultFillToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultShapeToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultSprayToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultClipboardToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultImportToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultTextToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultTransformToolOptionsView
import org.hammel.paintpdf.ui.tools.DefaultSmudgeToolOptionsView

private const val DRAW_TIME_INIT: Long = 30_000_000

@SuppressWarnings("LongMethod")
class DefaultToolFactory(var mainActivity: MainActivity) : ToolFactory {
    override fun createTool(
        toolType: ToolType,
        toolOptionsViewController: ToolOptionsViewController,
        commandManager: CommandManager,
        workspace: Workspace,
        idlingResource: CountingIdlingResource,
        toolPaint: ToolPaint,
        contextCallback: ContextCallback,
        onColorPickedListener: OnColorPickedListener
    ): Tool {
        val toolLayout = toolOptionsViewController.toolSpecificOptionsLayout
        return when (toolType) {
            ToolType.CURSOR -> CursorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.CLIPBOARD -> ClipboardTool(
                DefaultClipboardToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.IMPORTPNG -> ImportTool(
                DefaultImportToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.PIPETTE -> PipetteTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                onColorPickedListener,
                mainActivity
            )
            ToolType.FILL -> FillTool(
                DefaultFillToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TRANSFORM -> TransformTool(
                DefaultTransformToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SHAPE -> ShapeTool(
                DefaultShapeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.ERASER -> EraserTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                mainActivity.bottomNavigationViewHolder,
                DRAW_TIME_INIT
            )
            ToolType.LINE -> LineTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.TEXT -> TextTool(
                DefaultTextToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.HAND -> HandTool(
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SPRAY -> SprayTool(
                DefaultSprayToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.WATERCOLOR -> WatercolorTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
            ToolType.SMUDGE -> SmudgeTool(
                DefaultSmudgeToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
            )
            ToolType.CLIP -> ClippingTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT,
                mainActivity
            )
            else -> BrushTool(
                DefaultBrushToolOptionsView(toolLayout),
                contextCallback,
                toolOptionsViewController,
                toolPaint,
                workspace,
                idlingResource,
                commandManager,
                DRAW_TIME_INIT
            )
        }
    }
}
