package org.hammel.paintpdf.command.serialization

import android.content.Context
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.TextToolCommand
import org.hammel.paintpdf.tools.FontType
import paintpdf.R

class TextToolCommandSerializer(version: Int, private val activityContext: Context) : VersionSerializer<TextToolCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: TextToolCommand) {
        with(kryo) {
            with(output) {
                writeObject(output, command.multilineText)
                writeObject(output, command.textPaint)
                writeFloat(command.boxOffset)
                writeFloat(command.boxWidth)
                writeFloat(command.boxHeight)
                writeObject(output, command.toolPosition)
                writeFloat(command.rotationAngle)
                writeObject(output, command.typeFaceInfo)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out TextToolCommand>): TextToolCommand =
        super.handleVersions(this, kryo, input, type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out TextToolCommand>): TextToolCommand {
        return with(input) {
            val text = kryo.readObject(input, Array<String>::class.java)
            val paint = kryo.readObject(input, Paint::class.java)
            val offset = readFloat()
            val width = readFloat()
            val height = readFloat()
            val position = kryo.readObject(input, PointF::class.java)
            val rotation = readFloat()
            val typeFaceInfo = kryo.readObject(input, SerializableTypeface::class.java)

            paint.apply {
                isFakeBoldText = typeFaceInfo.bold
                isUnderlineText = typeFaceInfo.underline
                textSize = typeFaceInfo.textSize
                textSkewX = typeFaceInfo.textSkewX
                val style = if (typeFaceInfo.italic) Typeface.ITALIC else Typeface.NORMAL
                typeface = try {
                    when (typeFaceInfo.font) {
                        FontType.SANS_SERIF -> Typeface.create(Typeface.SANS_SERIF, style)
                        FontType.SERIF -> Typeface.create(Typeface.SERIF, style)
                        FontType.MONOSPACE -> Typeface.create(Typeface.MONOSPACE, style)
                        FontType.STC -> ResourcesCompat.getFont(activityContext, R.font.stc_regular)
                        FontType.DUBAI -> ResourcesCompat.getFont(activityContext, R.font.dubai)
                    }
                } catch (e: Exception) {
                    Log.e("LoadImageAsync", "Typeface not supported on this mobile phone")
                    Typeface.create(Typeface.SANS_SERIF, style)
                }
            }
            TextToolCommand(text, paint, offset, width, height, position, rotation, typeFaceInfo)
        }
    }
}
