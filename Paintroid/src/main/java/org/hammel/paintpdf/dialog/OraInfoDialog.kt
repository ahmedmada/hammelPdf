package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import paintpdf.R

class OraInfoDialog : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setMessage(R.string.pdfPaint_ora_message_dialog)
            .setTitle(R.string.pdfPaint_ora_title_dialog)
            .setPositiveButton(R.string.pdfPaint_ok) { _, _ -> dismiss() }
            .create()
}
