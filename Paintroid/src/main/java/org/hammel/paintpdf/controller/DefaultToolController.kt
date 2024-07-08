package org.hammel.paintpdf.controller

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.Tool.StateChange
import org.hammel.paintpdf.tools.ToolFactory
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolReference
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.implementation.ClipboardTool
import org.hammel.paintpdf.tools.implementation.ClippingTool
import org.hammel.paintpdf.tools.implementation.EraserTool
import org.hammel.paintpdf.tools.implementation.ImportTool
import org.hammel.paintpdf.tools.implementation.LineTool
import org.hammel.paintpdf.tools.implementation.ShapeTool
import org.hammel.paintpdf.tools.implementation.SprayTool
import org.hammel.paintpdf.tools.implementation.TextTool
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

class DefaultToolController(
    private val toolReference: ToolReference,
    private val toolOptionsViewController: ToolOptionsViewController,
    private val toolFactory: ToolFactory,
    private val commandManager: CommandManager,
    private val workspace: Workspace,
    private val idlingResource: CountingIdlingResource,
    private val toolPaint: ToolPaint,
    private val contextCallback: ContextCallback
) : ToolController {
    private lateinit var onColorPickedListener: OnColorPickedListener

    override val isDefaultTool: Boolean
        get() = toolReference.tool?.toolType == ToolType.BRUSH

    override val toolColor: Int?
        get() = toolReference.tool?.drawPaint?.color

    override val currentTool: Tool?
        get() = toolReference.tool

    override val toolType: ToolType?
        get() = toolReference.tool?.toolType

    private fun createAndSetupTool(toolType: ToolType): Tool {
        if (toolType != ToolType.HAND) {
            toolOptionsViewController.removeToolViews()
        } else if (toolType != ToolType.CLIP) {
            toolOptionsViewController.removeToolViews()
        }
        if (toolList.contains(toolType)) {
            toolOptionsViewController.showCheckmark()
        } else {
            toolOptionsViewController.hideCheckmark()
        }
        if (toolReference.tool?.toolType == ToolType.SPRAY) {
            (currentTool as SprayTool).resetRadiusToStrokeWidth()
        }
        val tool: Tool = toolFactory.createTool(
            toolType,
            toolOptionsViewController,
            commandManager,
            workspace,
            idlingResource,
            toolPaint,
            contextCallback,
            onColorPickedListener
        )
        if (toolType != ToolType.HAND) {
            toolOptionsViewController.resetToOrigin()
            toolOptionsViewController.show()
        }
        return tool
    }

    override fun setOnColorPickedListener(onColorPickedListener: OnColorPickedListener) {
        this.onColorPickedListener = onColorPickedListener
    }

    override fun switchTool(toolType: ToolType) {
        switchTool(createAndSetupTool(toolType))
    }

    override fun hideToolOptionsView() {
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).hideTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(true, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(true, true)
            else -> toolOptionsViewController.hide()
        }
    }

    override fun hideToolOptionsViewForShapeTools() {
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).hideTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(true, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(true, true)
            else -> {}
        }
    }

    override fun showToolOptionsView() {
        toolOptionsViewController.show()
        when (toolReference.tool?.toolType) {
            ToolType.TEXT -> (toolReference.tool as TextTool).showTextToolLayout()
            ToolType.SHAPE -> (toolReference.tool as ShapeTool).changeShapeToolLayoutVisibility(false, true)
            ToolType.CLIPBOARD -> (toolReference.tool as ClipboardTool).changeClipboardToolLayoutVisibility(false, true)
            else -> {}
        }
    }

    override fun toolOptionsViewVisible(): Boolean = toolOptionsViewController.isVisible

    override fun resetToolInternalState() {
        toolReference.tool?.resetInternalState(StateChange.RESET_INTERNAL_STATE)
    }

    override fun resetToolInternalStateOnImageLoaded() {
        toolReference.tool?.resetInternalState(StateChange.NEW_IMAGE_LOADED)
    }

    private fun switchTool(tool: Tool) {
        val currentTool = toolReference.tool
        val currentToolType = currentTool?.toolType
        if (currentToolType == ToolType.ERASER) {
            (currentTool as EraserTool).setSavedColor()
        }
        currentToolType?.let { hidePlusIfShown(it) }

        if (currentToolType == tool.toolType) {
            val toolBundle = Bundle()
            currentTool.onSaveInstanceState(toolBundle)
            tool.onRestoreInstanceState(toolBundle)
        }
        toolReference.tool = tool
        workspace.invalidate()
    }

    override fun adjustClippingToolOnBackPressed(backPressed: Boolean) {
        val clippingTool = currentTool as ClippingTool
        if (backPressed) {
            if (clippingTool.areaClosed) {
                clippingTool.handleDown(PointF(0f, 0f))
                clippingTool.wasRecentlyApplied = true
                clippingTool.resetInternalState(StateChange.NEW_IMAGE_LOADED)
            }
        } else {
            clippingTool.onClickOnButton()
        }
    }

    private fun hidePlusIfShown(currentToolType: ToolType) {
        if (currentToolType == ToolType.LINE && null != LineTool.topBarViewHolder) {
            val visibility = LineTool.topBarViewHolder?.plusButton?.visibility == View.VISIBLE
            if (visibility) {
                LineTool.topBarViewHolder?.plusButton?.visibility = View.GONE
            }
        }
    }

    override fun disableToolOptionsView() {
        if (toolType != ToolType.IMPORTPNG) {
            toolOptionsViewController.disable()
        }
    }

    override fun enableToolOptionsView() {
        toolOptionsViewController.enable()
    }

    override fun adaptLayoutForFullScreen() {
        if (toolReference.tool?.toolType != ToolType.HAND) {
            toolOptionsViewController.show(true)
        }
    }

    override fun enableHideOption() {
        toolOptionsViewController.enableHide()
    }

    override fun disableHideOption() {
        if (toolType != ToolType.IMPORTPNG) {
            toolOptionsViewController.disableHide()
        }
    }

    override fun createTool() {
        val currentTool = toolReference.tool
        if (currentTool == null) {
            toolReference.tool = createAndSetupTool(ToolType.BRUSH)
        } else {
            val bundle = Bundle()
            toolReference.tool?.onSaveInstanceState(bundle)
            toolReference.tool = createAndSetupTool(currentTool.toolType)
            toolReference.tool?.onRestoreInstanceState(bundle)
        }
        workspace.invalidate()
    }

    override fun toggleToolOptionsView() {
        if (toolOptionsViewController.isVisible) {
            toolOptionsViewController.hide()
        } else {
            toolOptionsViewController.show()
        }
    }

    override fun hasToolOptionsView(): Boolean = toolType?.hasOptions() ?: false

    override fun setBitmapFromSource(bitmap: Bitmap?) {
        bitmap ?: return
        val importTool = toolReference.tool as ImportTool?
        importTool?.setBitmapFromSource(bitmap)
    }
}
