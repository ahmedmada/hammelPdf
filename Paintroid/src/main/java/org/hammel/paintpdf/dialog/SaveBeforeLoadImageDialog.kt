package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class SaveBeforeLoadImageDialog : MainActivityDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireActivity(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.menu_load_image)
            .setMessage(R.string.dialog_warning_new_image)
            .setPositiveButton(R.string.save_button_text) { _, _ -> presenter.saveBeforeLoadImage() }
            .setNegativeButton(R.string.discard_button_text) { _, _ -> presenter.loadNewImage() }
            .create()
}
