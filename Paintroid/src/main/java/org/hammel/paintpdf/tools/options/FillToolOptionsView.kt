package org.hammel.paintpdf.tools.options

interface FillToolOptionsView {
    fun setCallback(callback: Callback)

    interface Callback {
        fun onColorToleranceChanged(colorTolerance: Int)
    }
}
