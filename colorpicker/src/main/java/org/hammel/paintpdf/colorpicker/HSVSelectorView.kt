package org.hammel.paintpdf.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat

class HSVSelectorView : LinearLayoutCompat {
    val hsvColorPickerView: HSVColorPickerView = HSVColorPickerView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        addView(hsvColorPickerView)
    }

    fun setSelectedColor(color: Int) {
        hsvColorPickerView.selectedColor = color
    }
}
