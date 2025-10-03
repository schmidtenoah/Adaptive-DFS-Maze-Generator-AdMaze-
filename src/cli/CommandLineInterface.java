package cli;

import config.MazeConfig;
import config.AlgorithmProfile;
import core.AdDfsMaze;
import benchmark.BenchmarkRunner;
import export.TextExporter;
import util.MazeRenderer;
import util.StatisticsCalculator;

/**
 * Command-line interface for maze generation.
 * Handles argument parsing, execution flow, and output formatting.
 */
public class CommandLineInterface {

    public void run(String[] args) {
        try {
            if (args.length == 0) {
                runDefault();
                return;
            }

            MazeConfig config = parseArguments(args);

            if (config.runBenchmark) {
                runBenchmark();
                return;
            }

            generateAndDisplay(config);

        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Use --help for usage information");
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Runs with default configuration.
     */
    private void runDefault() {
        MazeConfig config = MazeConfig.getDefault();
        printHeader();
        generateAndDisplay(config);
    }

    /**
     * Generates and displays a maze with given configuration.
     */
    private void generateAndDisplay(MazeConfig config) {
        printHeader();
        printConfiguration(config);

        // Create maze
        AdDfsMaze maze = new AdDfsMaze(
                config.width, config.height, config.seed,
                config.historyWindow, config.beta, config.braidProbability
        );

        // Measure generation time
        long startTime = System.nanoTime();
        maze.generate(config.startX, config.startY);
        long endTime = System.nanoTime();

        // Setup entrance/exit
        maze.setEntranceExit(config.startX, config.startY, config.exitX, config.exitY);
        maze.openBoundaryEntrance(config.startX, config.startY, AdDfsMaze.Dir.UP);
        maze.openBoundaryEntrance(config.exitX, config.exitY, AdDfsMaze.Dir.DOWN);

        // Display
        System.out.println("Generated Maze:");
        System.out.println();
        MazeRenderer.printMaze(maze);
        System.out.println();

        // Statistics
        double timeMs = (endTime - startTime) / 1_000_000.0;
        System.out.printf("Generation time: %.3f ms (%.6f s)%n", timeMs, timeMs / 1000.0);
        System.out.println();
        StatisticsCalculator.printStatistics(maze);

        // Export if requested
        if (config.exportPath != null) {
            exportMaze(maze, config);
        }
    }

    /**
     * Runs benchmark suite.
     */
    private void runBenchmark() {
        BenchmarkRunner runner = new BenchmarkRunner();
        runner.runFullSuite();
    }

    /**
     * Exports maze to file.
     */
    private void exportMaze(AdDfsMaze maze, MazeConfig config) {
        try {
            TextExporter exporter = new TextExporter();
            exporter.export(maze, config.exportPath);
            System.out.println("\nExported to: " + config.exportPath);
        } catch (Exception e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }

    /**
     * Parses command-line arguments into configuration.
     */
    private MazeConfig parseArguments(String[] args) {
        MazeConfig.Builder builder = new MazeConfig.Builder();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg) {
                case "-w":
                case "--width":
                    builder.width(parseIntArg(args, ++i, "width"));
                    break;

                case "-h":
                case "--height":
                    builder.height(parseIntArg(args, ++i, "height"));
                    break;

                case "-s":
                case "--seed":
                    builder.seed(parseLongArg(args, ++i, "seed"));
                    break;

                case "-b":
                case "--beta":
                    builder.beta(parseDoubleArg(args, ++i, "beta"));
                    break;

                case "-k":
                case "--history":
                    builder.historyWindow(parseIntArg(args, ++i, "history"));
                    break;

                case "-p":
                case "--braid":
                    builder.braidProbability(parseDoubleArg(args, ++i, "braid"));
                    break;

                case "--profile":
                    applyProfile(builder, args[++i]);
                    break;

                case "--benchmark":
                    builder.runBenchmark(true);
                    break;

                case "-o":
                case "--output":
                    builder.exportPath(args[++i]);
                    break;

                case "--help":
                    printHelp();
                    System.exit(0);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown option: " + arg);
            }
        }

        return builder.build();
    }

    /**
     * Applies algorithm profile to builder.
     */
    private void applyProfile(MazeConfig.Builder builder, String profileName) {
        AlgorithmProfile profile = AlgorithmProfile.fromString(profileName);
        profile.applyTo(builder);
        System.out.println("Applied profile: " + profile);
        System.out.println();
    }

    // ==================== Argument Parsing Helpers ====================

    private int parseIntArg(String[] args, int index, String name) {
        checkArgument(args, index, name);
        try {
            return Integer.parseInt(args[index]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be an integer");
        }
    }

    private long parseLongArg(String[] args, int index, String name) {
        checkArgument(args, index, name);
        try {
            return Long.parseLong(args[index]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a long");
        }
    }

    private double parseDoubleArg(String[] args, int index, String name) {
        checkArgument(args, index, name);
        try {
            return Double.parseDouble(args[index]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a number");
        }
    }

    private void checkArgument(String[] args, int index, String name) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for " + name);
        }
    }

    // ==================== Output Formatting ====================

    private void printHeader() {
        System.out.println("=".repeat(60));
        System.out.println("  AdDfsMaze Generator v1.0");
        System.out.println("=".repeat(60));
        System.out.println();
    }

    private void printConfiguration(MazeConfig config) {
        System.out.println("Configuration:");
        System.out.println("  Size: " + config.width + " × " + config.height);
        System.out.println("  Seed: " + config.seed);
        System.out.println("  Anti-Persistence: β=" + config.beta + ", k=" + config.historyWindow);
        System.out.println("  Braiding: p=" + config.braidProbability);
        System.out.println();
    }

    private void printHelp() {
        System.out.println("AdDfsMaze Generator - Usage:");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -w, --width <n>       Maze width in cells (default: 11)");
        System.out.println("  -h, --height <n>      Maze height in cells (default: 11)");
        System.out.println("  -s, --seed <n>        Random seed (default: 21)");
        System.out.println("  -b, --beta <f>        Anti-persistence strength (default: 0.8)");
        System.out.println("  -k, --history <n>     History window size (default: 50)");
        System.out.println("  -p, --braid <f>       Braiding probability (default: 0.08)");
        System.out.println("  --profile <name>      Use preset (classic/winding/open/complex/sparse)");
        System.out.println("  --benchmark           Run performance benchmarks");
        System.out.println("  -o, --output <file>   Export maze to file");
        System.out.println("  --help                Show this help");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java Main");
        System.out.println("  java Main --width 20 --height 15");
        System.out.println("  java Main --profile winding --seed 42");
        System.out.println("  java Main --benchmark");
        System.out.println("  java Main -w 30 -h 30 -o maze.txt");
        System.out.println();
        System.out.println("Profiles:");
        for (AlgorithmProfile profile : AlgorithmProfile.values()) {
            System.out.println("  " + profile);
        }
    }
}