package org.hammel.paintpdf.tools.common

import android.graphics.MaskFilter
import android.graphics.Paint.Cap
import org.hammel.paintpdf.tools.ToolPaint
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.tools.options.BrushToolOptionsView.OnBrushPreviewListener

class CommonBrushPreviewListener(
    private val toolPaint: ToolPaint,
    override val toolType: ToolType
) : OnBrushPreviewListener {
    override val strokeWidth: Float
        get() = toolPaint.strokeWidth

    override val strokeCap: Cap
        get() = toolPaint.strokeCap

    override val color: Int
        get() = toolPaint.color

    override val maskFilter: MaskFilter?
        get() = toolPaint.paint.maskFilter
}
