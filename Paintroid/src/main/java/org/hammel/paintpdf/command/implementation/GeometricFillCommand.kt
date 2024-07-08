
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.drawable.ShapeDrawable

class GeometricFillCommand(
    shapeDrawable: ShapeDrawable,
    pointX: Int,
    pointY: Int,
    boxRect: RectF,
    boxRotation: Float,
    paint: Paint
) : Command {

    var shapeDrawable = shapeDrawable; private set
    var pointX = pointX; private set
    var pointY = pointY; private set
    var boxRect = boxRect; private set
    var boxRotation = boxRotation; private set
    var paint = paint; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        with(canvas) {
            save()
            translate(pointX.toFloat(), pointY.toFloat())
            rotate(boxRotation)
            shapeDrawable.draw(this, boxRect, paint)
            restore()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
