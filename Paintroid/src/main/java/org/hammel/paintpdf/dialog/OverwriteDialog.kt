package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.defaultFileName
import org.hammel.paintpdf.FileIO.fileType
import org.hammel.paintpdf.FileIO.getUriForFilenameInDownloadsFolder
import org.hammel.paintpdf.FileIO.getUriForFilenameInPicturesFolder
import paintpdf.R

class OverwriteDialog : MainActivityDialogFragment() {
    private var permission = 0
    private var isExport = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        isExport = arguments.getBoolean(IS_EXPORT)
        permission = arguments.getInt(PERMISSION)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.pdfPaint_overwrite_title)
            .setMessage(
                resources.getString(
                    R.string.pdfPaint_overwrite,
                    getString(R.string.menu_save_copy)
                )
            )
            .setPositiveButton(R.string.overwrite_button_text) { _, _ ->
                val resolver = requireContext().contentResolver
                var storeImageUri = when (fileType) {
                    FileIO.FileType.JPG, FileIO.FileType.PNG -> getUriForFilenameInPicturesFolder(
                        defaultFileName, resolver)
                    FileIO.FileType.CATROBAT, FileIO.FileType.ORA -> getUriForFilenameInDownloadsFolder(
                        defaultFileName, resolver)
                }

                storeImageUri = storeImageUri
                presenter.switchBetweenVersions(permission, isExport)
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ -> dismiss() }
            .create()

    companion object {
        private const val PERMISSION = "permission"
        private const val IS_EXPORT = "isExport"

        fun newInstance(permissionCode: Int, isExport: Boolean): OverwriteDialog =
            OverwriteDialog().apply {
                arguments = bundleOf(PERMISSION to permissionCode, IS_EXPORT to isExport)
            }
    }
}
