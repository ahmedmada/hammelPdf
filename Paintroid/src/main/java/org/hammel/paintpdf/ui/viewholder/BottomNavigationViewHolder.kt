package org.hammel.paintpdf.ui.viewholder

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import paintpdf.R
import org.hammel.paintpdf.contract.MainActivityContracts
import org.hammel.paintpdf.contract.MainActivityContracts.BottomNavigationAppearance
import org.hammel.paintpdf.tools.ToolType
import org.hammel.paintpdf.ui.BottomNavigationLandscape
import org.hammel.paintpdf.ui.BottomNavigationPortrait

@SuppressLint("RestrictedApi")
class BottomNavigationViewHolder(
    private val layout: View,
    private val orientation: Int,
    context: Context
) :
    MainActivityContracts.BottomNavigationViewHolder {
    val bottomNavigationView: BottomNavigationView =
        layout.findViewById(R.id.pdfPaint_bottom_navigation)
    private val bottomNavigation: BottomNavigationAppearance
    private val colorButton: ImageView
    private val colorItemView: BottomNavigationItemView

    init {
        bottomNavigation = setAppearance(context)
        val bottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        colorItemView = bottomNavigationMenuView.getChildAt(2) as BottomNavigationItemView
        colorButton = colorItemView.findViewById(R.id.icon)
        initColorButton()
    }

    override fun show() {
        layout.visibility = View.VISIBLE
    }

    override fun hide() {
        layout.visibility = View.GONE
    }

    override fun showCurrentTool(toolType: ToolType?) {
        toolType?.let { bottomNavigation.showCurrentTool(it) }
    }

    override fun enableColorItemView(show: Boolean) {
        colorItemView.isClickable = show
    }

    override fun setColorButtonColor(color: Int) {
        colorButton.setColorFilter(color)
    }

    private fun initColorButton() {
        colorButton.apply {
            scaleType = ImageView.ScaleType.FIT_XY
            setBackgroundColor(Color.WHITE)
            setPadding(2, 2, 2, 2)
        }
    }

    private fun setAppearance(context: Context): BottomNavigationAppearance =
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            BottomNavigationPortrait(bottomNavigationView)
        } else {
            BottomNavigationLandscape(context, bottomNavigationView)
        }
}
