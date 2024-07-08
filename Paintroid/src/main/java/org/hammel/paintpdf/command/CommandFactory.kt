package org.hammel.paintpdf.command

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import org.hammel.paintpdf.command.implementation.FlipCommand.FlipDirection
import org.hammel.paintpdf.command.implementation.RotateCommand.RotateDirection
import org.hammel.paintpdf.command.serialization.SerializablePath
import org.hammel.paintpdf.command.serialization.SerializableTypeface
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.drawable.ShapeDrawable

interface CommandFactory {
    fun createInitCommand(width: Int, height: Int): Command

    fun createInitCommand(bitmap: Bitmap): Command

    fun createInitCommand(layers: List<LayerContracts.Layer>): Command

    fun createResetCommand(): Command

    fun createAddEmptyLayerCommand(): Command

    fun createSelectLayerCommand(position: Int): Command

    fun createLayerOpacityCommand(position: Int, opacityPercentage: Int): Command

    fun createRemoveLayerCommand(index: Int): Command

    fun createReorderLayersCommand(position: Int, swapWith: Int): Command

    fun createMergeLayersCommand(position: Int, mergeWith: Int): Command

    fun createRotateCommand(rotateDirection: RotateDirection): Command

    fun createFlipCommand(flipDirection: FlipDirection): Command

    fun createCropCommand(
        resizeCoordinateXLeft: Int,
        resizeCoordinateYTop: Int,
        resizeCoordinateXRight: Int,
        resizeCoordinateYBottom: Int,
        maximumBitmapResolution: Int
    ): Command

    fun createPointCommand(paint: Paint, coordinate: PointF): Command

    fun createFillCommand(x: Int, y: Int, paint: Paint, colorTolerance: Float): Command

    fun createGeometricFillCommand(
        shapeDrawable: ShapeDrawable,
        position: Point,
        box: RectF,
        boxRotation: Float,
        paint: Paint
    ): Command

    fun createPathCommand(paint: Paint, path: SerializablePath): Command

    fun createSmudgePathCommand(
        bitmap: Bitmap,
        pointPath: MutableList<PointF>,
        maxPressure: Float,
        maxSize: Float,
        minSize: Float
    ): Command

    fun createTextToolCommand(
        multilineText: Array<String>,
        textPaint: Paint,
        boxOffset: Int,
        boxWidth: Float,
        boxHeight: Float,
        toolPosition: PointF,
        boxRotation: Float,
        typefaceInfo: SerializableTypeface
    ): Command

    fun createResizeCommand(newWidth: Int, newHeight: Int): Command

    fun createClipboardCommand(
        bitmap: Bitmap,
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command

    fun createSprayCommand(sprayedPoints: FloatArray, paint: Paint): Command

    fun createCutCommand(
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command

    fun createColorChangedCommand(tool: Tool?, context: Context, color: Int): Command

    fun createClippingCommand(
        bitmap: Bitmap,
        pathBitmap: Bitmap
    ): Command
}
