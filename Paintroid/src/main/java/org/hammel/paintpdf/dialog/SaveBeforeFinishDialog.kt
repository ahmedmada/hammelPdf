package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class SaveBeforeFinishDialog : MainActivityDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireActivity(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.closing_security_question_title)
            .setMessage(R.string.closing_security_question)
            .setPositiveButton(R.string.save_button_text) { _, _ -> presenter.saveBeforeFinish() }
            .setNegativeButton(R.string.discard_button_text) { _, _ -> presenter.finishActivity() }
            .create()
}
