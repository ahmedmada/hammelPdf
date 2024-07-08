package org.hammel.paintpdf.tools.options

import android.view.View
import android.view.ViewGroup

interface ToolOptionsViewController : ToolOptionsVisibilityController {
    val toolSpecificOptionsLayout: ViewGroup

    fun disable()

    fun enable()

    fun disableHide()

    fun enableHide()

    fun resetToOrigin()

    fun removeToolViews()

    fun showCheckmark()

    fun hideCheckmark()

    fun slideUp(view: View, willHide: Boolean, showOptionsView: Boolean, setViewGone: Boolean = false)

    fun slideDown(view: View, willHide: Boolean, showOptionsView: Boolean, setViewGone: Boolean = false)

    fun animateBottomAndTopNavigation(hide: Boolean)
}
