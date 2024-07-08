package org.hammel.paintpdf.tools.options

interface SmudgeToolOptionsView : BrushToolOptionsView {
    fun setCallback(callback: Callback)

    interface Callback {
        fun onPressureChanged(pressure: Int)

        fun onDragChanged(drag: Int)
    }
}
