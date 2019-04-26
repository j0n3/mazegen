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

package com.maltaisn.mazegen.generator

import com.maltaisn.mazegen.maze.Cell
import com.maltaisn.mazegen.maze.Maze
import kotlin.random.Random


/**
 * Implementation of the hunt-and-kill algorithm for maze generation as described
 * [here](http://weblog.jamisbuck.org/2011/1/24/maze-generation-hunt-and-kill-algorithm).
 *
 * 1. Make the initial cell the current cell and mark it as visited.
 * 2. Kill: Randomly connect the current cell with an unvisited neighbor, changing the
 *          current cell to the neighbor, until there are no more unvisited neighbors.
 * 3. Hunt: Find a visited cell with an unvisited neighbor. Make it the current cell.
 *          If there's none, the maze is done.
 *
 * Generated mazes are very similar to those generated by the recursive backtracker,
 * because the only difference in the algorithm is that instead of backtracking, it
 * chooses a cell at random from those already visited. That also makes it much slower.
 *
 * This implementation is actually different than most hunt-and-kill implementations
 * because instead of finding an unvisited cell next to a visited cells, it does the
 * opposite, and randomly. As a result, it's faster but also produces results more
 * similar to the recursive backtracker.
 *
 * Runtime complexity is O(n) and memory space is O(n).
 * A traditional implementation would have complexity of O(n²) and memory space of O(1).
 */
class HuntKillGenerator : Generator() {

    override fun generate(maze: Maze) {
        super.generate(maze)

        maze.fillAll()

        val visitedCells = mutableListOf<Cell>()

        // Get and mark the initial cell as visited
        var currentCell = maze.randomCell
        currentCell.visited = true
        visitedCells.add(currentCell)

        while (true) {
            // Kill: perform random walk until cell has no unvisited neighbors
            while (true) {
                // Find an unvisited neighbor cell
                val neighbor = currentCell.neighbors.shuffled().find { !it.visited }
                if (neighbor != null) {
                    // Connect with current cell
                    currentCell.connectWith(neighbor)

                    // Make the connected cell the current cell, mark it as visited
                    currentCell = neighbor
                    currentCell.visited = true
                    visitedCells.add(currentCell)
                } else {
                    // No unvisited neighbor cell
                    break
                }
            }

            // Hunt: find a visited cell with an unvisited neighbor
            var found = false
            while (visitedCells.size > 0) {
                val index = Random.nextInt(visitedCells.size)
                val cell = visitedCells[index]
                if (cell.neighbors.find { !it.visited } != null) {
                    // Found one
                    currentCell = cell
                    found = true
                    break
                } else {
                    // Visited cells has no unvisited neighbor,
                    // remove it to prevent checking again in the future
                    visitedCells.removeAt(index)
                }
            }
            if (!found) {
                // There are no unvisited cells left, maze is done
                break
            }
        }
    }

    override fun isMazeSupported(maze: Maze) = true

}
