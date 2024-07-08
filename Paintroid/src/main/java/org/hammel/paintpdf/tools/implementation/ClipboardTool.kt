package org.hammel.paintpdf.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import androidx.test.espresso.idling.CountingIdlingResource
import paintpdf.R
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ClipboardToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

private const val BUNDLE_TOOL_READY_FOR_PASTE = "BUNDLE_TOOL_READY_FOR_PASTE"
private const val BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP"

class ClipboardTool(
    clipboardToolOptionsView: ClipboardToolOptionsView,
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
    private val clipboardToolOptionsView: ClipboardToolOptionsView
    private var readyForPaste = false
    private val isDrawingBitmapReusable: Boolean
        get() {
            drawingBitmap?.let {
                return it.width == boxWidth.toInt() && it.height == boxHeight.toInt()
            }
            return false
        }

    override val toolType: ToolType
        get() = ToolType.CLIPBOARD

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        rotationEnabled = true
        this.clipboardToolOptionsView = clipboardToolOptionsView
        setBitmap(Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888))
        val callback: ClipboardToolOptionsView.Callback = object : ClipboardToolOptionsView.Callback {
            override fun copyClicked() {
                highlightBox()
                copyBoxContent()
                this@ClipboardTool.clipboardToolOptionsView.enablePaste(true)
            }

            override fun cutClicked() {
                highlightBox()
                copyBoxContent()
                cutBoxContent()
                this@ClipboardTool.clipboardToolOptionsView.enablePaste(true)
            }

            override fun pasteClicked() {
                highlightBox()
                pasteBoxContent()
            }
        }
        clipboardToolOptionsView.setCallback(callback)
        toolOptionsViewController.showDelayed()
        setShapeSizeChangedListener(this)
        createAndSetShapeSizeText(boxWidth, boxHeight)
    }

    fun copyBoxContent() {
        if (isDrawingBitmapReusable) {
            drawingBitmap?.eraseColor(Color.TRANSPARENT)
        } else {
            drawingBitmap =
                Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888)
        }
        val layerBitmap = workspace.bitmapOfCurrentLayer
        if (layerBitmap != null) {
            drawingBitmap?.let {
                Canvas(it).apply {
                    translate(-toolPosition.x + boxWidth / 2, -toolPosition.y + boxHeight / 2)
                    rotate(-boxRotation, toolPosition.x, toolPosition.y)
                    drawBitmap(layerBitmap, 0f, 0f, null)
                }
            }
        }
        readyForPaste = true
    }

    private fun pasteBoxContent() {
        drawingBitmap?.let {
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

    private fun cutBoxContent() {
        val command =
            commandFactory.createCutCommand(toolPosition, boxWidth, boxHeight, boxRotation)
        commandManager.addCommand(command)
    }

    override fun onClickOnButton() {
        if (!readyForPaste || drawingBitmap == null) {
            contextCallback.showNotification(R.string.clipboard_tool_copy_hint)
        } else if (boxIntersectsWorkspace()) {
            pasteBoxContent()
            highlightBox()
        }
    }

    override fun resetInternalState() = Unit
    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap)
        bundle?.putBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste)
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            readyForPaste = getBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste)
            drawingBitmap = getParcelable(BUNDLE_TOOL_DRAWING_BITMAP)
        }
        clipboardToolOptionsView.enablePaste(readyForPaste)
    }
    override fun onShapeSizeChanged(shapeText: String) {
        clipboardToolOptionsView.setShapeSizeText(shapeText)
    }

    override fun onToggleVisibility(isVisible: Boolean) {
        clipboardToolOptionsView.toggleShapeSizeVisibility(isVisible)
    }

    fun changeClipboardToolLayoutVisibility(willHide: Boolean, disabled: Boolean = false) {
        changeToolLayoutVisibility(clipboardToolOptionsView.getClipboardToolOptionsLayout(), willHide, disabled)
    }
}
