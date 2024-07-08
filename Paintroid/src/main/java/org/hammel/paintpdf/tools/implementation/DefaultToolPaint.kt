package org.hammel.paintpdf.tools.implementation

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Cap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import paintpdf.R
import org.hammel.paintpdf.tools.ToolPaint

const val STROKE_25 = 25f
const val STROKE_10 = 10f

class DefaultToolPaint(private val context: Context) : ToolPaint {
    private val bitmapPaint = Paint().apply {
        reset()
        isAntiAlias = antialiasing
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Cap.ROUND
        strokeWidth = STROKE_25
    }

    override val checkeredShader: Shader?
        get() {
            val checkerboard =
                BitmapFactory.decodeResource(context.resources, R.drawable.pocketpaint_checkeredbg)
            if (checkerboard != null) {
                return BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            }
            return null
        }

    override val previewPaint: Paint
        get() = Paint().apply { set(bitmapPaint) }

    override val previewColor: Int
        get() = previewPaint.color

    override var color: Int
        get() = bitmapPaint.color
        set(color) {
            bitmapPaint.color = color
            previewPaint.set(bitmapPaint)
            previewPaint.xfermode = null
            if (Color.alpha(color) == 0) {
                previewPaint.shader = checkeredShader
                previewPaint.color = Color.BLACK
                bitmapPaint.xfermode = eraseXfermode
                bitmapPaint.alpha = 0
            } else {
                bitmapPaint.xfermode = null
            }
        }

    override var strokeWidth: Float
        get() = bitmapPaint.strokeWidth
        set(strokeWidth) {
            bitmapPaint.strokeWidth = strokeWidth
            previewPaint.strokeWidth = strokeWidth
            var antiAliasing = antialiasing
            if (strokeWidth <= 1) {
                antiAliasing = false
            }
            bitmapPaint.isAntiAlias = antiAliasing
            previewPaint.isAntiAlias = antiAliasing
        }

    override var strokeCap: Cap
        get() = bitmapPaint.strokeCap
        set(strokeCap) {
            bitmapPaint.strokeCap = strokeCap
            previewPaint.strokeCap = strokeCap
        }

    override var paint: Paint
        get() = bitmapPaint
        set(paint) {
            bitmapPaint.set(paint)
            previewPaint.set(paint)
        }

    override val eraseXfermode: PorterDuffXfermode
        get() = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    companion object {
        var antialiasing = true
        fun arePaintEquals(paint1: Paint, paint2: Paint): Boolean =
            paint1.color == paint2.color && paint1.strokeCap == paint2.strokeCap && paint1.isAntiAlias == paint2.isAntiAlias && paint1.strokeJoin == paint2.strokeJoin && paint1.style == paint2.style
    }

    override fun setAntialiasing() {
        bitmapPaint.isAntiAlias = antialiasing
        previewPaint.isAntiAlias = antialiasing
    }
}
