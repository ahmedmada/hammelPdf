package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.SetDimensionCommand

class SetDimensionCommandSerializer(version: Int) : VersionSerializer<SetDimensionCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: SetDimensionCommand) {
        with(output) {
            writeInt(command.width)
            writeInt(command.height)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out SetDimensionCommand>): SetDimensionCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SetDimensionCommand>): SetDimensionCommand {
        return with(input) {
            val width = readInt()
            val height = readInt()
            SetDimensionCommand(width, height)
        }
    }
}
