package org.hammel.paintpdf.tools.options

import org.hammel.paintpdf.ui.tools.NumberRangeFilter

interface TransformToolOptionsView {
    fun setWidthFilter(numberRangeFilter: NumberRangeFilter)

    fun setHeightFilter(numberRangeFilter: NumberRangeFilter)

    fun setCallback(callback: Callback)

    fun setWidth(width: Int)

    fun setHeight(height: Int)

    interface Callback {
        fun autoCropClicked()

        fun setCenterClicked()

        fun rotateCounterClockwiseClicked()

        fun rotateClockwiseClicked()

        fun flipHorizontalClicked()

        fun flipVerticalClicked()

        fun applyResizeClicked(resizePercentage: Int)

        fun setBoxWidth(boxWidth: Float)

        fun setBoxHeight(boxHeight: Float)

        fun hideToolOptions()
    }
}
