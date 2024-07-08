package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import org.hammel.paintpdf.common.MainActivityConstants.LoadImageRequestCode
import paintpdf.R
private const val URI = "Uri"
private const val REQUEST_CODE = "requestCode"

class ScaleImageOnLoadDialog : MainActivityDialogFragment() {
    private var uri: Uri? = null
    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        uri = Uri.parse(arguments.getString(URI))
        requestCode = arguments.getInt(REQUEST_CODE)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireActivity(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.dialog_scale_title)
            .setMessage(R.string.dialog_scale_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                presenter.loadScaledImage(
                    uri,
                    requestCode
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    companion object {
        fun newInstance(uri: Uri, @LoadImageRequestCode requestCode: Int): ScaleImageOnLoadDialog =
            ScaleImageOnLoadDialog().apply {
                arguments = bundleOf(URI to uri.toString(), REQUEST_CODE to requestCode)
            }
    }
}
