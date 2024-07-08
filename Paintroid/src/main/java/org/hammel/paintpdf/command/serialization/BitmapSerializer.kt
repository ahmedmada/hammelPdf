package org.hammel.paintpdf.command.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

const val BITMAP_SERIALIZATION_COMPRESSION_QUALITY = 100

class BitmapSerializer(version: Int) : VersionSerializer<Bitmap>(version) {
    override fun write(kryo: Kryo, output: Output, bitmap: Bitmap) {
        bitmap.compress(Bitmap.CompressFormat.PNG, BITMAP_SERIALIZATION_COMPRESSION_QUALITY, output)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Bitmap>): Bitmap =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Bitmap>): Bitmap =
        BitmapFactory.decodeStream(input)
}
