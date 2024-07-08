package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.CropCommand

class CropCommandSerializer(version: Int) : VersionSerializer<CropCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: CropCommand) {
        with(output) {
            writeInt(command.resizeCoordinateXLeft)
            writeInt(command.resizeCoordinateYTop)
            writeInt(command.resizeCoordinateXRight)
            writeInt(command.resizeCoordinateYBottom)
            writeInt(command.maximumBitmapResolution)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out CropCommand>): CropCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out CropCommand>): CropCommand {
        return with(input) {
            val coordinateXLeft = readInt()
            val coordinateYTop = readInt()
            val coordinateXRight = readInt()
            val coordinateYBottom = readInt()
            val maxResolution = readInt()
            CropCommand(coordinateXLeft, coordinateYTop, coordinateXRight, coordinateYBottom, maxResolution)
        }
    }
}
