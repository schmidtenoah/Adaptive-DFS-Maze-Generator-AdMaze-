package util;

import core.AdDfsMaze;

/**
 * Calculates and displays statistics about generated mazes.
 */
public class StatisticsCalculator {

    /**
     * Container for maze statistics.
     */
    public static class MazeStats {
        public final int gridWidth;
        public final int gridHeight;
        public final int mazeWidth;
        public final int mazeHeight;
        public final int totalCells;
        public final int wallCount;
        public final int emptyCount;
        public final double sparsity;
        public final int deadEndCount;

        public MazeStats(int gridWidth, int gridHeight, int mazeWidth, int mazeHeight,
                         int totalCells, int wallCount, int emptyCount,
                         double sparsity, int deadEndCount) {
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            this.mazeWidth = mazeWidth;
            this.mazeHeight = mazeHeight;
            this.totalCells = totalCells;
            this.wallCount = wallCount;
            this.emptyCount = emptyCount;
            this.sparsity = sparsity;
            this.deadEndCount = deadEndCount;
        }
    }

    /**
     * Calculates comprehensive statistics for a maze.
     */
    public static MazeStats calculate(AdDfsMaze maze) {
        char[][] grid = maze.getGrid();

        int gridWidth = grid[0].length;
        int gridHeight = grid.length;
        int mazeWidth = maze.getWidth();
        int mazeHeight = maze.getHeight();

        int totalCells = gridWidth * gridHeight;
        int wallCount = 0;
        int emptyCount = 0;

        // Count walls and empty spaces
        for (char[] row : grid) {
            for (char cell : row) {
                if (cell == '#') wallCount++;
                else emptyCount++;
            }
        }

        double sparsity = (double) emptyCount / totalCells * 100;

        // Count dead ends (cells with only one exit)
        int deadEndCount = countDeadEnds(grid);

        return new MazeStats(gridWidth, gridHeight, mazeWidth, mazeHeight,
                totalCells, wallCount, emptyCount, sparsity, deadEndCount);
    }

    /**
     * Counts dead ends in the maze (cells with 3 walls).
     */
    private static int countDeadEnds(char[][] grid) {
        int count = 0;

        // Check only odd coordinates (actual cells, not walls)
        for (int y = 1; y < grid.length; y += 2) {
            for (int x = 1; x < grid[0].length; x += 2) {
                if (grid[y][x] == ' ') {
                    int walls = 0;

                    // Check 4 neighbors
                    if (y > 0 && grid[y-1][x] == '#') walls++;
                    if (y < grid.length - 1 && grid[y+1][x] == '#') walls++;
                    if (x > 0 && grid[y][x-1] == '#') walls++;
                    if (x < grid[0].length - 1 && grid[y][x+1] == '#') walls++;

                    if (walls == 3) count++;
                }
            }
        }

        return count;
    }

    /**
     * Prints formatted statistics to console.
     */
    public static void printStatistics(AdDfsMaze maze) {
        MazeStats stats = calculate(maze);

        System.out.println("Statistics:");
        System.out.println("  Grid size: " + stats.gridWidth + " × " + stats.gridHeight);
        System.out.println("  Maze cells: " + stats.mazeWidth + " × " + stats.mazeHeight);
        System.out.printf("  Sparsity: %.1f%% open%n", stats.sparsity);
        System.out.println("  Dead ends: " + stats.deadEndCount);
        System.out.printf("  Connectivity: %.1f%%%n",
                (100.0 - (double) stats.deadEndCount / (stats.mazeWidth * stats.mazeHeight) * 100));
    }

    /**
     * Returns a compact one-line summary.
     */
    public static String summarize(AdDfsMaze maze) {
        MazeStats stats = calculate(maze);
        return String.format("%dx%d maze, %.1f%% open, %d dead ends",
                stats.mazeWidth, stats.mazeHeight, stats.sparsity, stats.deadEndCount);
    }
}