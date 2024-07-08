package org.hammel.paintpdf.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.os.Bundle
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ImportToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController
import kotlin.math.max
import kotlin.math.min

private const val BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP"

class ImportTool(
    importToolOptionsView: ImportToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback, toolOptionsViewController, toolPaint, workspace, idlingResource, commandManager
), BaseToolWithRectangleShape.ShapeSizeChangedListener {

    private val importToolOptionsView: ImportToolOptionsView
    override val toolType: ToolType
        get() = ToolType.IMPORTPNG

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        this.importToolOptionsView = importToolOptionsView
        rotationEnabled = true
        setShapeSizeChangedListener(this)
        importToolOptionsView.setShapeSizeInvisble()
    }

    override fun drawShape(canvas: Canvas) {
        if (drawingBitmap != null) {
            super.drawShape(canvas)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        val bitmap = bundle?.getParcelable<Bitmap>(BUNDLE_TOOL_DRAWING_BITMAP)
        drawingBitmap = bitmap
    }

    override fun onClickOnButton() {
        drawingBitmap?.let {
            highlightBox()
            val command = commandFactory.createClipboardCommand(
                it,
                toolPosition,
                boxWidth,
                boxHeight,
                boxRotation
            )
            commandManager.addCommand(command)
        }
    }

    fun setBitmapFromSource(bitmap: Bitmap) {
        super.setBitmap(bitmap)
        val maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * workspace.width
        val maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * workspace.height
        val minimumSize = DEFAULT_BOX_RESIZE_MARGIN.toFloat()
        boxWidth = max(minimumSize, min(maximumBorderRatioWidth, bitmap.width.toFloat()))
        boxHeight = max(minimumSize, min(maximumBorderRatioHeight, bitmap.height.toFloat()))
        createAndSetShapeSizeText(boxWidth, boxHeight)
    }

    override fun onShapeSizeChanged(shapeText: String) {
        importToolOptionsView.setShapeSizeText(shapeText)
    }

    override fun onToggleVisibility(isVisible: Boolean) {
        if (isVisible) hideToolSpecificLayout(isVisible) else showToolSpecificLayout()
    }
}
