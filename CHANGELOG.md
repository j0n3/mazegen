## v1.1.0
- Added Dijkstra's distance maps for all maze types.
- Added ability to export maze, solved maze and distance mapped maze as separate files.
- Added optimization levels to SVG output from 0 to 4, levels above 1 are experimental.
- Fixed background color not drawn in raster output formats.

## v1.0.0
- Initial release
- JSON configuration with schema
- 7 maze types:
    - **Orthogonal**: square cells, can be turned into a unicursal maze (labyrinth)
    - **Weave orthogonal**: like orthogonal but allow passages going over and under.
    - **Delta**: triangle cells, can be shaped like a rectangle, hexagon, triangle or rhombus.
    - **Sigma**: hexagon cells, can be shaped like a rectangle, hexagon, triangle or rhombus.
    - **Theta**: circle maze, adjustable center radius and subdivision parameter.
    - **Upsilon**: octogon and square cells.
    - **Zeta**: like orthogonal but allows diagonal passages.
- 11 maze algorithms: most of them work for all maze types.
    - **Aldous-Broder**
    - **Binary tree**: different side biases can be set.
    - **Eller's**: adjustable horizontal and vertical bias.
    - **Growing tree**: adjustable weights for choosing oldest, newest and random cell in the stack.
    - **Hunt-and-kill**
    - **Kruskal's**
    - **Prim's**
    - **Recursive backtracker**
    - **Recursive division**
    - **Sidewinder**
    - **Wilson's**
- Openings can be added
- Solving with A* algorithm
- Braiding (removing deadends)
- Exporting
    - Every maze type can be drawn with its solution.
    - 5 formats: PNG, JPG, BMP, GIF, SVG.
    - SVG precision can be tuned and path data can be optimizeted.
    - Styling settings can be changed: cell size, background color, stroke color, width and cap, antialiasing.
