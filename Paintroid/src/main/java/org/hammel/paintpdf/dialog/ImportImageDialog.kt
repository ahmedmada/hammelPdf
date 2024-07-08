package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import paintpdf.R

class ImportImageDialog : MainActivityDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val importGallery = view.findViewById<LinearLayout>(R.id.pdfPaint_dialog_import_gallery)
        val importStickers =
            view.findViewById<LinearLayout>(R.id.pdfPaint_dialog_import_stickers)
        importGallery.setOnClickListener {
            presenter.importFromGalleryClicked()
            dismiss()
        }
        importStickers.setOnClickListener {
            presenter.importStickersClicked()
            dismiss()
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_import_image, null)
        onViewCreated(layout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.dialog_import_image_title)
            .setView(layout)
            .setNegativeButton(R.string.pdfPaint_cancel) { _, _ -> dismiss() }
            .create()
    }
}
