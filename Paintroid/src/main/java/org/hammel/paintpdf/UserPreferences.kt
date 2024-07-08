package org.hammel.paintpdf

import android.content.SharedPreferences
import org.hammel.paintpdf.common.IMAGE_NUMBER_SHARED_PREFERENCES_TAG
import org.hammel.paintpdf.common.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG
import org.hammel.paintpdf.common.ZOOM_WINDOW_ENABLED_SHARED_PREFERENCES_TAG
import org.hammel.paintpdf.common.ZOOM_WINDOW_ZOOM_PERCENTAGE_SHARED_PREFERENCES_TAG

open class UserPreferences(var preferences: SharedPreferences) {

    open val preferenceLikeUsDialogValue: Boolean
        get() = preferences.getBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, false)
    open var preferenceImageNumber: Int
        get() = preferences.getInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, 0)
        set(value) {
            preferences
                .edit()
                .putInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, value)
                .apply()
        }
    open var preferenceZoomWindowEnabled: Boolean
        get() = preferences.getBoolean(ZOOM_WINDOW_ENABLED_SHARED_PREFERENCES_TAG, true)
        set(value) {
            preferences
                .edit()
                .putBoolean(ZOOM_WINDOW_ENABLED_SHARED_PREFERENCES_TAG, value)
                .apply()
        }

    open var preferenceZoomWindowZoomPercentage: Int
        get() = preferences.getInt(
            ZOOM_WINDOW_ZOOM_PERCENTAGE_SHARED_PREFERENCES_TAG,
            initialZoomPercent
        )
        set(value) {
            preferences
                .edit()
                .putInt(ZOOM_WINDOW_ZOOM_PERCENTAGE_SHARED_PREFERENCES_TAG, value)
                .apply()
        }

    open fun setPreferenceLikeUsDialogValue() {
        preferences
            .edit()
            .putBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, true)
            .apply()
    }

    companion object {
        const val initialZoomPercent: Int = 100
    }
}
