package org.hammel.paintpdf.tools.helper

class JavaFillAlgorithmFactory : FillAlgorithmFactory {
    override fun createFillAlgorithm(): FillAlgorithm = JavaFillAlgorithm()
}
