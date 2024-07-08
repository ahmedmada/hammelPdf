package org.hammel.paintpdf.colorpicker

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Shader.TileMode
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageButton
import org.hammel.paintpdf.colorpicker.ColorPickerDialog.CustomColorDrawable.Companion.createDrawable
import paintpdf.colorpicker.R
import kotlin.jvm.JvmOverloads

class ColorPickerPresetColorButton @JvmOverloads constructor(
    context: Context,
    @field:ColorInt @get:ColorInt
    @param:ColorInt var color: Int = Color.BLACK
) : AppCompatImageButton(context, null, R.attr.borderlessButtonStyle) {

    init {
        val checkeredBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val bitmapShader = BitmapShader(checkeredBitmap, TileMode.REPEAT, TileMode.REPEAT)
        background = createDrawable(bitmapShader, color)
    }
}
