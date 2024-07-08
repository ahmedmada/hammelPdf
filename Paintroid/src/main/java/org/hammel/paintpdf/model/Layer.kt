package org.hammel.paintpdf.model

import android.graphics.Bitmap
import org.hammel.paintpdf.contract.LayerContracts

const val MAX_LAYER_OPACITY_PERCENTAGE = 100
const val MAX_LAYER_OPACITY_VALUE = 255

open class Layer(override var bitmap: Bitmap) : LayerContracts.Layer {
    override var isVisible: Boolean = true
    override var opacityPercentage: Int = MAX_LAYER_OPACITY_PERCENTAGE

    override fun getValueForOpacityPercentage(): Int = (opacityPercentage.toFloat() / MAX_LAYER_OPACITY_PERCENTAGE * MAX_LAYER_OPACITY_VALUE).toInt()
}
