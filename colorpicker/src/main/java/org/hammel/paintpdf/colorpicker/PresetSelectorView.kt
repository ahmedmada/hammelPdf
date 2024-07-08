package org.hammel.paintpdf.colorpicker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import paintpdf.colorpicker.R

private const val MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW = 4
private const val COLOR_BUTTON_MARGIN = 2

class PresetSelectorView : LinearLayout {
    private var selectedColor = 0
    private var tableLayout: TableLayout
    private var onColorChangedListener: OnColorChangedListener? = null

    constructor(context: Context) : this(context, null) {
        setWillNotDraw(false)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setWillNotDraw(false)
        tableLayout = TableLayout(context, attrs).apply {
            gravity = Gravity.TOP
            orientation = VERTICAL
            isStretchAllColumns = true
            isShrinkAllColumns = true
        }
        val presetButtonListener = OnClickListener { v ->
            selectedColor = (v as ColorPickerPresetColorButton).color
            onColorChanged()
        }
        val presetColors =
            resources.obtainTypedArray(R.array.pdfPaint_color_picker_preset_colors)
        var colorButtonsTableRow = TableRow(context)
        val colorButtonLayoutParameters = TableRow.LayoutParams()
        colorButtonLayoutParameters.setMargins(
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN,
            COLOR_BUTTON_MARGIN
        )
        for (colorButtonIndexInRow in 0 until presetColors.length()) {
            val color = presetColors.getColor(colorButtonIndexInRow, Color.TRANSPARENT)
            val colorButton: View = ColorPickerPresetColorButton(context, color)
            colorButton.setOnClickListener(presetButtonListener)
            colorButtonsTableRow.addView(colorButton, colorButtonLayoutParameters)
            if ((colorButtonIndexInRow + 1) % MAXIMUM_COLOR_BUTTONS_IN_COLOR_ROW == 0) {
                tableLayout.addView(colorButtonsTableRow)
                colorButtonsTableRow = TableRow(context)
            }
        }
        presetColors.recycle()
        addView(tableLayout)
    }

    private fun getSelectedColor(): Int = selectedColor

    fun setSelectedColor(color: Int) {
        selectedColor = color
    }

    private fun onColorChanged() {
        onColorChangedListener?.colorChanged(getSelectedColor())
    }

    fun setOnColorChangedListener(listener: OnColorChangedListener?) {
        onColorChangedListener = listener
    }

    fun interface OnColorChangedListener {
        fun colorChanged(color: Int)
    }
}
