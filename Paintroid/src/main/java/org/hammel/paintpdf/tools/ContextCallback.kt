package org.hammel.paintpdf.tools

import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes

interface ContextCallback {
    val scrollTolerance: Int
    val orientation: ScreenOrientation?
    val displayMetrics: DisplayMetrics
    val checkeredBitmapShader: Shader?

    fun showNotification(@StringRes resId: Int)

    fun showNotificationWithDuration(@StringRes resId: Int, duration: NotificationDuration)

    fun getFont(@FontRes id: Int): Typeface?

    @ColorInt
    fun getColor(@ColorRes id: Int): Int

    fun getDrawable(@DrawableRes resource: Int): Drawable?

    enum class ScreenOrientation {
        PORTRAIT, LANDSCAPE
    }

    enum class NotificationDuration {
        SHORT, LONG
    }
}
