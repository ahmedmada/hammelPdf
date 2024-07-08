
package org.hammel.paintpdf.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.RectF
import androidx.annotation.VisibleForTesting
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.getBitmapFromFile
import org.hammel.paintpdf.PaintroidApplication
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import java.io.File
import java.io.FileOutputStream
import java.util.Random

class ClipboardCommand(bitmap: Bitmap, position: Point, width: Float, height: Float, rotation: Float) :
    Command {

    var bitmap: Bitmap? = bitmap.copy(Bitmap.Config.ARGB_8888, false); private set
    var coordinates = position; private set
    var boxRotation = rotation; private set
    var boxWidth = width; private set
    var boxHeight = height; private set
    var fileToStoredBitmap: File? = null

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        var bitmapToDraw = bitmap
        if (fileToStoredBitmap != null) {
            bitmapToDraw = getBitmapFromFile(fileToStoredBitmap)
        }
        bitmapToDraw ?: return

        val rect = RectF(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f, boxHeight / 2f)
        with(canvas) {
            save()
            translate(coordinates.x.toFloat(), coordinates.y.toFloat())
            rotate(boxRotation)
            drawBitmap(bitmapToDraw, null, rect, null)
            restore()
        }

        if (fileToStoredBitmap == null) {
            storeBitmap(bitmapToDraw, boxWidth, boxHeight)
        }
        bitmap = recycleBitmap(bitmapToDraw)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun storeBitmap(bitmapToStore: Bitmap, boxWidth: Float, boxHeight: Float) {
        val random = Random()
        random.setSeed(System.currentTimeMillis())
        fileToStoredBitmap =
            File(PaintroidApplication.cacheDir?.absolutePath, random.nextLong().toString())
        val resizedBitmap = resizeBitmap(bitmapToStore, boxWidth, boxHeight)
        FileOutputStream(fileToStoredBitmap).use { stream ->
            resizedBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, stream)
        }
    }

    private fun resizeBitmap(bitmapToResize: Bitmap, boxWidth: Float, boxHeight: Float): Bitmap {
        val newWidth =
            if (boxWidth < bitmapToResize.width) boxWidth.toInt() else bitmapToResize.width
        val newHeight =
            if (boxHeight < bitmapToResize.height) boxHeight.toInt() else bitmapToResize.height
        if (newWidth == bitmapToResize.width && newHeight == bitmapToResize.height) {
            return bitmapToResize
        }
        return Bitmap.createScaledBitmap(bitmapToResize, newWidth, newHeight, false)
    }

    private fun recycleBitmap(bitmapToRecycle: Bitmap?): Bitmap? {
        return bitmapToRecycle?.let { bitmap ->
            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }
            null
        }
    }

    override fun freeResources() {
        bitmap = recycleBitmap(bitmap)
        fileToStoredBitmap?.let { file ->
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
