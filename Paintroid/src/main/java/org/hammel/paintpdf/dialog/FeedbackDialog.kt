package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import paintpdf.R

class FeedbackDialog : AppCompatDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setMessage(R.string.pdfPaint_feedback)
            .setTitle(R.string.pdfPaint_rate_us_title)
            .setPositiveButton(R.string.pdfPaint_ok) { _, _ -> dismiss() }
            .create()
    }
}
