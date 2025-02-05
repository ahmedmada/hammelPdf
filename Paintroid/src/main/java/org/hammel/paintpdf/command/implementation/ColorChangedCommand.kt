/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.hammel.paintpdf.command.implementation

import android.content.Context
import android.graphics.Canvas
import org.hammel.paintpdf.MainActivity
import org.hammel.paintpdf.command.Command
import org.hammel.paintpdf.contract.LayerContracts
import org.hammel.paintpdf.tools.Tool
import org.hammel.paintpdf.tools.implementation.EraserTool
import org.hammel.paintpdf.tools.implementation.LineTool

class ColorChangedCommand(tool: Tool?, context: Context, color: Int) : Command {

    var tool = tool; private set
    var context = context; private set
    var color = color; private set
    var firstTime = true

    override fun run(canvas: Canvas, layerModel: LayerContracts.Model) {
        if (tool !is LineTool) {
            (context as MainActivity).runOnUiThread {
                tool?.changePaintColor(color)
            }
        } else {
            if (tool is LineTool && !firstTime) {
                (context as MainActivity).runOnUiThread {
                    (tool as LineTool).undoColorChangedCommand(color)
                }
            } else {
                (context as MainActivity).runOnUiThread {
                    tool?.changePaintColor(color)
                }
                firstTime = false
            }
        }
        if (tool !is EraserTool) {
            (context as MainActivity).runOnUiThread {
                (context as MainActivity).bottomNavigationViewHolder.setColorButtonColor(color)
            }
        }
    }

     fun runInUndoMode() {
        if (tool !is LineTool) {
            (context as MainActivity).runOnUiThread {
                tool?.changePaintColor(color, false)
            }
        } else {
            if (tool is LineTool && !firstTime) {
                (context as MainActivity).runOnUiThread {
                    (tool as LineTool).undoColorChangedCommand(color, false)
                }
            } else {
                (context as MainActivity).runOnUiThread {
                    tool?.changePaintColor(color, false)
                }
                firstTime = false
            }
        }
    }

    override fun freeResources() {
        // No resources to free
    }
}
