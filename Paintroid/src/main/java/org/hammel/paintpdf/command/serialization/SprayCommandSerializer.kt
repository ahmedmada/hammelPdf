package org.hammel.paintpdf.command.serialization

import android.graphics.Paint
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.SprayCommand

class SprayCommandSerializer(version: Int) : VersionSerializer<SprayCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: SprayCommand) {
        with(kryo) {
            writeObject(output, command.sprayedPoints)
            writeObject(output, command.paint)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out SprayCommand>): SprayCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SprayCommand>): SprayCommand {
        return with(kryo) {
            val sprayPoints = kryo.readObject(input, FloatArray::class.java)
            val paint = kryo.readObject(input, Paint::class.java)
            SprayCommand(sprayPoints, paint)
        }
    }
}
