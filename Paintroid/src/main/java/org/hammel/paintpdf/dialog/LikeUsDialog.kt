package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class LikeUsDialog : MainActivityDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setMessage(getString(R.string.pdfPaint_like_us))
            .setTitle(getString(R.string.pdfPaint_rate_us_title))
            .setPositiveButton(R.string.pdfPaint_yes) { _, _ ->
                presenter.showRateUsDialog()
                dismiss()
            }
            .setNegativeButton(R.string.pdfPaint_no) { _, _ ->
                presenter.showFeedbackDialog()
                dismiss()
            }
            .create()
    }
}
