package org.hammel.paintpdf.command.serialization

import android.graphics.Point
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.CutCommand

class CutCommandSerializer(version: Int) : VersionSerializer<CutCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: CutCommand) {
        kryo.writeObject(output, command.toolPosition)
        with(output) {
            writeFloat(command.boxWidth)
            writeFloat(command.boxHeight)
            writeFloat(command.boxRotation)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out CutCommand>): CutCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out CutCommand>): CutCommand {
        val position = kryo.readObject(input, Point::class.java)
        return with(input) {
            val width = readFloat()
            val height = readFloat()
            val rotation = readFloat()
            CutCommand(position, width, height, rotation)
        }
    }
}
