package benchmark;

/**
 * Container for a single benchmark result.
 */
public class BenchmarkResult {
    private final int width;
    private final int height;
    private final double timeMs;
    private final long memoryBytes;
    private final String algorithmName;

    public BenchmarkResult(int width, int height, double timeMs, long memoryBytes) {
        this(width, height, timeMs, memoryBytes, "AdDfsMaze");
    }

    public BenchmarkResult(int width, int height, double timeMs, long memoryBytes, String algorithmName) {
        this.width = width;
        this.height = height;
        this.timeMs = timeMs;
        this.memoryBytes = memoryBytes;
        this.algorithmName = algorithmName;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public double getTimeMs() { return timeMs; }
    public long getMemoryBytes() { return memoryBytes; }
    public String getAlgorithmName() { return algorithmName; }
    public int getCellCount() { return width * height; }

    /**
     * Returns time per cell in microseconds.
     */
    public double getTimePerCell() {
        return (timeMs * 1000.0) / getCellCount();
    }

    /**
     * Returns memory per cell in bytes.
     */
    public double getMemoryPerCell() {
        return (double) memoryBytes / getCellCount();
    }

    @Override
    public String toString() {
        return String.format("%s[%dx%d: %.3f ms, %d KB]",
                algorithmName, width, height, timeMs, memoryBytes / 1024);
    }
}