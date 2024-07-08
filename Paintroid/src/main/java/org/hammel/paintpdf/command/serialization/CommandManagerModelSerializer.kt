package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.model.CommandManagerModel

class CommandManagerModelSerializer(version: Int) : VersionSerializer<CommandManagerModel>(version) {
    override fun write(kryo: Kryo, output: Output, model: CommandManagerModel) {
        with(kryo) {
            writeClassAndObject(output, model.initialCommand)
            output.writeInt(model.commands.size)
            model.commands.forEach { command ->
                writeClassAndObject(output, command)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out CommandManagerModel>): CommandManagerModel =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out CommandManagerModel>): CommandManagerModel {
        return with(kryo) {
            val initCommand = kryo.readClassAndObject(input) as Command
            val size = input.readInt()
            val commandList = ArrayList<Command>()
            repeat(size) {
                commandList.add(kryo.readClassAndObject(input) as Command)
            }
            CommandManagerModel(initCommand, commandList)
        }
    }
}
