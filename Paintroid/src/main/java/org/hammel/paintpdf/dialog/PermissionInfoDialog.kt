package org.hammel.paintpdf.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import paintpdf.R

private const val PERMISSION_TYPE_KEY = "permissionTypeKey"
private const val PERMISSIONS_KEY = "permissionsKey"
private const val REQUEST_CODE_KEY = "requestCodeKey"

class PermissionInfoDialog : AppCompatDialogFragment() {
    private var requestCode = 0
    private lateinit var permissions: Array<String>
    private lateinit var permissionType: PermissionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().apply {
            requestCode = getInt(REQUEST_CODE_KEY)
            permissions = getStringArray(PERMISSIONS_KEY) as Array<String>
            permissionType = getSerializable(PERMISSION_TYPE_KEY) as PermissionType
        }
    }

    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?):
        Dialog = AlertDialog.Builder(requireContext(), R.style.PdfPaintAlertDialog)
            .setIcon(permissionType.iconResource)
            .setMessage(permissionType.messageResource)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissions, requestCode
                )
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create()

    enum class PermissionType(@get:DrawableRes @param:DrawableRes val iconResource: Int, @get:StringRes @param:StringRes val messageResource: Int) {
        EXTERNAL_STORAGE(R.drawable.ic_pocketpaint_dialog_info, R.string.permission_info_external_storage_text);
    }

    companion object {
        @JvmStatic
        fun newInstance(permissionType: PermissionType, permissions: Array<String>, requestCode: Int):
            PermissionInfoDialog = PermissionInfoDialog().apply {
                arguments = bundleOf(
                    PERMISSION_TYPE_KEY to permissionType,
                    PERMISSIONS_KEY to permissions,
                    REQUEST_CODE_KEY to requestCode
                )
            }
    }
}
