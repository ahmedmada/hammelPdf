package org.hammel.paintpdf.command.serialization

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.tools.implementation.DefaultToolPaint
import org.hammel.paintpdf.tools.implementation.WatercolorTool

class PaintSerializer(version: Int, private val activityContext: Context) : VersionSerializer<Paint>(version) {

    override fun write(kryo: Kryo, output: Output, paint: Paint) {
        with(output) {
            writeInt(paint.color)
            writeFloat(paint.strokeWidth)
            writeInt(paint.strokeCap.ordinal)
            writeBoolean(paint.isAntiAlias)
            writeInt(paint.style.ordinal)
            writeInt(paint.strokeJoin.ordinal)
            writeBoolean(paint.maskFilter != null)
            writeInt(paint.alpha)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Paint>): Paint =
        super.handleVersions(this, kryo, input, type)

    override fun readV1(serializer: VersionSerializer<Paint>, kryo: Kryo, input: Input, type: Class<out Paint>): Paint {
        val toolPaint = DefaultToolPaint(activityContext).apply {
            with(input) {
                color = readInt()
                strokeWidth = readFloat()
                strokeCap = Paint.Cap.values()[readInt()]
            }
        }

        return toolPaint.paint.apply {
            with(input) {
                isAntiAlias = readBoolean()
                style = Paint.Style.values()[readInt()]
                strokeJoin = Paint.Join.values()[readInt()]
            }
        }
    }

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Paint>): Paint {
        val toolPaint = DefaultToolPaint(activityContext).apply {
            with(input) {
                color = readInt()
                strokeWidth = readFloat()
                strokeCap = Paint.Cap.values()[readInt()]
            }
        }

        return toolPaint.paint.apply {
            with(input) {
                isAntiAlias = readBoolean()
                style = Paint.Style.values()[readInt()]
                strokeJoin = Paint.Join.values()[readInt()]
                val hadFilter: Boolean = input.readBoolean()
                alpha = input.readInt()
                if (hadFilter) maskFilter = BlurMaskFilter(WatercolorTool.calcRange(alpha), BlurMaskFilter.Blur.INNER)
            }
        }
    }
}
