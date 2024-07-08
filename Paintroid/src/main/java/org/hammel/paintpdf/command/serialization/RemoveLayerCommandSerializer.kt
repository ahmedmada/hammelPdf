package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.RemoveLayerCommand

class RemoveLayerCommandSerializer(version: Int) : VersionSerializer<RemoveLayerCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: RemoveLayerCommand) {
        with(output) {
            writeInt(command.position)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out RemoveLayerCommand>): RemoveLayerCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out RemoveLayerCommand>): RemoveLayerCommand {
        return with(input) {
            val position = readInt()
            RemoveLayerCommand(position)
        }
    }
}
