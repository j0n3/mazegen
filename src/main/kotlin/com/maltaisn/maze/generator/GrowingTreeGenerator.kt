/*
 * Copyright (c) 2018 Nicolas Maltais
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

package com.maltaisn.maze.generator

import com.maltaisn.maze.maze.Cell
import com.maltaisn.maze.maze.Maze


/**
 * Implementation of the Growing Tree algorithm as described
 * [here](http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm#).
 *
 * 1. Create an empty list containing only a random initial cell, marked as visited.
 * 2. Choose a cell from the list and find an unvisited neighbor
 *     - Connect the cell and the neighbor.
 *     - Add the neighbor to the list.
 *     - Mark the neighbor as visited.
 * 3. If the cell has no unvisited neighbors, remove it from the list.
 * 4. Repeat step 2 and 3 until list is empty.
 *
 * The cell at step 2 can be chosen in a variety of ways to produce completely
 * different mazes. If only the newest added cell is chosen, this will produce
 * the same mazes as [RecursiveBacktrackerGenerator]. If the cell is always
 * chosen at random, it will produce the same mazes as [PrimGenerator].
 *
 * [setChooseByWeight] can be used to assign weights to a random choice between
 * choosing a random cell, the newest cell and the oldest cell.
 * [cellChooser] can also be set directly for customized behavior.
 */
class GrowingTreeGenerator(maze: Maze) : Generator(maze) {

    /**
     * The function used for choosing a cell instead of weights.
     * [setChooseByWeight] can be used for auto generating a function.
     */
    lateinit var cellChooser: ((List<Cell>) -> Cell)

    init {
        setChooseByWeight(1, 1, 0)
    }

    override fun generate() {
        maze.reset(false)

        val initialCell = maze.getRandomCell()
        initialCell.visited = true
        val list = mutableListOf(initialCell)
        do {
            // Choose a cell from list
            val currentCell = cellChooser(list)

            var connected = false
            for (neighbor in currentCell.getNeighbors().shuffled()) {
                if (!neighbor.visited) {
                    // Found unvisited neighbor, connect it to current cell
                    currentCell.connectWith(neighbor)
                    connected = true

                    // Mark neighbor as visited and add it to list
                    list.add(neighbor)
                    neighbor.visited = true
                    break
                }
            }
            if (!connected) {
                // Cell has no unvisited neighbor: remove it from list
                list.remove(currentCell)
            }
        } while (list.isNotEmpty())
    }

    /**
     * Set weights used to choose the next cell.
     * @param[random] weight for choosing a random cell.
     * @param[newest] weight for choosing the last added cell.
     * @param[oldest] weight for choosing the first added cell.
     */
    fun setChooseByWeight(random: Int, newest: Int, oldest: Int) {
        val weightSum = random + newest + oldest
        cellChooser = { list ->
            val choice = (0 until weightSum).random()
            when {
                choice < random -> list.random()
                choice < random + newest -> list.last()
                else -> list.first()
            }
        }
    }

}