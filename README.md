# AdMaze – Adaptive DFS Maze Generator

AdMaze is a Java project for generating mazes using the classic DFS backtracking algorithm, extended with two key innovations:

1. **Anti-Persistence Heuristic (`beta, k`)**
   Prevents long, monotonous corridors by making recently used directions less likely (via softmax weighting).

2. **Braiding (`pBraid`)**  
   Intentionally opens additional passages to reduce dead ends and create loops — without allowing 2×2 open rooms.

## Features
- Configurable maze size (W, H)
- Start (S) and exit (E) markers
- Optional braiding functionality
- ASCII-based visualization in the terminal
- Runtime measurement (nanosecond precision)

## Roadmap
- Benchmarking: Compare runtime with DFS, Prim, Kruskal, etc.
- Visualizer: Show maze generation in progress
- Exports: Save mazes as PNG images
- Algorithm extensions: Implement dead-end braiding
- Documentation: Write and commit the full theoretical background
- Comments: Improve documentation
- Clean-up: Refactor and optimize code where needed
- Testing: Add unit tests
