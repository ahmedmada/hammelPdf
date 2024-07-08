package org.hammel.paintpdf.ui.viewholder

import android.view.View
import org.hammel.paintpdf.contract.MainActivityContracts

class BottomBarViewHolder(val layout: View) : MainActivityContracts.BottomBarViewHolder {
    override val isVisible: Boolean
        get() = layout.visibility == View.VISIBLE

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    override fun hide() {
        layout.visibility = View.GONE
    }
}
