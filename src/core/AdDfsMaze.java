package core;

import java.util.*;

/**
 * Anti-Persistent DFS Maze Generator with Braiding.
 *
 * Features:
 * - Anti-persistence heuristic to avoid long straight corridors
 * - Optional braiding to create loops and reduce dead ends
 * - Deterministic generation via seed
 *
 * @version 1.0
 */
public class AdDfsMaze {
    private static final char WALL = '#', EMPTY = ' ';

    private final int width, height;
    private final char[][] grid;
    private final boolean[][] visited;

    // Anti-persistence parameters
    private final int historyWindow;
    private final double antiPersistence;
    private final int[] directionCounts;
    private final Deque<Dir> recentDirs;

    // Braiding
    private final double braidProbability;
    private final Random rng;

    // Entrance/Exit (use -1 for unset)
    private int entranceX = -1, entranceY = -1;
    private int exitX = -1, exitY = -1;

    public enum Dir {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        final int dx, dy;
        Dir(int dx, int dy) { this.dx = dx; this.dy = dy; }
    }

    /**
     * Creates a new maze generator.
     *
     * @throws IllegalArgumentException if parameters are invalid
     */
    public AdDfsMaze(int width, int height, long seed,
                     int historyWindow, double antiPersistence,
                     double braidProbability) {
        // Validation
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        if (historyWindow < 1) {
            throw new IllegalArgumentException("History window must be >= 1");
        }
        if (antiPersistence < 0) {
            throw new IllegalArgumentException("Anti-persistence must be non-negative");
        }
        if (braidProbability < 0 || braidProbability > 1) {
            throw new IllegalArgumentException("Braid probability must be in [0, 1]");
        }

        this.width = width;
        this.height = height;
        this.historyWindow = historyWindow;
        this.antiPersistence = antiPersistence;
        this.braidProbability = braidProbability;
        this.rng = new Random(seed);

        // Initialize grid (all walls)
        this.grid = new char[2 * height + 1][2 * width + 1];
        for (int y = 0; y < grid.length; y++) {
            Arrays.fill(grid[y], WALL);
        }

        this.visited = new boolean[height][width];
        this.directionCounts = new int[4];
        this.recentDirs = new ArrayDeque<>(historyWindow);
    }

    /**
     * Generates the maze starting from the given cell.
     */
    public void generate(int startX, int startY) {
        if (!isInBounds(startX, startY)) {
            throw new IllegalArgumentException("Start position out of bounds");
        }

        carveCell(startX, startY);
        Deque<Cell> stack = new ArrayDeque<>();
        stack.push(new Cell(startX, startY));

        while (!stack.isEmpty()) {
            Cell current = stack.peek();

            List<Dir> candidates = getUnvisitedNeighbors(current.x, current.y);

            if (candidates.isEmpty()) {
                stack.pop();
                continue;
            }

            Dir chosen = sampleByAntiPersistence(candidates);
            int nextX = current.x + chosen.dx;
            int nextY = current.y + chosen.dy;

            carvePassage(current.x, current.y, nextX, nextY);
            carveCell(nextX, nextY);

            stack.push(new Cell(nextX, nextY));
            recordDirection(chosen);

            // FIX: Braid at NEW position (not old one)
            if (rng.nextDouble() < braidProbability) {
                attemptBraid(nextX, nextY);
            }
        }
    }

    /**
     * Sets entrance and exit positions (for rendering markers).
     */
    public void setEntranceExit(int entranceX, int entranceY, int exitX, int exitY) {
        if (!isInBounds(entranceX, entranceY) || !isInBounds(exitX, exitY)) {
            throw new IllegalArgumentException("Entrance/exit out of bounds");
        }
        this.entranceX = entranceX;
        this.entranceY = entranceY;
        this.exitX = exitX;
        this.exitY = exitY;
    }

    /**
     * Opens a boundary wall (only if at actual edge).
     *
     * FIX: Validates that the wall is actually at the boundary.
     */
    public void openBoundaryEntrance(int cellX, int cellY, Dir direction) {
        if (!isInBounds(cellX, cellY)) {
            throw new IllegalArgumentException("Cell out of bounds");
        }

        int wallY = 2 * cellY + 1 + direction.dy;
        int wallX = 2 * cellX + 1 + direction.dx;

        // Validate that this is actually a boundary wall
        if (wallY < 0 || wallY >= grid.length ||
                wallX < 0 || wallX >= grid[0].length) {
            return; // Out of grid = nothing to open
        }

        // Additional check: only open if at actual boundary
        boolean atBoundary = false;
        if (direction == Dir.UP && cellY == 0) atBoundary = true;
        if (direction == Dir.DOWN && cellY == height - 1) atBoundary = true;
        if (direction == Dir.LEFT && cellX == 0) atBoundary = true;
        if (direction == Dir.RIGHT && cellX == width - 1) atBoundary = true;

        if (atBoundary) {
            grid[wallY][wallX] = EMPTY;
        }
    }

    // ==================== Core Generation ====================

