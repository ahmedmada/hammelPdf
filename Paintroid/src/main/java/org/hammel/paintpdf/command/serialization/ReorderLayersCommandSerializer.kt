package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.ReorderLayersCommand

class ReorderLayersCommandSerializer(version: Int) : VersionSerializer<ReorderLayersCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: ReorderLayersCommand) {
        with(output) {
            writeInt(command.position)
            writeInt(command.destination)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ReorderLayersCommand>): ReorderLayersCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out ReorderLayersCommand>): ReorderLayersCommand {
        return with(input) {
            val position = readInt()
            val destination = readInt()
            ReorderLayersCommand(position, destination)
        }
    }
}
