package org.hammel.paintpdf.contract

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.DisplayMetrics
import android.view.Menu
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import org.hammel.paintpdf.UserPreferences
import org.hammel.paintpdf.command.serialization.CommandSerializer
import org.hammel.paintpdf.colorpicker.ColorHistory
import org.hammel.paintpdf.common.MainActivityConstants.ActivityRequestCode
import org.hammel.paintpdf.dialog.PermissionInfoDialog.PermissionType
import org.hammel.paintpdf.iotasks.CreateFile.CreateFileCallback
import org.hammel.paintpdf.iotasks.LoadImage.LoadImageCallback
import org.hammel.paintpdf.iotasks.SaveImage.SaveImageCallback
import org.hammel.paintpdf.iotasks.WorkspaceReturnValue
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.ui.LayerAdapter
import java.io.File

interface MainActivityContracts {
    interface Navigator {
        val isSdkAboveOrEqualM: Boolean
        val isSdkAboveOrEqualQ: Boolean
        val isSdkAboveOrEqualT: Boolean

        fun showColorPickerDialog()

        fun startLoadImageActivity(@ActivityRequestCode requestCode: Int)

        fun startImportImageActivity(@ActivityRequestCode requestCode: Int)

        fun showAboutDialog()

        fun showLikeUsDialog()

        fun showRateUsDialog()

        fun showFeedbackDialog()

        fun showZoomWindowSettingsDialog(sharedPreferences: UserPreferences)

        fun showAdvancedSettingsDialog()

        fun showOverwriteDialog(permissionCode: Int, isExport: Boolean)

        fun showPngInformationDialog()

        fun showJpgInformationDialog()

        fun showOraInformationDialog()

        fun showCatrobatInformationDialog()

        fun sendFeedback()

        fun startWelcomeActivity(@ActivityRequestCode requestCode: Int)

        fun startShareImageActivity(bitmap: Bitmap?)

        fun showIndeterminateProgressDialog()

        fun dismissIndeterminateProgressDialog()

        fun returnToPocketCode(path: String?)

        fun showToast(msg: String, duration: Int)

        fun showToast(@StringRes resId: Int, duration: Int)

        fun showSaveErrorDialog()

        fun showLoadErrorDialog()

        fun showRequestPermissionRationaleDialog(
            permissionType: PermissionType,
            permissions: Array<String>,
            requestCode: Int
        )

        fun showRequestPermanentlyDeniedPermissionRationaleDialog()

        fun askForPermission(permissions: Array<String>, requestCode: Int)

        fun doIHavePermission(permission: String): Boolean

        fun isPermissionPermanentlyDenied(permissions: Array<String>): Boolean

        fun finishActivity()

        fun showSaveBeforeFinishDialog()

        fun showSaveBeforeNewImageDialog()

        fun showSaveBeforeLoadImageDialog()

        fun showSaveImageInformationDialogWhenStandalone(
            permissionCode: Int,
            imageNumber: Int,
            isExport: Boolean
        )

        fun restoreFragmentListeners()

        fun showToolChangeToast(offset: Int, idRes: Int)

        fun broadcastAddPictureToGallery(uri: Uri)

        fun rateUsClicked()

        fun showImageImportDialog()

        fun setAntialiasingOnToolPaint()

        fun showCatroidMediaGallery()

        fun showScaleImageRequestDialog(uri: Uri?, requestCode: Int)

        fun setMaskFilterToNull()
    }

    interface MainView {
        val finishing: Boolean
        val isKeyboardShown: Boolean
        val myContentResolver: ContentResolver
        val displayMetrics: DisplayMetrics
        val presenter: Presenter

        fun initializeActionBar(isOpenedFromCatroid: Boolean)

        fun superHandleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun superHandleRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        )

        fun getUriFromFile(file: File): Uri

        fun hideKeyboard()

        fun refreshDrawingSurface()

        fun enterHideButtons()

        fun exitHideButtons()

        fun showContentLoadingProgressBar()

