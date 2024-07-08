package org.hammel.paintpdf.tools.implementation

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import paintpdf.R
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.options.ToolOptionsViewController
import kotlin.math.max
import kotlin.math.min

private const val BUNDLE_TOOL_POSITION_Y = "TOOL_POSITION_Y"
private const val BUNDLE_TOOL_POSITION_X = "TOOL_POSITION_X"

abstract class BaseToolWithShape @SuppressLint("VisibleForTests") constructor(
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager
) : BaseTool(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
    commandManager
) {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    @JvmField
    var toolPosition: PointF

    @JvmField
    var primaryShapeColor: Int =
        contextCallback.getColor(R.color.pdfPaint_main_rectangle_tool_primary_color)

    @JvmField
    var secondaryShapeColor: Int = contextCallback.getColor(R.color.pdfPaint_colorAccentAlpha60)

    @JvmField
    val linePaint: Paint

    @JvmField
    val metrics: DisplayMetrics = contextCallback.displayMetrics

    init {
        val perspective = workspace.perspective
        toolPosition = if (perspective.scale > 1) {
            PointF(
                perspective.surfaceCenterX - perspective.surfaceTranslationX,
                perspective.surfaceCenterY - perspective.surfaceTranslationY
            )
        } else {
            PointF(workspace.width / 2f, workspace.height / 2f)
        }
        linePaint = Paint()
        linePaint.color = primaryShapeColor
        linePaint.pathEffect = null
    }

    abstract fun drawShape(canvas: Canvas)

    fun getStrokeWidthForZoom(
        defaultStrokeWidth: Float,
        minStrokeWidth: Float,
        maxStrokeWidth: Float
    ): Float {
        val strokeWidth = defaultStrokeWidth * metrics.density / workspace.scale
        return min(maxStrokeWidth, max(minStrokeWidth, strokeWidth))
    }

    fun getInverselyProportionalSizeForZoom(defaultSize: Float): Float {
        val applicationScale = workspace.scale
        return defaultSize * metrics.density / applicationScale
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x)
            putFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            toolPosition.x = getFloat(BUNDLE_TOOL_POSITION_X, toolPosition.x)
            toolPosition.y = getFloat(BUNDLE_TOOL_POSITION_Y, toolPosition.y)
        }
    }

    override fun getAutoScrollDirection(
        pointX: Float,
        pointY: Float,
        screenWidth: Int,
        screenHeight: Int
    ): Point {
        val surfaceToolPosition = workspace.getSurfacePointFromCanvasPoint(toolPosition)
        return scrollBehavior.getScrollDirection(
            surfaceToolPosition.x,
            surfaceToolPosition.y,
            screenWidth,
            screenHeight
        )
    }

    abstract fun onClickOnButton()

    protected open fun drawToolSpecifics(canvas: Canvas, boxWidth: Float, boxHeight: Float) {}
}
