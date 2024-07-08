package org.hammel.paintpdf.tools.options

import org.hammel.paintpdf.tools.options.BrushToolOptionsView.OnBrushPreviewListener

interface BrushToolPreview {
    fun setListener(callback: OnBrushPreviewListener)

    fun invalidate()
}
