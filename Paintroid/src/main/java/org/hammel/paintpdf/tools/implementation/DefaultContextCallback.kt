package org.hammel.paintpdf.tools.implementation

import android.content.Context
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Shader
import android.graphics.Shader.TileMode.REPEAT
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import paintpdf.R
import org.hammel.paintpdf.tools.ContextCallback
import org.hammel.paintpdf.tools.ContextCallback.NotificationDuration
import org.hammel.paintpdf.tools.ContextCallback.NotificationDuration.SHORT
import org.hammel.paintpdf.tools.ContextCallback.ScreenOrientation
import org.hammel.paintpdf.tools.ContextCallback.ScreenOrientation.LANDSCAPE
import org.hammel.paintpdf.tools.ContextCallback.ScreenOrientation.PORTRAIT
import org.hammel.paintpdf.tools.common.SCROLL_TOLERANCE_PERCENTAGE
import org.hammel.paintpdf.ui.ToastFactory

class DefaultContextCallback(private val context: Context) : ContextCallback {
    override val checkeredBitmapShader: Shader?

    init {
        val checkerboard =
            BitmapFactory.decodeResource(context.resources, R.drawable.pocketpaint_checkeredbg)
        checkeredBitmapShader = BitmapShader(checkerboard, REPEAT, REPEAT)
    }

    override fun showNotification(@StringRes resId: Int) {
        showNotificationWithDuration(resId, SHORT)
    }

    override fun showNotificationWithDuration(
        @StringRes resId: Int,
        duration: NotificationDuration
    ) {
        val toastDuration = if (duration == SHORT) LENGTH_SHORT else LENGTH_LONG
        ToastFactory.makeText(context, resId, toastDuration).show()
    }

    override val scrollTolerance: Int =
        (context.resources.displayMetrics.widthPixels * SCROLL_TOLERANCE_PERCENTAGE).toInt()

    override val orientation: ScreenOrientation
        get() {
            val orientation = context.resources.configuration.orientation
            return if (orientation == ORIENTATION_LANDSCAPE) LANDSCAPE else PORTRAIT
        }

    override fun getFont(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(context, id)

    override val displayMetrics: DisplayMetrics = context.resources.displayMetrics

    @ColorInt
    override fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)

    override fun getDrawable(@DrawableRes resource: Int): Drawable? =
        AppCompatResources.getDrawable(context, resource)
}
