package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class SaveBeforeNewImageDialog : MainActivityDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireActivity(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.menu_new_image)
            .setMessage(R.string.dialog_warning_new_image)
            .setPositiveButton(R.string.save_button_text) { _, _ -> presenter.saveBeforeNewImage() }
            .setNegativeButton(R.string.discard_button_text) { _, _ -> presenter.onNewImage() }
            .create()
}
