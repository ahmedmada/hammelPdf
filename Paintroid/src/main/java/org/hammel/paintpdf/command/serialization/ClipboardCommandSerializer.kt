package org.hammel.paintpdf.command.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.FileIO
import org.hammel.paintpdf.FileIO.getBitmapFromFile
import org.hammel.paintpdf.command.implementation.ClipboardCommand

class ClipboardCommandSerializer(version: Int) : VersionSerializer<ClipboardCommand>(version) {

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun write(kryo: Kryo, output: Output, command: ClipboardCommand) {
        with(kryo) {
            with(output) {
                var bitmap = command.fileToStoredBitmap?.let { file ->
                    getBitmapFromFile(file)
                }
                bitmap = bitmap ?: command.bitmap ?: throw KryoException()
                bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, output)
                writeObject(output, command.coordinates)
                writeFloat(command.boxWidth)
                writeFloat(command.boxHeight)
                writeFloat(command.boxRotation)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ClipboardCommand>): ClipboardCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out ClipboardCommand>): ClipboardCommand {
        return with(kryo) {
            with(input) {
                val bitmap = BitmapFactory.decodeStream(input)
                val coordinates = readObject(input, Point::class.java)
                val width = readFloat()
                val height = readFloat()
                val rotation = readFloat()
                ClipboardCommand(bitmap, coordinates, width, height, rotation)
            }
        }
    }
}
