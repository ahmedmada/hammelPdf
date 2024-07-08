package org.hammel.paintpdf.command.serialization

import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class DataStructuresSerializer {
    class FloatArraySerializer(version: Int) : VersionSerializer<FloatArray>(version) {
        override fun write(kryo: Kryo, output: Output, array: FloatArray) {
            with(output) {
                writeInt(array.size)
                array.forEach { floatValue ->
                    writeFloat(floatValue)
                }
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out FloatArray>): FloatArray =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out FloatArray>): FloatArray {
            return with(input) {
                val size = readInt()
                val floatList = ArrayList<Float>()
                repeat(size) {
                    floatList.add(readFloat())
                }
                floatList.toFloatArray()
            }
        }
    }

    class StringArraySerializer(version: Int) : VersionSerializer<Array<String>>(version) {
        override fun write(kryo: Kryo, output: Output, array: Array<String>) {
            with(output) {
                writeInt(array.size)
                array.forEach { str ->
                    writeString(str)
                }
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Array<String>>): Array<String> =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Array<String>>): Array<String> {
            return with(input) {
                val size = readInt()
                val strList = ArrayList<String>()
                repeat(size) {
                    strList.add(readString())
                }
                strList.toTypedArray()
            }
        }
    }

    class RectFSerializer(version: Int) : VersionSerializer<RectF>(version) {
        override fun write(kryo: Kryo, output: Output, rect: RectF) {
            with(output) {
                writeFloat(rect.left)
                writeFloat(rect.top)
                writeFloat(rect.right)
                writeFloat(rect.bottom)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out RectF>): RectF =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out RectF>): RectF {
            return with(input) {
                RectF(readFloat(), readFloat(), readFloat(), readFloat())
            }
        }
    }

    class PointFSerializer(version: Int) : VersionSerializer<PointF>(version) {
        override fun write(kryo: Kryo, output: Output, rect: PointF) {
            with(output) {
                writeFloat(rect.x)
                writeFloat(rect.y)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out PointF>): PointF =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out PointF>): PointF {
            return with(input) {
                PointF(readFloat(), readFloat())
            }
        }
    }

    class PointSerializer(version: Int) : VersionSerializer<Point>(version) {
        override fun write(kryo: Kryo, output: Output, rect: Point) {
            with(output) {
                writeInt(rect.x)
                writeInt(rect.y)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Point>): Point =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Point>): Point {
            return with(input) {
                Point(readInt(), readInt())
            }
        }
    }
}
