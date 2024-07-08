package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.MergeLayersCommand

class MergeLayersCommandSerializer(version: Int) : VersionSerializer<MergeLayersCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: MergeLayersCommand) {
        with(output) {
            writeInt(command.position)
            writeInt(command.mergeWith)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out MergeLayersCommand>): MergeLayersCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out MergeLayersCommand>): MergeLayersCommand {
        return with(input) {
            val position = readInt()
            val mergeWith = readInt()
            MergeLayersCommand(position, mergeWith)
        }
    }
}
