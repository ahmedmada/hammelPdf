
package org.hammel.paintpdf.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.command.serialization.SerializableTypeface
import org.hammel.paintpdf.common.ITALIC_FONT_BOX_ADJUSTMENT
import org.hammel.paintpdf.contract.LayerContracts

class TextToolCommand(
    multilineText: Array<String>,
    textPaint: Paint,
    boxOffset: Float,
    boxWidth: Float,
    boxHeight: Float,
    toolPosition: PointF,
    rotationAngle: Float,
    typeFaceInfo: SerializableTypeface
) : Command {

    var multilineText = multilineText.clone(); private set
    var textPaint = textPaint; private set
    var boxOffset = boxOffset; private set
    var boxWidth = boxWidth; private set
    var boxHeight = boxHeight; private set
    var toolPosition = toolPosition; private set
    var rotationAngle = rotationAngle; private set
    var typeFaceInfo = typeFaceInfo; private set

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        val textAscent = textPaint.ascent()
        val textDescent = textPaint.descent()
        val textHeight = (textDescent - textAscent) * multilineText.size
        val lineHeight = textHeight / multilineText.size
        var maxTextWidth = multilineText.maxOf { line ->
            textPaint.measureText(line)
        }

        if (typeFaceInfo.italic) {
            maxTextWidth *= ITALIC_FONT_BOX_ADJUSTMENT
        }

        with(canvas) {
            save()
            translate(toolPosition.x, toolPosition.y)
            rotate(rotationAngle)

            val widthScaling = (boxWidth - 2 * boxOffset) / maxTextWidth
            val heightScaling = (boxHeight - 2 * boxOffset) / textHeight
            canvas.scale(widthScaling, heightScaling)

            val scaledHeightOffset = boxOffset / heightScaling
            val scaledWidthOffset = boxOffset / widthScaling
            val scaledBoxWidth = boxWidth / widthScaling
            val scaledBoxHeight = boxHeight / heightScaling

            multilineText.forEachIndexed { index, textLine ->
                canvas.drawText(
                    textLine,
                    scaledWidthOffset - scaledBoxWidth / 2 / if (typeFaceInfo.italic) ITALIC_FONT_BOX_ADJUSTMENT else 1f,
                    -(scaledBoxHeight / 2) + scaledHeightOffset - textAscent + lineHeight * index,
                    textPaint
                )
            }
            restore()
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
