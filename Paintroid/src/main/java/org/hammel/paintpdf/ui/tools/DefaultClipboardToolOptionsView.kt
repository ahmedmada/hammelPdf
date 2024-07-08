package org.hammel.paintpdf.ui.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.chip.Chip
import paintpdf.R
import org.hammel.paintpdf.tools.options.ClipboardToolOptionsView

class DefaultClipboardToolOptionsView(rootView: ViewGroup) : ClipboardToolOptionsView {
    private val pasteChip: Chip
    private val copyChip: Chip
    private val cutChip: Chip
    private val shapeSizeChip: Chip
    private val changeSizeShapeSizeChip: Chip
    private val clipboardToolOptionsView: View
    private var changeSizeShapeSizeChipVisible = false

    private var callback: ClipboardToolOptionsView.Callback? = null

    private fun initializeListeners() {
        copyChip.setOnClickListener {
            callback?.copyClicked()
        }

        cutChip.setOnClickListener {
            callback?.cutClicked()
        }

        pasteChip.setOnClickListener {
            callback?.pasteClicked()
        }
    }

    override fun setCallback(callback: ClipboardToolOptionsView.Callback) {
        this.callback = callback
    }

    override fun enablePaste(enable: Boolean) {
        pasteChip.isEnabled = enable
    }

    override fun toggleShapeSizeVisibility(isVisible: Boolean) {
        if (isVisible && !changeSizeShapeSizeChipVisible && clipboardToolOptionsView.visibility == View.INVISIBLE) {
            changeSizeShapeSizeChip.visibility = View.VISIBLE
        } else {
            if (isVisible && clipboardToolOptionsView.visibility == View.GONE) changeSizeShapeSizeChip.visibility = View.VISIBLE
            if (!isVisible) changeSizeShapeSizeChip.visibility = View.GONE
        }
        changeSizeShapeSizeChipVisible = isVisible
    }

    override fun getClipboardToolOptionsLayout(): View = clipboardToolOptionsView

    override fun setShapeSizeText(shapeSize: String) {
        shapeSizeChip.setText(shapeSize)
        changeSizeShapeSizeChip.setText(shapeSize)
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val stampToolOptionsView: View =
            inflater.inflate(R.layout.dialog_pocketpaint_clipboard_tool, rootView)
        copyChip = stampToolOptionsView.findViewById(R.id.action_copy)
        pasteChip = stampToolOptionsView.findViewById(R.id.action_paste)
        cutChip = stampToolOptionsView.findViewById(R.id.action_cut)
        enablePaste(false)
        initializeListeners()
        stampToolOptionsView.run {
            val viewShapeSizeLayout =
                findViewById<LinearLayout>(R.id.pdfPaint_layout_clipboard_tool_options_view_shape_size)
            shapeSizeChip = viewShapeSizeLayout.findViewById(R.id.pdfPaint_fill_shape_size_text)
            val changeShapeSizeLayout =
                findViewById<LinearLayout>(R.id.pdfPaint_layout_clipboard_tool_change_size_shape_size)
            changeSizeShapeSizeChip = changeShapeSizeLayout.findViewById(R.id.pdfPaint_fill_shape_size_text)
            clipboardToolOptionsView = findViewById(R.id.pdfPaint_layout_clipboard_tool_options)
        }
        toggleShapeSizeVisibility(false)
    }
}
