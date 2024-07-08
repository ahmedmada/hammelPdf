package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class RateUsDialog : MainActivityDialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setMessage(getString(R.string.pdfPaint_rate_us))
            .setTitle(getString(R.string.pdfPaint_rate_us_title))
            .setPositiveButton(R.string.pdfPaint_yes) { _, _ ->
                presenter.rateUsClicked()
                dismiss()
            }
            .setNegativeButton(R.string.pdfPaint_not_now) { _, _ -> dismiss() }
            .create()
}
