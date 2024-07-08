package org.hammel.paintpdf.iotasks

import android.graphics.Bitmap
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.colorpicker.ColorHistory
import org.hammel.paintpdf.model.CommandManagerModel

data class BitmapReturnValue(
    @JvmField
    var model: CommandManagerModel?,
    @JvmField
    var layerList: List<LayerContracts.Layer>?,
    @JvmField
    var bitmap: Bitmap?,
    @JvmField
    var toBeScaled: Boolean,
    @JvmField
    var colorHistory: ColorHistory?
) {
    constructor(bitmapList: List<LayerContracts.Layer>?, bitmap: Bitmap?, toBeScaled: Boolean) : this(
        null,
        bitmapList,
        bitmap,
        toBeScaled,
        null
    )

    constructor(model: CommandManagerModel?, colorHistory: ColorHistory?) : this(model, null, null, false, colorHistory)
}
