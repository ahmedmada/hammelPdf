package org.hammel.paintpdf.tools.drawable

class DrawableFactory {
    fun createDrawable(shape: DrawableShape): ShapeDrawable {
        return when (shape) {
            DrawableShape.RECTANGLE -> RectangleDrawable()
            DrawableShape.OVAL -> OvalDrawable()
            DrawableShape.HEART -> HeartDrawable()
            DrawableShape.STAR -> StarDrawable()
        }
    }
}
