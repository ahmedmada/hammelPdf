package org.hammel.paintpdf.ui.tools

import android.text.InputFilter
import android.text.Spanned

interface NumberRangeFilter : InputFilter {
    var max: Int

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence?
}
