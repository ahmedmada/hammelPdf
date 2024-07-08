package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import org.hammel.paintpdf.MainActivity
import paintpdf.R

class AboutDialog : AppCompatDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (showsDialog) {
            super.onCreateView(inflater, container, savedInstanceState)
        } else {
            inflater.inflate(R.layout.dialog_pocketpaint_about, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val aboutVersionView = view.findViewById<TextView>(R.id.pdfPaint_about_version)
        val aboutContentView = view.findViewById<TextView>(R.id.pdfPaint_about_content)
        val aboutLicenseView = view.findViewById<TextView>(R.id.pdfPaint_about_license_url)
        val aboutCatrobatView = view.findViewById<TextView>(R.id.pdfPaint_about_catrobat_url)
        val activity = requireActivity() as MainActivity
        val aboutVersion = getString(R.string.pdfPaint_about_version, activity.getVersionCode())
        aboutVersionView.text = aboutVersion
        val aboutContent = getString(
            R.string.pdfPaint_about_content,
            getString(R.string.pdfPaint_about_license)
        )
        aboutContentView.text = aboutContent
        val licenseUrl = getString(
            R.string.pdfPaint_about_url_license,
            getString(R.string.pdfPaint_about_url_license_description)
        )
        aboutLicenseView.text = HtmlCompat.fromHtml(licenseUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        aboutLicenseView.movementMethod = LinkMovementMethod.getInstance()
        val catrobatUrl = getString(
            R.string.pdfPaint_about_url_catrobat,
            getString(R.string.pdfPaint_about_url_catrobat_description)
        )
        aboutCatrobatView.text = HtmlCompat.fromHtml(catrobatUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        aboutCatrobatView.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_about, null)
        onViewCreated(layout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.pdfPaint_about_title)
            .setView(layout)
            .setPositiveButton(R.string.done) { _, _ -> dismiss() }
            .create()
    }
}
