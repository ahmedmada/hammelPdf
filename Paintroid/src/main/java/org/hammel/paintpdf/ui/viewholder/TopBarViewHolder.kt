package org.hammel.paintpdf.ui.viewholder

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import org.hammel.paintpdf.contract.MainActivityContracts
import paintpdf.R

class TopBarViewHolder(val layout: ViewGroup) : MainActivityContracts.TopBarViewHolder {
    private val toolbar: Toolbar = layout.findViewById(R.id.pdfPaint_toolbar)
    val undoButton: ImageButton = layout.findViewById(R.id.pdfPaint_btn_top_undo)
    val redoButton: ImageButton = layout.findViewById(R.id.pdfPaint_btn_top_redo)
    val checkmarkButton: ImageButton = layout.findViewById(R.id.pdfPaint_btn_top_checkmark)
    var plusButton: ImageButton = layout.findViewById(R.id.pdfPaint_btn_top_plus)
    var managePages: ImageButton = layout.findViewById(R.id.pdfPaint_btn_manage_pages)
    var removePages: ImageButton = layout.findViewById(R.id.pdfPaint_btn_remove_pages)

    override val height: Int
        get() = layout.height

    override fun enableUndoButton() {
        undoButton.isEnabled = true
    }

    override fun disableUndoButton() {
        undoButton.isEnabled = false
    }

    override fun enableRedoButton() {
        redoButton.isEnabled = true
    }

    override fun disableRedoButton() {
        redoButton.isEnabled = false
    }

    override fun hide() {
        layout.visibility = View.GONE
    }

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    fun hidePlusButton() {
        plusButton.visibility = View.GONE
    }

    fun showPlusButton() {
        plusButton.visibility = View.VISIBLE
    }

    override fun removeStandaloneMenuItems(menu: Menu?) {
        menu?.apply {
            removeItem(R.id.pdfPaint_options_save_image)
            removeItem(R.id.pdfPaint_options_save_duplicate)
            removeItem(R.id.pdfPaint_options_new_image)
            removeItem(R.id.pdfPaint_options_rate_us)
        }
    }

    override fun removeCatroidMenuItems(menu: Menu?) {
        menu?.apply {
            removeItem(R.id.pdfPaint_options_export)
            removeItem(R.id.pdfPaint_options_discard_image)
        }
    }

    override fun hideTitleIfNotStandalone() {
        toolbar.title = ""
    }
}
