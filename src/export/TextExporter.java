package export;

import core.AdDfsMaze;
import java.io.*;
import java.nio.file.*;

/**
 * Exports mazes to text files (ASCII/Unicode).
 */
public class TextExporter {
    /**
     * Exports maze to file with default format.
     */
    public void export(AdDfsMaze maze, String filepath) throws IOException {
        export(maze, filepath, true);
    }

    /**
     * Exports maze to file with metadata option.
     */
    public void export(AdDfsMaze maze, String filepath, boolean includeMetadata) throws IOException {
        StringBuilder content = new StringBuilder();

        if (includeMetadata) {
            content.append(generateMetadata(maze));
            content.append("\n");
        }

        content.append(maze.render());

        Files.writeString(Paths.get(filepath), content.toString());
    }

    /**
     * Generates metadata header for the maze.
     */
    private String generateMetadata(AdDfsMaze maze) {
        StringBuilder sb = new StringBuilder();
        sb.append("# AdDfsMaze Export\n");
        sb.append("# Generated: ").append(java.time.LocalDateTime.now()).append("\n");
        sb.append("# Dimensions: ").append(maze.getWidth()).append(" × ").append(maze.getHeight()).append("\n");
        sb.append("# Grid Size: ").append(maze.getGrid()[0].length)
                .append(" × ").append(maze.getGrid().length).append("\n");
        return sb.toString();
    }
}
