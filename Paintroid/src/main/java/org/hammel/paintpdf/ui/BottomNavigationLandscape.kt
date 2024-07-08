package org.hammel.paintpdf.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import paintpdf.R
import org.hammel.paintpdf.contract.MainActivityContracts.BottomNavigationAppearance
import org.hammel.paintpdf.tools.ToolType

class BottomNavigationLandscape(context: Context, private val bottomNavigationView: BottomNavigationView) : BottomNavigationAppearance {
    @SuppressLint("RestrictedApi")
    private val bottomNavigationMenuView: BottomNavigationMenuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView

    init {
        setAppearance(context)
    }

    override fun showCurrentTool(toolType: ToolType) {
        val item = bottomNavigationMenuView.getChildAt(1)
        val icon = item.findViewById<ImageView>(R.id.icon)
        val title = item.findViewById<TextView>(R.id.title)
        icon.setImageResource(toolType.drawableResource)
        title.setText(toolType.nameResource)
    }

    @SuppressLint("RestrictedApi")
    private fun setAppearance(context: Context) {
        val inflater = LayoutInflater.from(context)
        val menu = bottomNavigationView.menu
        for (i in 0 until menu.size()) {
            val item = bottomNavigationMenuView.getChildAt(i) as BottomNavigationItemView
            val itemBottomNavigation = inflater.inflate(R.layout.pocketpaint_layout_bottom_navigation_item, bottomNavigationMenuView, false)
            val icon = itemBottomNavigation.findViewById<ImageView>(R.id.icon)
            val text = itemBottomNavigation.findViewById<TextView>(R.id.title)
            icon.setImageDrawable(menu.getItem(i).icon)
            icon.setColorFilter(ContextCompat.getColor(context, R.color.pdfPaint_welcome_dot_active))
            text.text = menu.getItem(i).title
            item.removeAllViews()
            item.addView(itemBottomNavigation)
        }
    }
}
