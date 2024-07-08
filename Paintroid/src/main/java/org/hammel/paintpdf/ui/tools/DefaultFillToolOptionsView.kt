package org.hammel.paintpdf.ui.tools

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import paintpdf.R
import org.hammel.paintpdf.tools.helper.DefaultNumberRangeFilter
import org.hammel.paintpdf.tools.implementation.DEFAULT_TOLERANCE_IN_PERCENT
import org.hammel.paintpdf.tools.options.FillToolOptionsView
import java.lang.NumberFormatException
import java.util.Locale

private const val MIN_VAL = 0
private const val MAX_VAL = 100

class DefaultFillToolOptionsView(toolSpecificOptionsLayout: ViewGroup) : FillToolOptionsView {
    private val colorToleranceSeekBar: AppCompatSeekBar
    private val colorToleranceEditText: AppCompatEditText
    private var callback: FillToolOptionsView.Callback? = null

    init {
        val inflater = LayoutInflater.from(toolSpecificOptionsLayout.context)
        val fillToolOptionsView =
            inflater.inflate(R.layout.dialog_pocketpaint_fill_tool, toolSpecificOptionsLayout)
        colorToleranceSeekBar =
            fillToolOptionsView.findViewById(R.id.pdfPaint_color_tolerance_seek_bar)
        colorToleranceEditText =
            fillToolOptionsView.findViewById(R.id.pdfPaint_fill_tool_dialog_color_tolerance_input)
        colorToleranceEditText.filters =
            arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_VAL, MAX_VAL))
        colorToleranceSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    setColorToleranceText(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        colorToleranceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable) {
                try {
                    val colorToleranceInPercent = s.toString().toInt()
                    colorToleranceSeekBar.progress = colorToleranceInPercent
                    updateColorTolerance(colorToleranceInPercent)
                } catch (e: NumberFormatException) {
                    Log.e("Error parsing tolerance", "result was null")
                }
            }
        })

        setColorToleranceText(DEFAULT_TOLERANCE_IN_PERCENT)
    }

    private fun updateColorTolerance(colorTolerance: Int) {
        callback?.onColorToleranceChanged(colorTolerance)
    }

    private fun setColorToleranceText(toleranceInPercent: Int) {
        colorToleranceEditText.setText(String.format(Locale.getDefault(), "%d", toleranceInPercent))
    }

    override fun setCallback(callback: FillToolOptionsView.Callback) {
        this.callback = callback
    }
}
