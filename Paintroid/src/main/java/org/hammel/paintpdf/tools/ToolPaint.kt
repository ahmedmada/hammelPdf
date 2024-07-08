package org.hammel.paintpdf.tools

import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PorterDuffXfermode
import android.graphics.Shader

interface ToolPaint {
    var paint: Paint
    val previewPaint: Paint
    var color: Int
    val eraseXfermode: PorterDuffXfermode
    val previewColor: Int
    var strokeWidth: Float
    var strokeCap: Cap
    val checkeredShader: Shader?

    fun setAntialiasing()
}
