package org.hammel.paintpdf.tools.helper

import android.graphics.Point
import android.graphics.PointF

fun toPoint(point: PointF): Point = Point(point.x.toInt(), point.y.toInt())
