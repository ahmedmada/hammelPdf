package org.hammel.paintpdf.tools.implementation

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.Workspace
import org.hammel.paintpdf.ui.Perspective

class DefaultWorkspace(
    override val layerModel: LayerContracts.Model,
    override var perspective: Perspective,
    private val listener: Listener
) : Workspace {
    override val height: Int
        get() = layerModel.height

    override val width: Int
        get() = layerModel.width

    override val surfaceWidth: Int
        get() = perspective.surfaceWidth

    override val surfaceHeight: Int
        get() = perspective.surfaceHeight

    override val bitmapOfAllLayers: Bitmap?
        get() = layerModel.getBitmapOfAllLayers()

    override val bitmapListOfAllLayers: List<Bitmap?>
        get() = layerModel.getBitmapListOfAllLayers()

    override var bitmapOfCurrentLayer: Bitmap? = null
        get() = layerModel.currentLayer?.bitmap?.let { Bitmap.createBitmap(it) }

    override val currentLayerIndex: Int
        get() {
            var index = -1
            layerModel.currentLayer?.let {
                index = layerModel.getLayerIndexOf(it)
            }
            return index
        }

    override val scaleForCenterBitmap: Float
        get() = perspective.scaleForCenterBitmap

    override var scale: Float
        get() = perspective.scale
        set(zoomFactor) {
            perspective.scale = zoomFactor
        }

    override fun resetPerspective() {
        perspective.setBitmapDimensions(width, height)
        perspective.resetScaleAndTranslation()
    }

    override fun getCanvasPointFromSurfacePoint(surfacePoint: PointF): PointF =
        perspective.getCanvasPointFromSurfacePoint(surfacePoint)

    override fun invalidate() {
        listener.invalidate()
    }

    override fun contains(point: PointF): Boolean =
        point.x < width && point.x >= 0 && point.y < height && point.y >= 0

    override fun intersectsWith(rectF: RectF): Boolean =
        0 < rectF.right && rectF.left < width && 0 < rectF.bottom && rectF.top < height

    override fun getSurfacePointFromCanvasPoint(coordinate: PointF): PointF =
        perspective.getSurfacePointFromCanvasPoint(coordinate)

    fun interface Listener {
        fun invalidate()
    }
}
