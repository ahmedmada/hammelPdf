package org.hammel.paintpdf.ui.tools

import android.graphics.Paint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.google.android.material.textfield.TextInputEditText
import paintpdf.R
import org.hammel.paintpdf.tools.helper.DefaultNumberRangeFilter
import org.hammel.paintpdf.tools.options.SprayToolOptionsView
import java.util.Locale

const val MIN_RADIUS = 1
private const val DEFAULT_RADIUS = 5
private const val MAX_RADIUS = 100

class DefaultSprayToolOptionsView(rootView: ViewGroup) : SprayToolOptionsView {

    private var callback: SprayToolOptionsView.Callback? = null
    private val radiusText: TextInputEditText
    private val radiusSeekBar: SeekBar

    companion object {
        private val TAG = DefaultSprayToolOptionsView::class.java.simpleName
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val sprayToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_spray_tool, rootView)
        radiusText = sprayToolOptionsView.findViewById(R.id.pdfPaint_radius_text)
        radiusSeekBar = sprayToolOptionsView.findViewById(R.id.pdfPaint_spray_radius_seek_bar)
        initializeListeners()
    }

    private fun initializeListeners() {
        radiusSeekBar.progress = DEFAULT_RADIUS
        radiusText.setText(radiusSeekBar.progress.toString())
        radiusText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_RADIUS, MAX_RADIUS))
        radiusText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val sizeText: String = radiusText.text.toString()
                val sizeTextInt = try {
                    sizeText.toInt()
                } catch (exp: NumberFormatException) {
                    exp.localizedMessage?.let {
                        Log.d(TAG, it)
                    }
                    MIN_RADIUS
                }
                radiusSeekBar.progress = sizeTextInt
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                var progressValue = progress
                if (progressValue < MIN_RADIUS) {
                    progressValue = MIN_RADIUS
                    radiusSeekBar.progress = progressValue
                }

                if (fromUser) {
                    radiusText.setText(progressValue.toString())
                }

                callback?.radiusChanged(progressValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
    }

    override fun setCallback(callback: SprayToolOptionsView.Callback?) {
        this.callback = callback
    }

    override fun setRadius(radius: Int) {
        radiusSeekBar.progress = radius
        radiusText.setText(
            String.format(
                Locale.getDefault(), "%d",
                radius
            )
        )
    }

    override fun setCurrentPaint(paint: Paint) {
        setRadius(paint.strokeWidth.toInt())
    }

    override fun getRadius(): Float = radiusSeekBar.progress.toFloat()
}
