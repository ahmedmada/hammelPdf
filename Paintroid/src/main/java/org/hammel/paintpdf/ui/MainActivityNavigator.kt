package org.hammel.paintpdf.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.catroidFlag
import org.hammel.paintpdf.FileIO.compressFormat
import org.hammel.paintpdf.FileIO.fileType
import org.hammel.paintpdf.FileIO.filename
import org.hammel.paintpdf.FileIO.parseFileName
import org.hammel.paintpdf.FileIO.saveBitmapToCache
import org.hammel.paintpdf.MainActivity
import org.hammel.paintpdf.UserPreferences
import org.hammel.paintpdf.WelcomeActivity
import org.hammel.paintpdf.colorpicker.ColorPickerDialog
import org.hammel.paintpdf.colorpicker.OnColorPickedListener
import org.hammel.paintpdf.command.CommandFactory
import org.hammel.paintpdf.command.implementation.DefaultCommandFactory
import org.hammel.paintpdf.common.ABOUT_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.ADVANCED_SETTINGS_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.CATROBAT_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.CATROID_MEDIA_GALLERY_FRAGMENT_TAG
import org.hammel.paintpdf.common.COLOR_PICKER_DIALOG_TAG
import org.hammel.paintpdf.common.FEEDBACK_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.INDETERMINATE_PROGRESS_DIALOG_TAG
import org.hammel.paintpdf.common.JPG_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.LIKE_US_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.LOAD_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.MainActivityConstants.ActivityRequestCode
import org.hammel.paintpdf.common.ORA_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.OVERWRITE_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.PAINTROID_PICTURE_PATH
import org.hammel.paintpdf.common.PERMISSION_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.PERMISSION_EXTERNAL_STORAGE_SAVE_COPY
import org.hammel.paintpdf.common.PNG_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.RATE_US_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.SAVE_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.common.SAVE_INFORMATION_DIALOG_TAG
import org.hammel.paintpdf.common.SAVE_QUESTION_FRAGMENT_TAG
import org.hammel.paintpdf.common.SCALE_IMAGE_FRAGMENT_TAG
import org.hammel.paintpdf.common.ZOOM_WINDOW_SETTINGS_DIALOG_FRAGMENT_TAG
import org.hammel.paintpdf.contract.MainActivityContracts
import org.hammel.paintpdf.dialog.AboutDialog
import org.hammel.paintpdf.dialog.AdvancedSettingsDialog
import org.hammel.paintpdf.dialog.CatrobatImageInfoDialog
import org.hammel.paintpdf.dialog.FeedbackDialog
import org.hammel.paintpdf.dialog.ImportImageDialog
import org.hammel.paintpdf.dialog.IndeterminateProgressDialog
import org.hammel.paintpdf.dialog.InfoDialog
import org.hammel.paintpdf.dialog.JpgInfoDialog
import org.hammel.paintpdf.dialog.LikeUsDialog
import org.hammel.paintpdf.dialog.OraInfoDialog
import org.hammel.paintpdf.dialog.OverwriteDialog
import org.hammel.paintpdf.dialog.PermanentDenialPermissionInfoDialog
import org.hammel.paintpdf.dialog.PermissionInfoDialog
import org.hammel.paintpdf.dialog.PermissionInfoDialog.PermissionType
import org.hammel.paintpdf.dialog.PngInfoDialog
import org.hammel.paintpdf.dialog.RateUsDialog
import org.hammel.paintpdf.dialog.SaveBeforeFinishDialog
import org.hammel.paintpdf.dialog.SaveBeforeLoadImageDialog
import org.hammel.paintpdf.dialog.SaveBeforeNewImageDialog
import org.hammel.paintpdf.dialog.SaveInformationDialog
import org.hammel.paintpdf.dialog.ScaleImageOnLoadDialog
import org.hammel.paintpdf.dialog.ZoomWindowSettingsDialog
import org.hammel.paintpdf.tools.ToolReference
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.ui.fragments.CatroidMediaGalleryFragment
import org.hammel.paintpdf.ui.fragments.CatroidMediaGalleryFragment.MediaGalleryListener
import paintpdf.R

