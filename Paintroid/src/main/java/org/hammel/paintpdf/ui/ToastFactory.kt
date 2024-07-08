package org.hammel.paintpdf.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

@SuppressLint("ShowToast")
object ToastFactory {
    private lateinit var currentToast: Toast

    private fun cancelToast() {
        if (this::currentToast.isInitialized) {
            currentToast.cancel()
        }
    }

    @JvmStatic
    fun makeText(context: Context, @StringRes resId: Int, duration: Int): Toast {
        cancelToast()
        currentToast = Toast.makeText(context, resId, duration)
        return currentToast
    }

    @JvmStatic
    fun makeText(context: Context, msg: String, duration: Int): Toast {
        cancelToast()
        currentToast = Toast.makeText(context, msg, duration)
        return currentToast
    }
}
