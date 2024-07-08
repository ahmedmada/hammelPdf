package org.hammel.paintpdf.ui.viewholder

import android.view.View
import com.google.android.material.navigation.NavigationView
import paintpdf.R
import org.hammel.paintpdf.contract.LayerContracts

class LayerMenuViewHolder(private val layerLayout: NavigationView) : LayerContracts.LayerMenuViewHolder {
    val layerAddButton: View = layerLayout.findViewById(R.id.pdfPaint_layer_side_nav_button_add)
    val layerDeleteButton: View = layerLayout.findViewById(R.id.pdfPaint_layer_side_nav_button_delete)
    val layerVisibilityButton: View = layerLayout.findViewById(R.id.pdfPaint_layer_side_nav_button_visibility)
    val layerOpacityButton: View = layerLayout.findViewById(R.id.pdfPaint_layer_side_nav_button_opacity)

    override fun isShown(): Boolean = layerLayout.isShown

    override fun disableAddLayerButton() {
        layerAddButton.isEnabled = false
    }

    override fun enableAddLayerButton() {
        layerAddButton.isEnabled = true
    }

    override fun disableRemoveLayerButton() {
        layerDeleteButton.isEnabled = false
    }

    override fun enableRemoveLayerButton() {
        layerDeleteButton.isEnabled = true
    }

    override fun disableLayerVisibilityButton() {
        layerVisibilityButton.isEnabled = false
    }

    override fun disableLayerOpacityButton() {
        layerOpacityButton.isEnabled = false
    }
}
