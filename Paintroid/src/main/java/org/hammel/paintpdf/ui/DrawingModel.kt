package org.hammel.paintpdf.ui

import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.Workspace

data class DrawingModel(
    var layerModel: LayerContracts.Model,
    var workspace: Workspace,
    var perspective: Perspective,
    var drawingSurface: DrawingSurface,
//    var idlingResource: CountingIdlingResource = CountingIdlingResource("MainIdleResource")

)