class MainActivityNavigator(
    private val mainActivity: MainActivity,
    private val toolReference: ToolReference
) : MainActivityContracts.Navigator {
    override val isSdkAboveOrEqualM: Boolean
        @SuppressLint("AnnotateVersionCheck")
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    override val isSdkAboveOrEqualQ: Boolean
        @SuppressLint("AnnotateVersionCheck")
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    override val isSdkAboveOrEqualT: Boolean
        @SuppressLint("AnnotateVersionCheck")
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private var commandFactory: CommandFactory = DefaultCommandFactory()

    private fun showFragment(
        fragment: Fragment,
        tag: String = CATROID_MEDIA_GALLERY_FRAGMENT_TAG
    ) {
        val fragmentManager = mainActivity.supportFragmentManager
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_to_top,
                R.anim.slide_to_bottom,
                R.anim.slide_to_top,
                R.anim.slide_to_bottom
            )
            .addToBackStack(null)
            .add(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun showDialogFragmentSafely(dialog: DialogFragment, tag: String) {
        val fragmentManager = mainActivity.supportFragmentManager
        if (!fragmentManager.isStateSaved) {
            dialog.show(fragmentManager, tag)
        }
    }

    private fun findFragmentByTag(tag: String): Fragment? =
        mainActivity.supportFragmentManager.findFragmentByTag(tag)

    private fun setupColorPickerDialogListeners(dialog: ColorPickerDialog) {
        dialog.addOnColorPickedListener(object : OnColorPickedListener {
            override fun colorChanged(color: Int) {
                val command = commandFactory.createColorChangedCommand(
                    toolReference.tool,
                    mainActivity,
                    color
                )
                mainActivity.model.colorHistory.addColor(color)

                if (toolReference.tool?.toolType != ToolType.CLIP) {
                    mainActivity.commandManager.addCommand(command)
                } else {
                    mainActivity.commandManager.addCommandWithoutUndo(command)
                }
            }
        })

        mainActivity.presenter.bitmap?.let { dialog.setBitmap(it) }
    }

    private fun setupCatroidMediaGalleryListeners(dialog: CatroidMediaGalleryFragment) {
        dialog.setMediaGalleryListener(object : MediaGalleryListener {
            override fun bitmapLoadedFromSource(loadedBitmap: Bitmap) {
                mainActivity.presenter.bitmapLoadedFromSource(loadedBitmap)
            }

            override fun showProgressDialog() {
                showIndeterminateProgressDialog()
            }

            override fun dismissProgressDialog() {
                dismissIndeterminateProgressDialog()
            }
        })
    }

    @SuppressWarnings("SwallowedException")
    private fun openPlayStore(applicationId: String) {
        val uriPlayStore = Uri.parse("market://details?id=$applicationId")
        val openPlayStore = Intent(Intent.ACTION_VIEW, uriPlayStore)
        try {
            mainActivity.startActivity(openPlayStore)
        } catch (e: ActivityNotFoundException) {
            val uriNoPlayStore =
                Uri.parse("http://play.google.com/store/apps/details?id=$applicationId")
            val noPlayStoreInstalled = Intent(Intent.ACTION_VIEW, uriNoPlayStore)

            runCatching {
                mainActivity.startActivity(noPlayStoreInstalled)
            }
        }
    }

    private fun getFileName(uri: Uri?): String? {
        uri ?: return null
        var result: String? = null
        if (uri.scheme == "content") {
            val queryCursor = mainActivity.contentResolver.query(uri, null, null, null, null)
            queryCursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result =
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }

    override fun showColorPickerDialog() {
        if (findFragmentByTag(COLOR_PICKER_DIALOG_TAG) == null) {
            toolReference.tool?.let {
                val dialog = ColorPickerDialog.newInstance(
                    it.drawPaint.color,
                    mainActivity.model.isOpenedFromCatroid,
                    mainActivity.model.isOpenedFromFormulaEditorInCatroid,
                    mainActivity.model.colorHistory
                )
                setupColorPickerDialogListeners(dialog)
                showDialogFragmentSafely(dialog, COLOR_PICKER_DIALOG_TAG)
            }
        }
    }

    override fun showCatroidMediaGallery() {
        if (findFragmentByTag(CATROID_MEDIA_GALLERY_FRAGMENT_TAG) == null) {
            val fragment = CatroidMediaGalleryFragment()
            fragment.setMediaGalleryListener(object : MediaGalleryListener {
                override fun bitmapLoadedFromSource(loadedBitmap: Bitmap) {
                    mainActivity.presenter.bitmapLoadedFromSource(loadedBitmap)
                }

                override fun showProgressDialog() {
                    showIndeterminateProgressDialog()
                }

                override fun dismissProgressDialog() {
                    dismissIndeterminateProgressDialog()
                }
            })
            showFragment(fragment)
        }
    }

    override fun startLoadImageActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        }
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startImportImageActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            flags = Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        }
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startWelcomeActivity(@ActivityRequestCode requestCode: Int) {
        val intent = Intent(mainActivity.applicationContext, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        mainActivity.startActivityForResult(intent, requestCode)
    }

    override fun startShareImageActivity(bitmap: Bitmap?) {
        val uri = saveBitmapToCache(
            bitmap,
            mainActivity,
            "image"
        ) ?: return
        val shareIntent = Intent().apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            setDataAndType(uri, mainActivity.contentResolver.getType(uri))
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            action = Intent.ACTION_SEND
        }
        val chooserTitle = mainActivity.resources.getString(R.string.share_image_via_text)
        mainActivity.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    }

    override fun showAboutDialog() {
        val about = AboutDialog()
        about.show(mainActivity.supportFragmentManager, ABOUT_DIALOG_FRAGMENT_TAG)
    }

    override fun showLikeUsDialog() {
        val likeUsDialog = LikeUsDialog()
        likeUsDialog.show(
            mainActivity.supportFragmentManager,
            LIKE_US_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showRateUsDialog() {
        val rateUsDialog = RateUsDialog()
        rateUsDialog.show(
            mainActivity.supportFragmentManager,
            RATE_US_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showFeedbackDialog() {
        val feedbackDialog = FeedbackDialog()
        feedbackDialog.show(
            mainActivity.supportFragmentManager,
            FEEDBACK_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showZoomWindowSettingsDialog(sharedPreferences: UserPreferences) {
        val zoomWindowSettingsDialog = ZoomWindowSettingsDialog(sharedPreferences)
        zoomWindowSettingsDialog.show(
            mainActivity.supportFragmentManager,
            ZOOM_WINDOW_SETTINGS_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showAdvancedSettingsDialog() {
        val advancedSettingsDialog = AdvancedSettingsDialog()
        advancedSettingsDialog.show(
            mainActivity.supportFragmentManager,
            ADVANCED_SETTINGS_DIALOG_FRAGMENT_TAG
        )
    }

    override fun showOverwriteDialog(permissionCode: Int, isExport: Boolean) {
//        mainActivity.idlingResource.increment()
        val overwriteDialog = OverwriteDialog.newInstance(permissionCode, isExport)
        overwriteDialog.show(
            mainActivity.supportFragmentManager,
            OVERWRITE_INFORMATION_DIALOG_TAG
        )
//        mainActivity.idlingResource.decrement()
    }

    override fun showPngInformationDialog() {
        val pngInfoDialog = PngInfoDialog()
        pngInfoDialog.show(
            mainActivity.supportFragmentManager,
            PNG_INFORMATION_DIALOG_TAG
        )
    }

    override fun showJpgInformationDialog() {
        val jpgInfoDialog = JpgInfoDialog()
        jpgInfoDialog.show(
            mainActivity.supportFragmentManager,
            JPG_INFORMATION_DIALOG_TAG
        )
    }

    override fun showOraInformationDialog() {
        val oraInfoDialog = OraInfoDialog()
        oraInfoDialog.show(
            mainActivity.supportFragmentManager,
            ORA_INFORMATION_DIALOG_TAG
        )
    }

    override fun showCatrobatInformationDialog() {
        val catrobatInfoDialog = CatrobatImageInfoDialog()
        catrobatInfoDialog.show(
            mainActivity.supportFragmentManager,
            CATROBAT_INFORMATION_DIALOG_TAG
        )
    }

    override fun sendFeedback() {
        val intent = Intent(Intent.ACTION_SENDTO)
        val data = Uri.parse("mailto:support-pdfPaint@catrobat.org")
        intent.data = data
        mainActivity.startActivity(intent)
    }

    override fun showImageImportDialog() {
        val importImage = ImportImageDialog()
        importImage.show(mainActivity.supportFragmentManager, ABOUT_DIALOG_FRAGMENT_TAG)
    }

    override fun showIndeterminateProgressDialog() {
        val progressDialogFragment = IndeterminateProgressDialog()
        showDialogFragmentSafely(progressDialogFragment, INDETERMINATE_PROGRESS_DIALOG_TAG)
    }

    override fun dismissIndeterminateProgressDialog() {
        val progressDialogFragment =
            findFragmentByTag(INDETERMINATE_PROGRESS_DIALOG_TAG) as DialogFragment?
        progressDialogFragment?.dismiss()
    }

    override fun returnToPocketCode(path: String?) {
        val resultIntent = Intent()
        resultIntent.putExtra(PAINTROID_PICTURE_PATH, path)
        mainActivity.setResult(Activity.RESULT_OK, resultIntent)
        mainActivity.finish()
    }

    override fun showToast(resId: Int, duration: Int) {
        ToastFactory.makeText(mainActivity, resId, duration).show()
    }

    override fun showToast(msg: String, duration: Int) {
        ToastFactory.makeText(mainActivity, msg, duration).show()
    }

    override fun showSaveErrorDialog() {
        val dialog: AppCompatDialogFragment = InfoDialog.newInstance(
            InfoDialog.DialogType.WARNING,
            R.string.dialog_error_sdcard_text, R.string.dialog_error_save_title
        )
        showDialogFragmentSafely(dialog, SAVE_DIALOG_FRAGMENT_TAG)
    }

    override fun showLoadErrorDialog() {
        val dialog: AppCompatDialogFragment = InfoDialog.newInstance(
            InfoDialog.DialogType.WARNING,
            R.string.dialog_loading_image_failed_title, R.string.dialog_loading_image_failed_text
        )
        showDialogFragmentSafely(dialog, LOAD_DIALOG_FRAGMENT_TAG)
    }

    override fun showRequestPermissionRationaleDialog(
        permissionType: PermissionType,
        permissions: Array<String>,
        requestCode: Int
    ) {
        val dialog: AppCompatDialogFragment =
            PermissionInfoDialog.newInstance(permissionType, permissions, requestCode)
        showDialogFragmentSafely(dialog, PERMISSION_DIALOG_FRAGMENT_TAG)
    }

    override fun showRequestPermanentlyDeniedPermissionRationaleDialog() {
        val dialog: AppCompatDialogFragment = PermanentDenialPermissionInfoDialog.newInstance(
            mainActivity.packageName
        )
        showDialogFragmentSafely(dialog, PERMISSION_DIALOG_FRAGMENT_TAG)
    }

    override fun askForPermission(permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(mainActivity, permissions, requestCode)
    }

    override fun doIHavePermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            mainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun isPermissionPermanentlyDenied(permissions: Array<String>): Boolean =
        !ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, permissions[0])

    override fun finishActivity() {
        mainActivity.finish()
    }

    override fun showSaveBeforeFinishDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeFinishDialog()
        showDialogFragmentSafely(dialog, SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showSaveBeforeNewImageDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeNewImageDialog()
        showDialogFragmentSafely(dialog, SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showSaveBeforeLoadImageDialog() {
        val dialog: AppCompatDialogFragment = SaveBeforeLoadImageDialog()
        showDialogFragmentSafely(dialog, SAVE_QUESTION_FRAGMENT_TAG)
    }

    override fun showScaleImageRequestDialog(uri: Uri?, requestCode: Int) {
        uri ?: return
        val dialog: AppCompatDialogFragment = ScaleImageOnLoadDialog.newInstance(uri, requestCode)
        showDialogFragmentSafely(dialog, SCALE_IMAGE_FRAGMENT_TAG)
    }

    @SuppressLint("VisibleForTests")
    override fun showSaveImageInformationDialogWhenStandalone(
        permissionCode: Int,
        imageNumber: Int,
        isExport: Boolean
    ) {
        val uri = mainActivity.model.savedPictureUri
        if (uri != null && permissionCode != PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
            parseFileName(
                uri,
                mainActivity.contentResolver
            )
        }
        if (!isExport && mainActivity.model.isOpenedFromCatroid) {
            val name = getFileName(uri)
            if (name != null && (name.endsWith(FileIO.FileType.JPG.value) || name.endsWith(
                    "jpeg"
                ))
            ) {
                compressFormat =
                    Bitmap.CompressFormat.JPEG
                fileType =
                    FileIO.FileType.JPG
            } else {
                compressFormat =
                    Bitmap.CompressFormat.PNG
                fileType =
                    FileIO.FileType.PNG
            }
            filename = "image$imageNumber"
            catroidFlag = true
            mainActivity.presenter.switchBetweenVersions(permissionCode, isExport)
            return
        }
        var isStandard = false
        if (permissionCode == PERMISSION_EXTERNAL_STORAGE_SAVE_COPY) {
            isStandard = true
        }
        val saveInfoDialog =
            SaveInformationDialog.newInstance(permissionCode, imageNumber, isStandard, isExport)
        saveInfoDialog.show(
            mainActivity.supportFragmentManager,
            SAVE_INFORMATION_DIALOG_TAG
        )
    }

    override fun showToolChangeToast(offset: Int, idRes: Int) {
        var offset = offset
        val toolNameToast = ToastFactory.makeText(mainActivity, idRes, Toast.LENGTH_SHORT)
        if (mainActivity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            offset = 0
        }
        toolNameToast.show()
    }

    override fun broadcastAddPictureToGallery(uri: Uri) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = uri
        mainActivity.sendBroadcast(mediaScanIntent)
    }

    override fun restoreFragmentListeners() {
        var fragment = findFragmentByTag(COLOR_PICKER_DIALOG_TAG)
        if (fragment != null) {
            setupColorPickerDialogListeners(fragment as ColorPickerDialog)
        }
        fragment = findFragmentByTag(CATROID_MEDIA_GALLERY_FRAGMENT_TAG)
        if (fragment != null) {
            setupCatroidMediaGalleryListeners(fragment as CatroidMediaGalleryFragment)
        }
    }

    override fun rateUsClicked() {
        openPlayStore(mainActivity.packageName)
    }

    override fun setAntialiasingOnToolPaint() {
        mainActivity.toolPaint.setAntialiasing()
    }

    override fun setMaskFilterToNull() {
        mainActivity.toolPaint.paint.maskFilter = null
        mainActivity.toolPaint.previewPaint.maskFilter = null
        toolReference.tool?.let {
            mainActivity.toolPaint.paint.alpha = it.drawPaint.alpha
            mainActivity.toolPaint.previewPaint.alpha = it.drawPaint.alpha
        }
    }
}
