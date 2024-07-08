package org.hammel.paintpdf.command.serialization

import android.graphics.Paint
import android.graphics.Point
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.hammel.paintpdf.command.implementation.FillCommand
import org.hammel.paintpdf.tools.helper.JavaFillAlgorithmFactory

class FillCommandSerializer(version: Int) : VersionSerializer<FillCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: FillCommand) {
        output.writeFloat(command.colorTolerance)
        with(kryo) {
            writeObject(output, command.clickedPixel)
            writeObject(output, command.paint)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out FillCommand>): FillCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out FillCommand>): FillCommand {
        val tolerance = input.readFloat()
        return with(kryo) {
            val pixel = readObject(input, Point::class.java)
            val paint = readObject(input, Paint::class.java)
            FillCommand(JavaFillAlgorithmFactory(), pixel, paint, tolerance)
        }
    }
}
