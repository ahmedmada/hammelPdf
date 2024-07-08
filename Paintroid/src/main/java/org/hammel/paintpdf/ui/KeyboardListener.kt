package org.hammel.paintpdf.ui

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View

private const val HEIGHT_THRESHOLD = 300f

class KeyboardListener(activityRootView: View) {
    var isSoftKeyboardVisible = false

    init {
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val heightDifference = activityRootView.rootView.height - activityRootView.height
            val displayMetrics = activityRootView.resources.displayMetrics
            isSoftKeyboardVisible = heightDifference > dpToPx(displayMetrics)
        }
    }

    private fun dpToPx(displayMetrics: DisplayMetrics, dpValue: Float = HEIGHT_THRESHOLD): Float =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            displayMetrics
        )
}
