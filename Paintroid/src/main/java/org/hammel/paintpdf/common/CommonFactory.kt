package org.hammel.paintpdf.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import org.hammel.paintpdf.command.serialization.SerializablePath

open class CommonFactory {
    open fun createCanvas() = Canvas()

    open fun createBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap =
        Bitmap.createBitmap(width, height, config)

    fun createPaint(paint: Paint?) = Paint(paint)

    fun createPointF(point: PointF) = PointF(point.x, point.y)

    fun createPoint(x: Int, y: Int) = Point(x, y)

    open fun createSerializablePath(path: SerializablePath): SerializablePath =
        SerializablePath(path)

    fun createRectF(rect: RectF?) = RectF(rect)
}
