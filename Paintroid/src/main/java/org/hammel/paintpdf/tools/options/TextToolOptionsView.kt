package org.hammel.paintpdf.tools.options

import android.view.View
import org.hammel.paintpdf.tools.FontType

interface TextToolOptionsView {
    fun setState(
        bold: Boolean,
        italic: Boolean,
        underlined: Boolean,
        text: String,
        textSize: Int,
        fontType: FontType
    )

    fun setCallback(listener: Callback)

    fun hideKeyboard()

    fun showKeyboard()

    fun getTopLayout(): View

    fun getBottomLayout(): View

    fun setShapeSizeText(shapeSize: String)

    fun toggleShapeSizeVisibility(isVisible: Boolean)

    interface Callback {
        fun setText(text: String)

        fun setFont(fontType: FontType)

        fun setUnderlined(underlined: Boolean)

        fun setItalic(italic: Boolean)

        fun setBold(bold: Boolean)

        fun setTextSize(size: Int)

        fun hideToolOptions()
    }
}
