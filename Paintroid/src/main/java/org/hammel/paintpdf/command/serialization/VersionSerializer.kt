package org.hammel.paintpdf.command.serialization

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input

abstract class VersionSerializer<T>(val version: Int) : Serializer<T>() {

    companion object {
        private const val V1 = 1
        private const val V2 = 2
        private const val V3 = 3
    }

    protected fun handleVersions(serializer: VersionSerializer<T>, kryo: Kryo, input: Input, type: Class<out T>): T {
        return when (version) {
            // Currently just here to see the intended pattern
            V1 -> serializer.readV1(serializer, kryo, input, type)
            V2 -> serializer.readV2(serializer, kryo, input, type)
            V3 -> serializer.readV3(serializer, kryo, input, type)
            // Enable when CURRENT_IMAGE_VERSION reached 4
            // CommandSerializationUtilities.CURRENT_IMAGE_VERSION -> serializer.readCurrentVersion(kryo, input, type)
            else -> throw KryoException()
        }
    }

    protected open fun readV1(serializer: VersionSerializer<T>, kryo: Kryo, input: Input, type: Class<out T>): T =
        serializer.readV2(serializer, kryo, input, type)

    protected open fun readV2(serializer: VersionSerializer<T>, kryo: Kryo, input: Input, type: Class<out T>): T =
        serializer.readV3(serializer, kryo, input, type)

    protected open fun readV3(serializer: VersionSerializer<T>, kryo: Kryo, input: Input, type: Class<out T>): T =
        serializer.readCurrentVersion(kryo, input, type)

    abstract fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out T>): T
}