    private void carveCell(int x, int y) {
        grid[2 * y + 1][2 * x + 1] = EMPTY;
        visited[y][x] = true;
    }

    private void carvePassage(int x1, int y1, int x2, int y2) {
        int wallY = (2 * y1 + 1 + 2 * y2 + 1) / 2;
        int wallX = (2 * x1 + 1 + 2 * x2 + 1) / 2;
        grid[wallY][wallX] = EMPTY;
    }

    private List<Dir> getUnvisitedNeighbors(int x, int y) {
        List<Dir> neighbors = new ArrayList<>(4);
        for (Dir dir : Dir.values()) {
            int nx = x + dir.dx;
            int ny = y + dir.dy;
            if (isInBounds(nx, ny) && !visited[ny][nx]) {
                neighbors.add(dir);
            }
        }
        return neighbors;
    }

    private Dir sampleByAntiPersistence(List<Dir> candidates) {
        if (candidates.size() == 1) return candidates.get(0);

        double[] weights = new double[candidates.size()];
        double sumWeights = 0.0;

        for (int i = 0; i < candidates.size(); i++) {
            int count = directionCounts[candidates.get(i).ordinal()];
            weights[i] = Math.exp(-antiPersistence * count);
            sumWeights += weights[i];
        }

        double r = rng.nextDouble() * sumWeights;
        double accumulated = 0.0;

        for (int i = 0; i < weights.length; i++) {
            accumulated += weights[i];
            if (r <= accumulated) {
                return candidates.get(i);
            }
        }

        return candidates.get(candidates.size() - 1);
    }

    private void recordDirection(Dir dir) {
        recentDirs.addLast(dir);
        directionCounts[dir.ordinal()]++;

        if (recentDirs.size() > historyWindow) {
            Dir oldest = recentDirs.removeFirst();
            directionCounts[oldest.ordinal()]--;
        }
    }

    private void attemptBraid(int x, int y) {
        List<Dir> visitedNeighbors = new ArrayList<>();

        for (Dir dir : Dir.values()) {
            int nx = x + dir.dx;
            int ny = y + dir.dy;
            if (isInBounds(nx, ny) && visited[ny][nx]) {
                visitedNeighbors.add(dir);
            }
        }

        if (visitedNeighbors.isEmpty()) return;

        Collections.shuffle(visitedNeighbors, rng);

        for (Dir dir : visitedNeighbors) {
            int nx = x + dir.dx;
            int ny = y + dir.dy;

            int wallY = (2 * y + 1 + 2 * ny + 1) / 2;
            int wallX = (2 * x + 1 + 2 * nx + 1) / 2;

            if (grid[wallY][wallX] == WALL && !creates2x2Opening(wallX, wallY)) {
                grid[wallY][wallX] = EMPTY;
                return;
            }
        }
    }

    /**
     * FIX: Checks if opening wall at (wallX, wallY) creates a 2x2 open area.
     * Uses direct grid checking instead of visited heuristics.
     */
    private boolean creates2x2Opening(int wallX, int wallY) {
        // Check all 2x2 blocks that include this wall position
        for (int dy = -1; dy <= 0; dy++) {
            for (int dx = -1; dx <= 0; dx++) {
                int topY = wallY + dy;
                int leftX = wallX + dx;

                // Check if this 2x2 block is valid
                if (topY < 0 || topY + 1 >= grid.length ||
                        leftX < 0 || leftX + 1 >= grid[0].length) {
                    continue;
                }

                // Count empty cells in 2x2 block (including the wall we'd open)
                int emptyCount = 0;
                for (int y = 0; y < 2; y++) {
                    for (int x = 0; x < 2; x++) {
                        int checkY = topY + y;
                        int checkX = leftX + x;
                        if (checkY == wallY && checkX == wallX) {
                            emptyCount++; // Count the wall we'd open
                        } else if (grid[checkY][checkX] == EMPTY) {
                            emptyCount++;
                        }
                    }
                }

                // If all 4 would be empty, we'd create a 2x2 hole
                if (emptyCount == 4) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // ==================== Rendering ====================

    /**
     * Returns string representation of the maze.
     * FIX: Only shows S/E if they were explicitly set.
     */
    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                char cell = grid[y][x];

                // Only show S/E if set AND on empty cells
                boolean isEntrance = (entranceX >= 0 && x == 2 * entranceX + 1 && y == 2 * entranceY + 1);
                boolean isExit = (exitX >= 0 && x == 2 * exitX + 1 && y == 2 * exitY + 1);

                if (isEntrance && cell == EMPTY) {
                    sb.append("S ");
                } else if (isExit && cell == EMPTY) {
                    sb.append("E ");
                } else {
                    sb.append(cell == WALL ? "██" : "  ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void print() {
        System.out.print(render());
    }

    // ==================== Getters ====================

    public char[][] getGrid() { return grid; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Helper class to avoid allocating int[] arrays in the stack.
     */
    private static class Cell {
        final int x, y;
        Cell(int x, int y) { this.x = x; this.y = y; }
    }
}