package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.tools.FontType

data class SerializableTypeface(val font: FontType, val bold: Boolean, val underline: Boolean, val italic: Boolean, val textSize: Float, val textSkewX: Float) {

    class TypefaceSerializer(version: Int) : VersionSerializer<SerializableTypeface>(version) {
        override fun write(kryo: Kryo, output: Output, typeface: SerializableTypeface) {
            with(output) {
                writeString(typeface.font.name)
                writeBoolean(typeface.bold)
                writeBoolean(typeface.underline)
                writeBoolean(typeface.italic)
                writeFloat(typeface.textSize)
                writeFloat(typeface.textSkewX)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out SerializableTypeface>): SerializableTypeface =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SerializableTypeface>): SerializableTypeface {
            return with(input) {
                SerializableTypeface(FontType.valueOf(readString()), readBoolean(), readBoolean(), readBoolean(), readFloat(), readFloat())
            }
        }
    }
}
