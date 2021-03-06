/*
 * Copyright (c) 2019 Nicolas Maltais
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to
 * deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom
 * the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.maltaisn.mazegen.maze

import com.maltaisn.mazegen.Configuration
import com.maltaisn.mazegen.render.Canvas
import com.maltaisn.mazegen.render.Point
import java.util.*


/**
 * Class for a normal square-tiled orthogonal maze with [width] columns and [height] rows.
 */
open class OrthogonalMaze(width: Int, height: Int) :
        BaseGridMaze<OrthogonalCell>(width, height) {

    final override val grid: Array<Array<OrthogonalCell>>

    init {
        grid = Array(width) { x ->
            Array(height) { y ->
                OrthogonalCell(this, Position2D(x, y))
            }
        }
    }

    override fun drawTo(canvas: Canvas, style: Configuration.Style) {
        val w = grid.size
        val h = grid[0].size
        val csive = style.cellSize
        canvas.init(w * csive + style.stroke.lineWidth,
                h * csive + style.stroke.lineWidth)

        // Draw the background
        if (style.backgroundColor != null) {
            canvas.zIndex = 0
            canvas.color = style.backgroundColor
            canvas.drawRect(0.0, 0.0, canvas.width, canvas.height, true)
        }

        val offset = style.stroke.lineWidth / 2.0
        canvas.translate = Point(offset, offset)

        // Draw the distance map
        if (hasDistanceMap) {
            canvas.zIndex = 10

            val distMapColors = style.generateDistanceMapColors(this)
            for (x in 0 until w) {
                for (y in 0 until h) {
                    val px = x * csive
                    val py = y * csive
                    canvas.color = distMapColors[grid[x][y].distanceMapValue]
                    canvas.drawRect(px, py, csive, csive, true)
                }
            }
        }

        // Draw the maze
        // For each cell, only the north and west walls are drawn if they are set,
        // except for the last row and column where to south and east walls are also drawn.
        canvas.zIndex = 20
        canvas.color = style.color
        canvas.stroke = style.stroke
        for (x in 0..w) {
            val px = x * csive
            for (y in 0..h) {
                val py = y * csive
                val cell = cellAt(x, y)

                if (cell != null && cell.hasSide(OrthogonalCell.Side.NORTH) || cell == null
                        && cellAt(x, y - 1)?.hasSide(OrthogonalCell.Side.SOUTH) == true) {
                    canvas.drawLine(px, py, px + csive, py)
                }
                if (cell != null && cell.hasSide(OrthogonalCell.Side.WEST) || cell == null
                        && cellAt(x - 1, y)?.hasSide(OrthogonalCell.Side.EAST) == true) {
                    canvas.drawLine(px, py, px, py + csive)
                }
            }
        }

        // Draw the solution
        if (solution != null) {
            canvas.zIndex = 30
            canvas.color = style.solutionColor
            canvas.stroke = style.solutionStroke

            val points = LinkedList<Point>()
            for (cell in solution!!) {
                val pos = cell.position as Position2D
                val px = (pos.x + 0.5) * csive
                val py = (pos.y + 0.5) * csive
                points.add(Point(px, py))
            }
            canvas.drawPath(points)
        }
    }

}
