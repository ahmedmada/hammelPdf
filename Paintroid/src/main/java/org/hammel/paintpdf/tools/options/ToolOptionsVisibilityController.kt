package org.hammel.paintpdf.tools.options

interface ToolOptionsVisibilityController {
    val isVisible: Boolean

    fun hide()

    fun setCallback(callback: Callback)

    fun show(isFullScreen: Boolean = false)

    fun showDelayed()

    interface Callback {
        fun onHide()

        fun onShow()
    }
}
