package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import paintpdf.R

class IndeterminateProgressDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintProgressDialog)
            .setView(R.layout.pocketpaint_layout_indeterminate)
            .create()
    }

    override fun onPause() {
        super.onPause()
        dismissAllowingStateLoss()
    }
}
