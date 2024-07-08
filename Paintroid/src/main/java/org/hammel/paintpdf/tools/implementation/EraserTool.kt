package org.hammel.paintpdf.tools.implementation

import android.graphics.Color
import android.graphics.Paint
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.BrushToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController
import org.hammel.paintpdf.ui.viewholder.BottomNavigationViewHolder

class EraserTool(
        brushToolOptionsView: BrushToolOptionsView,
        contextCallback: ContextCallback,
        toolOptionsViewController: ToolOptionsViewController,
        toolPaint: ToolPaint,
        workspace: Workspace,
        idlingResource: CountingIdlingResource,
        commandManager: CommandManager,
        bottomNavigationViewHolder: BottomNavigationViewHolder,
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

    private var savedColor: Int
    private var bottomNavigationViewHolder: BottomNavigationViewHolder

    init {
        this.bottomNavigationViewHolder = bottomNavigationViewHolder
        bottomNavigationViewHolder.enableColorItemView(false)
        bottomNavigationViewHolder.setColorButtonColor(Color.TRANSPARENT)
        savedColor = toolPaint.color
        toolPaint.color = Color.TRANSPARENT
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }
    override val previewPaint: Paint
        get() = Paint().apply {
            set(super.previewPaint)
            color = Color.TRANSPARENT
            shader = toolPaint.checkeredShader
        }

    override val bitmapPaint: Paint
        get() = Paint().apply {
            set(super.bitmapPaint)
            xfermode = toolPaint.eraseXfermode
            alpha = 0
        }

    override val toolType: ToolType
        get() = ToolType.ERASER

    fun setSavedColor() {
        bottomNavigationViewHolder.enableColorItemView(true)
        bottomNavigationViewHolder.setColorButtonColor(savedColor)
        toolPaint.color = savedColor
        brushToolOptionsView.setCurrentPaint(toolPaint.paint)
    }
}
