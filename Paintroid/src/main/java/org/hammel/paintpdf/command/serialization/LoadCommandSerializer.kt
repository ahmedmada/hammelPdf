package org.hammel.paintpdf.command.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.LoadCommand

class LoadCommandSerializer(version: Int) : VersionSerializer<LoadCommand>(version) {

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun write(kryo: Kryo, output: Output, command: LoadCommand) {
        command.loadedBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, output)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out LoadCommand>): LoadCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out LoadCommand>): LoadCommand {
        val bitmap = BitmapFactory.decodeStream(input)
        return LoadCommand(bitmap)
    }
}
