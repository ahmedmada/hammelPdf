package org.hammel.paintpdf.command.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.LoadLayerListCommand
import org.hammel.paintpdf.model.Layer

class LoadLayerListCommandSerializer(version: Int) :
    VersionSerializer<LoadLayerListCommand>(version) {

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun write(kryo: Kryo, output: Output, command: LoadLayerListCommand) {
        output.writeInt(command.loadedLayers.size)
        command.loadedLayers.forEach { layer ->
            layer.bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, output)
            output.writeInt(layer.opacityPercentage)
        }
    }

    override fun read(
        kryo: Kryo,
        input: Input,
        type: Class<out LoadLayerListCommand>
    ): LoadLayerListCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(
        kryo: Kryo,
        input: Input,
        type: Class<out LoadLayerListCommand>
    ): LoadLayerListCommand {
        val size = input.readInt()
        val layerList = ArrayList<Layer>()
        repeat(size) {
            val bitmap = BitmapFactory.decodeStream(input)
            val alpha = input.readInt()
            val layer = Layer(bitmap).apply {
                opacityPercentage = alpha
            }
            layerList.add(layer)
        }

        return LoadLayerListCommand(layerList)
    }
}
