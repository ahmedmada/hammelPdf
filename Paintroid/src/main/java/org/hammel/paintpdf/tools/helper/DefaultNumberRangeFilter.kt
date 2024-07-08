package org.hammel.paintpdf.tools.helper

import android.text.Spanned
import android.util.Log
import org.hammel.paintpdf.ui.tools.NumberRangeFilter

class DefaultNumberRangeFilter(private val min: Int, override var max: Int) : NumberRangeFilter {
    companion object {
        private val TAG = DefaultNumberRangeFilter::class.java.simpleName
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toInt()
            if (input in min..max) {
                return null
            }
        } catch (nfe: NumberFormatException) {
            nfe.localizedMessage?.let {
                Log.d(TAG, it)
            }
        }
        return ""
    }
}
