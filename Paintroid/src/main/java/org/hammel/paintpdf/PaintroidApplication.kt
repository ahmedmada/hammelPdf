package org.hammel.paintpdf

import java.io.File
import java.lang.IllegalArgumentException

@SuppressWarnings("ThrowingExceptionsWithoutMessageOrCause")
class PaintroidApplication private constructor() {
    companion object {
        @JvmStatic
        var cacheDir: File? = null
    }

    init {
        throw IllegalArgumentException()
    }
}
