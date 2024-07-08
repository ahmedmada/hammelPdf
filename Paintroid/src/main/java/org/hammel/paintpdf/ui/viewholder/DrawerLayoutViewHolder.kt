package org.hammel.paintpdf.ui.viewholder

import androidx.drawerlayout.widget.DrawerLayout
import org.hammel.paintpdf.contract.MainActivityContracts

class DrawerLayoutViewHolder(private val drawerLayout: DrawerLayout) : MainActivityContracts.DrawerLayoutViewHolder {
    override fun closeDrawer(gravity: Int, animate: Boolean) {
        drawerLayout.closeDrawer(gravity, animate)
    }

    override fun isDrawerOpen(gravity: Int): Boolean = drawerLayout.isDrawerOpen(gravity)

    override fun isDrawerVisible(gravity: Int): Boolean = drawerLayout.isDrawerVisible(gravity)

    override fun openDrawer(gravity: Int) {
        drawerLayout.openDrawer(gravity)
    }
}
