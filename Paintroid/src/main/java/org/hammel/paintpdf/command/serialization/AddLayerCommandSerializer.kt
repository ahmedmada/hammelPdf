package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.AddEmptyLayerCommand
import org.hammel.paintpdf.common.CommonFactory

class AddLayerCommandSerializer(version: Int) : VersionSerializer<AddEmptyLayerCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: AddEmptyLayerCommand) {
        // Has no member variables to save
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out AddEmptyLayerCommand>): AddEmptyLayerCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out AddEmptyLayerCommand>): AddEmptyLayerCommand =
        AddEmptyLayerCommand(CommonFactory())
}
