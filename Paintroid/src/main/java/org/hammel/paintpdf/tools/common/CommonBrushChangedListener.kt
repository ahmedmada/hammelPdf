package org.hammel.paintpdf.tools.common

import android.graphics.Paint.Cap
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.options.BrushToolOptionsView.OnBrushChangedListener

class CommonBrushChangedListener(private val tool: Tool) : OnBrushChangedListener {
    override fun setCap(strokeCap: Cap) {
        tool.changePaintStrokeCap(strokeCap)
    }

    override fun setStrokeWidth(strokeWidth: Int) {
        tool.changePaintStrokeWidth(strokeWidth)
    }
}
