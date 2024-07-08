package org.hammel.paintpdf.command.serialization

import android.graphics.Paint
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.PathCommand

class PathCommandSerializer(version: Int) : VersionSerializer<PathCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: PathCommand) {
        with(kryo) {
            writeObject(output, command.paint)
            writeObject(output, command.path as SerializablePath)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out PathCommand>): PathCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out PathCommand>): PathCommand {
        return with(kryo) {
            val paint = readObject(input, Paint::class.java)
            val path = readObject(input, SerializablePath::class.java)
            PathCommand(Paint(paint), path)
        }
    }
}
