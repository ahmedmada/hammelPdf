package org.hammel.paintpdf.tools.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import androidx.test.espresso.idling.CountingIdlingResource
import org.hammel.paintpdf.command.CommandManager
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.tools.drawable.DrawableFactory
import org.hammel.paintpdf.tools.drawable.DrawableShape
import org.hammel.paintpdf.tools.drawable.DrawableStyle
import org.hammel.paintpdf.tools.helper.toPoint
import org.hammel.paintpdf.tools.options.ShapeToolOptionsView
import org.hammel.paintpdf.tools.options.ToolOptionsViewController

private const val SHAPE_OFFSET = 10f
private const val MIN_SHAPE_OFFSET = 0.75f
private const val DEFAULT_OUTLINE_WIDTH = 25
private const val BUNDLE_BASE_SHAPE = "BASE_SHAPE"
private const val BUNDLE_SHAPE_DRAW_TYPE = "SHAPE_DRAW_TYPE"
private const val BUNDLE_OUTLINE_WIDTH = "OUTLINE_WIDTH"
private const val STROKE_MITER_MULTIPLICATOR: Int = 4

class ShapeTool(
    shapeToolOptionsView: ShapeToolOptionsView,
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
    private val shapeToolOptionsView: ShapeToolOptionsView
    private val shapePreviewPaint = Paint()
    private val shapePreviewRect = RectF()
    private val drawableFactory = DrawableFactory()
    private var baseShape = DrawableShape.RECTANGLE
    private var shapeDrawType = DrawableStyle.FILL
    private var shapeDrawable = drawableFactory.createDrawable(baseShape)
    private var shapeOutlineWidth = DEFAULT_OUTLINE_WIDTH
    val shapeBitmapPaint = Paint()

    override val toolType: ToolType
        get() = ToolType.SHAPE

    override fun handleUpAnimations(coordinate: PointF?) {
        super.handleUp(coordinate)
    }

    override fun handleDownAnimations(coordinate: PointF?) {
        super.handleDown(coordinate)
    }

    override fun toolPositionCoordinates(coordinate: PointF): PointF = coordinate

    init {
        rotationEnabled = true
        this.shapeToolOptionsView = shapeToolOptionsView
        this.shapeToolOptionsView.setCallback(
            object : ShapeToolOptionsView.Callback {
                override fun setToolType(shape: DrawableShape) {
                    setBaseShape(shape)
                    workspace.invalidate()
                }

                override fun setDrawType(drawType: DrawableStyle) {
                    shapeDrawType = drawType
                    workspace.invalidate()
                }

                override fun setOutlineWidth(outlineWidth: Int) {
                    shapeOutlineWidth = outlineWidth
                    workspace.invalidate()
                }
            })
        toolOptionsViewController.showDelayed()
        setShapeSizeChangedListener(this)
        createAndSetShapeSizeText(boxWidth, boxHeight)
    }

    fun getBaseShape(): DrawableShape = baseShape

    fun setBaseShape(shape: DrawableShape) {
        baseShape = shape
        shapeDrawable = drawableFactory.createDrawable(shape)
    }

    override fun changePaintColor(color: Int, invalidate: Boolean) {
        super.changePaintColor(color, invalidate)
        if (invalidate) workspace.invalidate()
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putSerializable(BUNDLE_BASE_SHAPE, baseShape)
            putSerializable(BUNDLE_SHAPE_DRAW_TYPE, shapeDrawType)
            putInt(BUNDLE_OUTLINE_WIDTH, shapeOutlineWidth)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        val newBaseShape = bundle?.getSerializable(BUNDLE_BASE_SHAPE) as DrawableShape? ?: return
        val newShapeDrawType =
            bundle?.getSerializable(BUNDLE_SHAPE_DRAW_TYPE) as DrawableStyle? ?: return
        val newShapeOutlineWidth = bundle?.getInt(BUNDLE_OUTLINE_WIDTH) ?: return
        if (baseShape != newBaseShape || shapeDrawType != newShapeDrawType) {
            baseShape = newBaseShape
            shapeDrawType = newShapeDrawType
            shapeOutlineWidth = newShapeOutlineWidth
            shapeDrawable = drawableFactory.createDrawable(newBaseShape)
            shapeToolOptionsView.setShapeActivated(newBaseShape)
            shapeToolOptionsView.setDrawTypeActivated(newShapeDrawType)
            shapeToolOptionsView.setShapeOutlineWidth(newShapeOutlineWidth)
        }
    }

    override fun drawBitmap(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        shapePreviewPaint.set(toolPaint.previewPaint)
        preparePaint(shapePreviewPaint)
        prepareShapeRectangle(shapePreviewRect, boxWidth, boxHeight)
        shapeDrawable.draw(canvas, shapePreviewRect, shapePreviewPaint)
    }

    private fun prepareShapeRectangle(shapeRect: RectF, boxWidth: Float, boxHeight: Float) {
        shapeRect.setEmpty()
        val zoomScaling = if (workspace.scale > 1) workspace.scale else 1f
        var shapeOffset = SHAPE_OFFSET / zoomScaling
        shapeOffset = if (shapeOffset > MIN_SHAPE_OFFSET) shapeOffset else MIN_SHAPE_OFFSET
        shapeRect.inset(shapeOffset - boxWidth / 2, shapeOffset - boxHeight / 2)
        if (shapePreviewPaint.style == Paint.Style.STROKE) {
            shapeRect.inset(shapeOutlineWidth / 2f, shapeOutlineWidth / 2f)
        }
    }

    private fun preparePaint(paint: Paint) {
        when (shapeDrawType) {
            DrawableStyle.FILL -> {
                paint.style = Paint.Style.FILL
                paint.strokeJoin = Paint.Join.MITER
            }
            DrawableStyle.STROKE -> {
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = shapeOutlineWidth.toFloat()
                paint.strokeCap = Paint.Cap.BUTT
                paint.strokeJoin = Paint.Join.MITER
                paint.strokeMiter *= STROKE_MITER_MULTIPLICATOR
                val antiAlias = shapeOutlineWidth > 1
                paint.isAntiAlias = antiAlias
            }
        }
    }

    override fun onClickOnButton() {
        if (toolPosition.x in -boxWidth / 2..workspace.width + boxWidth / 2 && toolPosition.y in -boxHeight / 2..workspace.height + boxHeight / 2) {
            shapeBitmapPaint.set(toolPaint.paint)
            val shapeRect = RectF()
            preparePaint(shapeBitmapPaint)
            prepareShapeRectangle(shapeRect, boxWidth, boxHeight)
            val command = commandFactory.createGeometricFillCommand(
                shapeDrawable,
                toPoint(toolPosition),
                shapeRect,
                boxRotation,
                shapeBitmapPaint
            )
            commandManager.addCommand(command)
            highlightBox()
        }
    }

    fun changeShapeToolLayoutVisibility(willHide: Boolean, disabled: Boolean = false) {
        changeToolLayoutVisibility(shapeToolOptionsView.getShapeToolOptionsLayout(), willHide, disabled)
    }

    override fun onShapeSizeChanged(shapeText: String) {
        shapeToolOptionsView.setShapeSizeText(shapeText)
    }

    override fun onToggleVisibility(isVisible: Boolean) {
        shapeToolOptionsView.toggleShapeSizeVisibility(isVisible)
    }
}
