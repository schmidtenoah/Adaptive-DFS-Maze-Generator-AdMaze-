package benchmark;

import config.MazeConfig;
import core.AdDfsMaze;
import java.util.*;

/**
 * Comprehensive benchmarking suite for maze generation algorithms.
 */
public class BenchmarkRunner {

    private static final int[] DEFAULT_SIZES = {5, 10, 25, 50, 100};
    private static final int DEFAULT_ITERATIONS = 5;
    private static final int WARMUP_ITERATIONS = 3;

    /**
     * Runs full benchmark suite.
     */
    public void runFullSuite() {
        System.out.println("=".repeat(70));
        System.out.println("  BENCHMARK SUITE: AdDfsMaze Generator");
        System.out.println("=".repeat(70));
        System.out.println();

        runSizeBenchmark();
        System.out.println();
        runParameterSensitivity();
    }

    /**
     * Benchmarks different maze sizes.
     */
    public void runSizeBenchmark() {
        System.out.println("Size Benchmark (default parameters):");
        System.out.printf("%-12s %-15s %-15s %-15s %-15s%n",
                "Size", "Avg (ms)", "Min (ms)", "Max (ms)", "StdDev (ms)");
        System.out.println("-".repeat(72));

        for (int size : DEFAULT_SIZES) {
            warmup(size, size);
            List<BenchmarkResult> results = new ArrayList<>();

            for (int i = 0; i < DEFAULT_ITERATIONS; i++) {
                BenchmarkResult result = benchmarkSingle(size, size, i);
                results.add(result);
            }

            PerformanceMetrics metrics = PerformanceMetrics.compute(results);

            System.out.printf("%-12s %-15.3f %-15.3f %-15.3f %-15.3f%n",
                    size + "×" + size,
                    metrics.averageMs,
                    metrics.minMs,
                    metrics.maxMs,
                    metrics.stdDevMs);
        }
    }

    /**
     * Benchmarks parameter sensitivity (beta, braiding).
     */
    public void runParameterSensitivity() {
        int size = 50;

        System.out.println("Parameter Sensitivity (50×50 maze):");
        System.out.println();

        // Beta sensitivity
        System.out.println("Anti-Persistence (β) Impact:");
        System.out.printf("%-10s %-15s %-15s%n", "Beta", "Avg (ms)", "StdDev (ms)");
        System.out.println("-".repeat(40));

        double[] betas = {0.0, 0.4, 0.8, 1.2, 1.6};
        for (double beta : betas) {
            warmup(size, size);
            List<BenchmarkResult> results = new ArrayList<>();

            for (int i = 0; i < DEFAULT_ITERATIONS; i++) {
                BenchmarkResult result = benchmarkWithBeta(size, size, i, beta);
                results.add(result);
            }

            PerformanceMetrics metrics = PerformanceMetrics.compute(results);
            System.out.printf("%-10.1f %-15.3f %-15.3f%n",
                    beta, metrics.averageMs, metrics.stdDevMs);
        }

        System.out.println();

        // Braiding sensitivity
        System.out.println("Braiding Probability Impact:");
        System.out.printf("%-10s %-15s %-15s%n", "Braid p", "Avg (ms)", "StdDev (ms)");
        System.out.println("-".repeat(40));

        double[] braidProbs = {0.0, 0.05, 0.10, 0.15, 0.20};
        for (double p : braidProbs) {
            warmup(size, size);
            List<BenchmarkResult> results = new ArrayList<>();

            for (int i = 0; i < DEFAULT_ITERATIONS; i++) {
                BenchmarkResult result = benchmarkWithBraiding(size, size, i, p);
                results.add(result);
            }

            PerformanceMetrics metrics = PerformanceMetrics.compute(results);
            System.out.printf("%-10.2f %-15.3f %-15.3f%n",
                    p, metrics.averageMs, metrics.stdDevMs);
        }
    }

    /**
     * Benchmarks large mazes.
     */
    public void runLargeMazeBenchmark() {
        System.out.println("Large Maze Benchmark:");
        System.out.printf("%-12s %-15s %-20s%n", "Size", "Time (ms)", "Cells/ms");
        System.out.println("-".repeat(50));

        int[] largeSizes = {100, 200, 500, 1000};

        for (int size : largeSizes) {
            warmup(size, size);

            BenchmarkResult result = benchmarkSingle(size, size, 42);
            double cellsPerMs = result.getCellCount() / result.getTimeMs();

            System.out.printf("%-12s %-15.3f %-20.0f%n",
                    size + "×" + size,
                    result.getTimeMs(),
                    cellsPerMs);
        }
    }

    /**
     * Benchmarks memory usage.
     */
    public void runMemoryBenchmark() {
        System.out.println("Memory Usage Benchmark:");
        System.out.printf("%-12s %-15s %-20s%n", "Size", "Memory (KB)", "Bytes/Cell");
        System.out.println("-".repeat(50));

        for (int size : DEFAULT_SIZES) {
            // Force GC before measurement
            System.gc();
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            long memBefore = getUsedMemory();
            AdDfsMaze maze = new AdDfsMaze(size, size, 42L, 50, 0.8, 0.08);
            maze.generate(0, 0);
            long memAfter = getUsedMemory();

            long memUsed = memAfter - memBefore;
            double bytesPerCell = (double) memUsed / (size * size);

            System.out.printf("%-12s %-15d %-20.1f%n",
                    size + "×" + size,
                    memUsed / 1024,
                    bytesPerCell);
        }
    }

    // ==================== Helper Methods ====================

    /**
     * Warms up JVM for more accurate benchmarks.
     */
    private void warmup(int width, int height) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            AdDfsMaze maze = new AdDfsMaze(width, height, i, 50, 0.8, 0.08);
            maze.generate(0, 0);
        }
    }

    /**
     * Benchmarks a single maze generation with default parameters.
     */
    private BenchmarkResult benchmarkSingle(int width, int height, long seed) {
        AdDfsMaze maze = new AdDfsMaze(width, height, seed, 50, 0.8, 0.08);

        long startTime = System.nanoTime();
        maze.generate(0, 0);
        long endTime = System.nanoTime();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        long memUsed = getUsedMemory();

        return new BenchmarkResult(width, height, timeMs, memUsed);
    }

    /**
     * Benchmarks with specific beta value.
     */
    private BenchmarkResult benchmarkWithBeta(int width, int height, long seed, double beta) {
        AdDfsMaze maze = new AdDfsMaze(width, height, seed, 50, beta, 0.08);

        long startTime = System.nanoTime();
        maze.generate(0, 0);
        long endTime = System.nanoTime();

        double timeMs = (endTime - startTime) / 1_000_000.0;

        return new BenchmarkResult(width, height, timeMs, 0);
    }

    /**
     * Benchmarks with specific braiding probability.
     */
    private BenchmarkResult benchmarkWithBraiding(int width, int height, long seed, double p) {
        AdDfsMaze maze = new AdDfsMaze(width, height, seed, 50, 0.8, p);

        long startTime = System.nanoTime();
        maze.generate(0, 0);
        long endTime = System.nanoTime();

        double timeMs = (endTime - startTime) / 1_000_000.0;

        return new BenchmarkResult(width, height, timeMs, 0);
    }

    /**
     * Gets current used memory in bytes.
     */
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}