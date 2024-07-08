package org.hammel.paintpdf.command.implementation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.command.CommandFactory
import org.hammel.paintpdf.command.implementation.FlipCommand.FlipDirection
import org.hammel.paintpdf.command.implementation.RotateCommand.RotateDirection
import org.hammel.paintpdf.command.serialization.SerializablePath
import org.hammel.paintpdf.command.serialization.SerializableTypeface
import org.hammel.paintpdf.common.CommonFactory
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.drawable.ShapeDrawable
import org.hammel.paintpdf.tools.helper.JavaFillAlgorithmFactory
import org.hammel.paintpdf.tools.helper.toPoint

class DefaultCommandFactory : CommandFactory {
    private val commonFactory = CommonFactory()
    override fun createInitCommand(width: Int, height: Int): Command = CompositeCommand().apply {
        addCommand(SetDimensionCommand(width, height))
        addCommand(AddEmptyLayerCommand(commonFactory))
    }

    override fun createInitCommand(bitmap: Bitmap): Command = CompositeCommand().apply {
        addCommand(SetDimensionCommand(bitmap.width, bitmap.height))
        addCommand(LoadCommand(bitmap))
    }

    override fun createInitCommand(layers: List<LayerContracts.Layer>): Command = CompositeCommand().apply {
        layers[0].let {
            val bitmap = it.bitmap
            addCommand(SetDimensionCommand(bitmap.width, bitmap.height))
        }
        addCommand(LoadLayerListCommand(layers))
    }

    override fun createResetCommand(): Command = CompositeCommand().apply {
        addCommand(ResetCommand())
        addCommand(AddEmptyLayerCommand(commonFactory))
    }

    override fun createAddEmptyLayerCommand(): Command = AddEmptyLayerCommand(commonFactory)

    override fun createSelectLayerCommand(position: Int): Command = SelectLayerCommand(position)

    override fun createLayerOpacityCommand(position: Int, opacityPercentage: Int): Command = LayerOpacityCommand(position, opacityPercentage)

    override fun createRemoveLayerCommand(index: Int): Command = RemoveLayerCommand(index)

    override fun createReorderLayersCommand(position: Int, swapWith: Int): Command =
        ReorderLayersCommand(position, swapWith)

    override fun createMergeLayersCommand(position: Int, mergeWith: Int): Command =
        MergeLayersCommand(position, mergeWith)

    override fun createRotateCommand(rotateDirection: RotateDirection): Command =
        RotateCommand(rotateDirection)

    override fun createFlipCommand(flipDirection: FlipDirection): Command =
        FlipCommand(flipDirection)

    override fun createCropCommand(
        resizeCoordinateXLeft: Int,
        resizeCoordinateYTop: Int,
        resizeCoordinateXRight: Int,
        resizeCoordinateYBottom: Int,
        maximumBitmapResolution: Int
    ): Command = CropCommand(
        resizeCoordinateXLeft,
        resizeCoordinateYTop,
        resizeCoordinateXRight,
        resizeCoordinateYBottom,
        maximumBitmapResolution
    )

    override fun createPointCommand(paint: Paint, coordinate: PointF): Command = PointCommand(
        commonFactory.createPaint(paint),
        commonFactory.createPointF(coordinate)
    )

    override fun createFillCommand(x: Int, y: Int, paint: Paint, colorTolerance: Float): Command =
        FillCommand(
            JavaFillAlgorithmFactory(),
            commonFactory.createPoint(x, y),
            commonFactory.createPaint(paint),
            colorTolerance
        )

    override fun createGeometricFillCommand(
        shapeDrawable: ShapeDrawable,
        position: Point,
        box: RectF,
        boxRotation: Float,
        paint: Paint
    ): Command {
        val destRectF = commonFactory.createRectF(box)
        return GeometricFillCommand(
            shapeDrawable,
            position.x,
            position.y,
            destRectF,
            boxRotation,
            commonFactory.createPaint(paint)
        )
    }

    override fun createPathCommand(paint: Paint, path: SerializablePath): Command = PathCommand(
        commonFactory.createPaint(paint),
        commonFactory.createSerializablePath(path)
    )

    override fun createSmudgePathCommand(
        bitmap: Bitmap,
        pointPath: MutableList<PointF>,
        maxPressure: Float,
        maxSize: Float,
        minSize: Float
    ): Command {
        val copy = mutableListOf<PointF>()

        pointPath.forEach {
            copy.add(commonFactory.createPointF(it))
        }

        return SmudgePathCommand(bitmap.copy(Bitmap.Config.ARGB_8888, false), copy, maxPressure, maxSize, minSize)
    }

    override fun createTextToolCommand(
        multilineText: Array<String>,
        textPaint: Paint,
        boxOffset: Int,
        boxWidth: Float,
        boxHeight: Float,
        toolPosition: PointF,
        boxRotation: Float,
        typefaceInfo: SerializableTypeface
    ): Command = TextToolCommand(
        multilineText, commonFactory.createPaint(textPaint),
        boxOffset.toFloat(), boxWidth, boxHeight, commonFactory.createPointF(toolPosition),
        boxRotation, typefaceInfo
    )

    override fun createResizeCommand(newWidth: Int, newHeight: Int): Command =
        ResizeCommand(newWidth, newHeight)

    override fun createClipboardCommand(
        bitmap: Bitmap,
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command = ClipboardCommand(
        bitmap,
        toPoint(toolPosition),
        boxWidth,
        boxHeight,
        boxRotation
    )

    override fun createClippingCommand(bitmap: Bitmap, pathBitmap: Bitmap): Command =
        ClippingCommand(
            bitmap,
            pathBitmap
        )

    override fun createSprayCommand(sprayedPoints: FloatArray, paint: Paint): Command =
        SprayCommand(sprayedPoints, paint)

    override fun createCutCommand(
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command = CutCommand(toPoint(toolPosition), boxWidth, boxHeight, boxRotation)

    override fun createColorChangedCommand(
        tool: Tool?,
        context: Context,
        color: Int
    ): Command =
        ColorChangedCommand(tool, context, color)
}
