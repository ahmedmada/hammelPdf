package org.hammel.paintpdf.tools

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.ui.Perspective

interface Workspace {
    val height: Int
    val width: Int
    val surfaceWidth: Int
    val surfaceHeight: Int
    val bitmapOfAllLayers: Bitmap?
    val bitmapListOfAllLayers: List<Bitmap?>
    var bitmapOfCurrentLayer: Bitmap?
    val currentLayerIndex: Int
    val scaleForCenterBitmap: Float
    var scale: Float
    var perspective: Perspective
    val layerModel: LayerContracts.Model

    fun contains(point: PointF): Boolean

    fun intersectsWith(rectF: RectF): Boolean

    fun resetPerspective()

    fun getSurfacePointFromCanvasPoint(coordinate: PointF): PointF

    fun getCanvasPointFromSurfacePoint(surfacePoint: PointF): PointF

    fun invalidate()
}
