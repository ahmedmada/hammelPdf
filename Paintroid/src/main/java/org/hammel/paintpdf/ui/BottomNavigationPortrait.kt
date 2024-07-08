package org.hammel.paintpdf.ui

import com.google.android.material.bottomnavigation.BottomNavigationView
import paintpdf.R
import org.hammel.paintpdf.contract.MainActivityContracts.BottomNavigationAppearance
import org.hammel.paintpdf.tools.ToolType

class BottomNavigationPortrait(private val bottomNavigationView: BottomNavigationView) : BottomNavigationAppearance {
    override fun showCurrentTool(toolType: ToolType) {
        bottomNavigationView.menu.findItem(R.id.action_current_tool).apply {
            if (!this.toString().equals(toolType.name, ignoreCase = true)) {
                setIcon(toolType.drawableResource)
                setTitle(toolType.nameResource)
            }
        }
    }
}
