package org.hammel.paintpdf.colorpicker

interface OnColorChangedListener {
    fun colorChanged(color: Int)
}

interface OnColorPickedListener {
    fun colorChanged(color: Int)
}

interface OnImageViewPointClickedListener {
    fun colorChanged(color: Int)
}

interface OnColorInHistoryChangedListener {
    fun colorInHistoryChanged(color: Int)
}
