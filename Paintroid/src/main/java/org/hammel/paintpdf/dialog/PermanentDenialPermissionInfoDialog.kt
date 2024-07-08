package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import paintpdf.R

private const val CONTEXT = "context"

class PermanentDenialPermissionInfoDialog : AppCompatDialogFragment() {
    private var context: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireArguments().getString(CONTEXT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setMessage(R.string.permission_info_permanent_denial_text)
            .setPositiveButton(R.string.dialog_settings) { _, _ -> startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$context"))) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    companion object {
        @JvmStatic
        fun newInstance(context: String):
            PermanentDenialPermissionInfoDialog = PermanentDenialPermissionInfoDialog().apply {
                arguments = bundleOf(CONTEXT to context)
            }
    }
}
