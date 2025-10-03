package util;

import core.AdDfsMaze;
import java.io.PrintStream;

/**
 * Utility for rendering mazes to various outputs.
 * Separated from AdDfsMaze to follow Single Responsibility Principle.
 */
public class MazeRenderer {

    /**
     * Character styles for maze rendering.
     */
    public enum Style {
        /** Unicode block characters (█) */
        UNICODE_BLOCK("█ ", "  "),

        /** ASCII hash marks (##) */
        ASCII_HASH("##", "  "),

        /** ASCII brackets ([]) */
        ASCII_BRACKET("[]", "  "),

        /** Box drawing characters (▓) */
        UNICODE_SHADE("▓▓", "  "),

        /** Simple plus and spaces */
        ASCII_PLUS("++", "  ");

        private final String wall;
        private final String empty;

        Style(String wall, String empty) {
            this.wall = wall;
            this.empty = empty;
        }

        public String getWall() { return wall; }
        public String getEmpty() { return empty; }
    }

    private final Style style;
    private final boolean showMarkers;

    /**
     * Creates renderer with default style (Unicode block).
     */
    public MazeRenderer() {
        this(Style.UNICODE_BLOCK, true);
    }

    /**
     * Creates renderer with custom style.
     */
    public MazeRenderer(Style style, boolean showMarkers) {
        this.style = style;
        this.showMarkers = showMarkers;
    }

    /**
     * Renders maze to string.
     */
    public String render(AdDfsMaze maze) {
        return maze.render(); // Uses AdDfsMaze's internal rendering
    }

    /**
     * Renders maze with custom style.
     */
    public String renderWithStyle(AdDfsMaze maze) {
        char[][] grid = maze.getGrid();
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                char cell = grid[y][x];

                // For now, simple rendering (can be extended with entrance/exit markers)
                sb.append(cell == '#' ? style.getWall() : style.getEmpty());
            }
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     * Renders maze to PrintStream (typically System.out).
     */
    public void render(AdDfsMaze maze, PrintStream out) {
        out.print(render(maze));
    }

    /**
     * Renders with box borders around the maze.
     */
    public String renderWithBorder(AdDfsMaze maze, String title) {
        String mazeStr = render(maze);
        String[] lines = mazeStr.split("\n");

        int width = lines[0].length();
        StringBuilder sb = new StringBuilder();

        // Top border
        sb.append("╔").append("═".repeat(width)).append("╗\n");

        // Title (if provided)
        if (title != null && !title.isEmpty()) {
            int padding = (width - title.length()) / 2;
            sb.append("║")
                    .append(" ".repeat(Math.max(0, padding)))
                    .append(title)
                    .append(" ".repeat(Math.max(0, width - title.length() - padding)))
                    .append("║\n");
            sb.append("╠").append("═".repeat(width)).append("╣\n");
        }

        // Maze content
        for (String line : lines) {
            sb.append("║").append(line).append("║\n");
        }

        // Bottom border
        sb.append("╚").append("═".repeat(width)).append("╝\n");

        return sb.toString();
    }

    /**
     * Static convenience method for quick rendering.
     */
    public static void printMaze(AdDfsMaze maze) {
        new MazeRenderer().render(maze, System.out);
    }

    /**
     * Static method with custom style.
     */
    public static void printMaze(AdDfsMaze maze, Style style) {
        System.out.print(new MazeRenderer(style, true).renderWithStyle(maze));
    }
}