        fun hideContentLoadingProgressBar()
        fun addPage()
        fun removePage()
    }

    interface Presenter {
        val imageNumber: Int
        val bitmap: Bitmap?
        val context: Context

        fun initializeFromCleanState(extraPicturePath: String?, extraPictureName: String?)

        fun restoreState(
            isFullscreen: Boolean,
            isSaved: Boolean,
            isOpenedFromCatroid: Boolean,
            isOpenedFromFormulaEditorInCatroid: Boolean,
            savedPictureUri: Uri?,
            cameraImageUri: Uri?
        )

        fun finishInitialize()

        fun removeMoreOptionsItems(menu: Menu?)

        fun replaceImageClicked()

        fun addImageToCurrentLayerClicked()

        fun loadNewImage()

        fun newImageClicked()

        fun discardImageClicked()

        fun saveCopyClicked(isExport: Boolean)

        fun saveImageClicked()

        fun shareImageClicked()

        fun enterHideButtonsClicked()

        fun exitHideButtonsClicked()

        fun backToPocketCodeClicked()

        fun showHelpClicked()

        fun showAboutClicked()

        fun showZoomWindowSettingsClicked(sharedPreferences: UserPreferences)

        fun showAdvancedSettingsClicked()

        fun showRateUsDialog()

        fun showFeedbackDialog()

        fun showOverwriteDialog(permissionCode: Int, isExport: Boolean)

        fun showPngInformationDialog()

        fun showJpgInformationDialog()

        fun showOraInformationDialog()

        fun showCatrobatInformationDialog()

        fun sendFeedback()

        fun onNewImage()

        fun switchBetweenVersions(requestCode: Int, isExport: Boolean)

        fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        fun handleRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        )

        fun onBackPressed()

        fun saveImageConfirmClicked(requestCode: Int, uri: Uri?)

        fun saveCopyConfirmClicked(requestCode: Int, uri: Uri?)

        fun undoClicked()

        fun redoClicked()
        fun managePages()
        fun removePage()
        fun removePages()

        fun showColorPickerClicked()

        fun showLayerMenuClicked()

        fun onCommandPostExecute()

        fun setBottomNavigationColor(color: Int)

        fun onCreateTool()

        fun toolClicked(toolType: ToolType)

        fun saveBeforeLoadImage()

        fun saveBeforeNewImage()

        fun saveBeforeFinish()

        fun finishActivity()

        fun actionToolsClicked()

        fun actionCurrentToolClicked()

        fun rateUsClicked()

        fun importFromGalleryClicked()

        fun showImportDialog()

        fun importStickersClicked()

        fun bitmapLoadedFromSource(loadedImage: Bitmap)

        fun setLayerAdapter(layerAdapter: LayerAdapter)

        fun loadScaledImage(uri: Uri?, @ActivityRequestCode requestCode: Int)

        fun setAntialiasingOnOkClicked()

        fun saveNewTemporaryImage()

        fun openTemporaryFile(): WorkspaceReturnValue?

        fun checkForTemporaryFile(): Boolean

        fun setColorHistoryAfterLoadImage(colorHistory: ColorHistory?)
    }

    interface Model {
        var cameraImageUri: Uri?
        var savedPictureUri: Uri?
        var isSaved: Boolean
        var isFullscreen: Boolean
        var isOpenedFromCatroid: Boolean
        var isOpenedFromFormulaEditorInCatroid: Boolean
        var colorHistory: ColorHistory

        fun wasInitialAnimationPlayed(): Boolean

        fun setInitialAnimationPlayed(wasInitialAnimationPlayed: Boolean)
    }

    interface Interactor {
        fun saveCopy(
            callback: SaveImageCallback,
            requestCode: Int,
            layerModel: LayerContracts.Model,
            commandSerializer: CommandSerializer,
            uri: Uri?,
            context: Context
        )

        fun createFile(callback: CreateFileCallback, requestCode: Int, filename: String)

        fun saveImage(
            callback: SaveImageCallback,
            requestCode: Int,
            layerModel: LayerContracts.Model,
            commandSerializer: CommandSerializer,
            uri: Uri?,
            context: Context
        )

        fun loadFile(
            callback: LoadImageCallback,
            requestCode: Int,
            uri: Uri?,
            context: Context,
            scaling: Boolean,
            commandSerializer: CommandSerializer,
        )
    }

    interface TopBarViewHolder {
        val height: Int

        fun enableUndoButton()

        fun disableUndoButton()

        fun enableRedoButton()

        fun disableRedoButton()

        fun hide()

        fun show()

        fun removeStandaloneMenuItems(menu: Menu?)

        fun removeCatroidMenuItems(menu: Menu?)

        fun hideTitleIfNotStandalone()
    }

    interface DrawerLayoutViewHolder {
        fun closeDrawer(gravity: Int, animate: Boolean)

        fun isDrawerOpen(gravity: Int): Boolean

        fun isDrawerVisible(gravity: Int): Boolean

        fun openDrawer(gravity: Int)
    }

    interface BottomBarViewHolder {
        val isVisible: Boolean

        fun show()

        fun hide()
    }

    interface BottomNavigationViewHolder {
        fun show()

        fun hide()

        fun showCurrentTool(toolType: ToolType?)

        fun enableColorItemView(show: Boolean)

        fun setColorButtonColor(@ColorInt color: Int)
    }

    interface BottomNavigationAppearance {
        fun showCurrentTool(toolType: ToolType)
    }
}
