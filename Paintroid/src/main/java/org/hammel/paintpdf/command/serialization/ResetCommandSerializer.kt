package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.ResetCommand

class ResetCommandSerializer(version: Int) : VersionSerializer<ResetCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: ResetCommand) {
        // Has no member variables to save
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ResetCommand>): ResetCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out ResetCommand>): ResetCommand =
        ResetCommand()
}
