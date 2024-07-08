package org.hammel.paintpdf.tools.options

import android.view.View
import org.hammel.paintpdf.tools.drawable.DrawableShape
import org.hammel.paintpdf.tools.drawable.DrawableStyle

interface ShapeToolOptionsView {
    fun setShapeActivated(shape: DrawableShape)

    fun setDrawTypeActivated(drawType: DrawableStyle)

    fun setShapeOutlineWidth(outlineWidth: Int)

    fun setCallback(callback: Callback)

    fun setShapeSizeText(shapeSize: String)

    fun toggleShapeSizeVisibility(isVisible: Boolean)

    fun getShapeToolOptionsLayout(): View

    interface Callback {
        fun setToolType(shape: DrawableShape)

        fun setDrawType(drawType: DrawableStyle)

        fun setOutlineWidth(outlineWidth: Int)
    }
}
