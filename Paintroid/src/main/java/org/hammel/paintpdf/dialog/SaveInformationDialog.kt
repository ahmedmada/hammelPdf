package org.hammel.paintpdf.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import org.hammel.paintpdf.FileIO.FileType
import org.hammel.paintpdf.FileIO.FileType.CATROBAT
import org.hammel.paintpdf.FileIO.FileType.JPG
import org.hammel.paintpdf.FileIO.FileType.ORA
import org.hammel.paintpdf.FileIO.FileType.PNG
import org.hammel.paintpdf.FileIO.checkFileExists
import org.hammel.paintpdf.FileIO.compressFormat
import org.hammel.paintpdf.FileIO.compressQuality
import org.hammel.paintpdf.FileIO.defaultFileName
import org.hammel.paintpdf.FileIO.fileType
import org.hammel.paintpdf.FileIO.filename
import org.hammel.paintpdf.FileIO.storeImageUri
import paintpdf.R
import java.util.Locale

private const val STANDARD_FILE_NAME = "image"
private const val SET_NAME = "setName"
private const val PERMISSION = "permission"
private const val IS_EXPORT = "isExport"

class SaveInformationDialog :
    MainActivityDialogFragment(),
    OnItemSelectedListener,
    OnSeekBarChangeListener {
    private lateinit var spinner: Spinner
    private lateinit var inflater: LayoutInflater
    private lateinit var specificFormatLayout: ViewGroup
    private lateinit var jpgView: View
    private lateinit var percentage: AppCompatTextView
    private lateinit var imageName: AppCompatEditText
    private lateinit var fileName: String
    private var permission = 0
    private var isExport = false

    companion object {
        fun newInstance(
            permissionCode: Int,
            imageNumber: Int,
            isStandard: Boolean,
            isExport: Boolean
        ): SaveInformationDialog {
            if (isStandard) {
                filename = STANDARD_FILE_NAME
                compressFormat = Bitmap.CompressFormat.PNG
                fileType = PNG
            }
            return SaveInformationDialog().apply {
                arguments = Bundle().apply {
                    if (filename == STANDARD_FILE_NAME) {
                        putString(SET_NAME, filename + imageNumber)
                    } else {
                        putString(SET_NAME, filename)
                    }
                    putInt(PERMISSION, permissionCode)
                    putBoolean(IS_EXPORT, isExport)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arguments = requireArguments()
        arguments.apply {
            permission = getInt(PERMISSION)
            fileName = getString(SET_NAME).toString()
            isExport = getBoolean(IS_EXPORT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setSpinnerSelection()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        inflater = requireActivity().layoutInflater
        val customLayout = inflater.inflate(R.layout.dialog_pocketpaint_save, null)
        onViewCreated(customLayout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setTitle(R.string.dialog_save_image_title)
            .setView(customLayout)
            .setPositiveButton(R.string.save_button_text) { _, _ ->
                filename = imageName.text.toString()
                storeImageUri = null
                if (checkFileExists(fileType, defaultFileName, requireContext().contentResolver)) {
                    presenter.showOverwriteDialog(permission, isExport)
                } else {
                    presenter.switchBetweenVersions(permission, isExport)
                }
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ -> dismiss() }
            .create()
    }

    private fun initViews(customLayout: View) {
        initSpecificFormatLayout(customLayout)
        initJpgView()
        initSeekBar()
        initPercentage()
        initSpinner(customLayout)
        initInfoButton(customLayout)
        initImageName(customLayout)
    }

    private fun initSpecificFormatLayout(view: View) {
        specificFormatLayout = view.findViewById(R.id.pdfPaint_save_format_specific_options)
    }

    private fun initJpgView() {
        jpgView = inflater.inflate(
            R.layout.dialog_pocketpaint_save_jpg_sub_dialog,
            specificFormatLayout,
            false
        )
    }

    private fun initSeekBar() {
        val seekBar: SeekBar = jpgView.findViewById(R.id.pdfPaint_jpg_seekbar_save_info)
        seekBar.progress = compressQuality
        seekBar.setOnSeekBarChangeListener(this)
    }

    private fun initPercentage() {
        percentage = jpgView.findViewById(R.id.pdfPaint_percentage_save_info)
        val percentageString = compressQuality.toString().plus('%')
        percentage.text = percentageString
    }

    private fun initInfoButton(view: View) {
        val infoButton: AppCompatImageButton = view.findViewById(R.id.pdfPaint_btn_save_info)
        infoButton.setOnClickListener {
            when (fileType) {
                JPG -> presenter.showJpgInformationDialog()
                ORA -> presenter.showOraInformationDialog()
                CATROBAT -> presenter.showCatrobatInformationDialog()
                else -> presenter.showPngInformationDialog()
            }
        }
    }

    private fun initSpinner(view: View) {
        spinner = view.findViewById(R.id.pdfPaint_save_dialog_spinner)
        val spinnerArray = FileType.values().map { it.value }
        val adapter =
            ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
    }

    private fun initImageName(view: View) {
        imageName = view.findViewById(R.id.pdfPaint_image_name_save_text)
        imageName.setText(fileName)
    }

    private fun setFileDetails(
        compressFormat: Bitmap.CompressFormat,
        fileType: FileType
    ) {
        specificFormatLayout.removeAllViews()
        if (fileType == JPG) {
            specificFormatLayout.addView(jpgView)
        }
        org.hammel.paintpdf.FileIO.compressFormat = compressFormat
        org.hammel.paintpdf.FileIO.fileType = fileType
    }

    private fun setSpinnerSelection() {
        when (fileType) {
            JPG -> spinner.setSelection(JPG.ordinal)
            ORA -> spinner.setSelection(ORA.ordinal)
            CATROBAT -> spinner.setSelection(CATROBAT.ordinal)
            else -> spinner.setSelection(PNG.ordinal)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.getItemAtPosition(position).toString().toLowerCase(Locale.getDefault())) {
            JPG.value -> setFileDetails(Bitmap.CompressFormat.JPEG, JPG)
            PNG.value -> setFileDetails(Bitmap.CompressFormat.PNG, PNG)
            ORA.value -> setFileDetails(Bitmap.CompressFormat.PNG, ORA)
            CATROBAT.value -> setFileDetails(Bitmap.CompressFormat.PNG, CATROBAT)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) = Unit
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        percentage.text = progress.toString().plus('%')
        compressQuality = progress
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
    override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
}
