package org.hammel.paintpdf.tools.options

import android.view.View

interface ClipboardToolOptionsView {
    fun setCallback(callback: Callback)

    fun enablePaste(enable: Boolean)

    fun setShapeSizeText(shapeSize: String)

    fun toggleShapeSizeVisibility(isVisible: Boolean)

    fun getClipboardToolOptionsLayout(): View

    interface Callback {
        fun copyClicked()

        fun cutClicked()

        fun pasteClicked()
    }
}
