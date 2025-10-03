package benchmark;

import java.util.List;

/**
 * Computes statistical metrics from multiple benchmark results.
 */
public class PerformanceMetrics {
    public final double averageMs;
    public final double minMs;
    public final double maxMs;
    public final double stdDevMs;
    public final double medianMs;

    public PerformanceMetrics(double averageMs, double minMs, double maxMs,
                              double stdDevMs, double medianMs) {
        this.averageMs = averageMs;
        this.minMs = minMs;
        this.maxMs = maxMs;
        this.stdDevMs = stdDevMs;
        this.medianMs = medianMs;
    }

    /**
     * Computes metrics from a list of benchmark results.
     */
    public static PerformanceMetrics compute(List<BenchmarkResult> results) {
        if (results.isEmpty()) {
            return new PerformanceMetrics(0, 0, 0, 0, 0);
        }

        double[] times = results.stream()
                .mapToDouble(BenchmarkResult::getTimeMs)
                .toArray();

        double avg = average(times);
        double min = min(times);
        double max = max(times);
        double stdDev = standardDeviation(times, avg);
        double median = median(times);

        return new PerformanceMetrics(avg, min, max, stdDev, median);
    }

    private static double average(double[] values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum / values.length;
    }

    private static double min(double[] values) {
        double min = Double.MAX_VALUE;
        for (double v : values) {
            if (v < min) min = v;
        }
        return min;
    }

    private static double max(double[] values) {
        double max = Double.MIN_VALUE;
        for (double v : values) {
            if (v > max) max = v;
        }
        return max;
    }

    private static double standardDeviation(double[] values, double mean) {
        double sumSquaredDiff = 0;
        for (double v : values) {
            double diff = v - mean;
            sumSquaredDiff += diff * diff;
        }
        return Math.sqrt(sumSquaredDiff / values.length);
    }

    private static double median(double[] values) {
        double[] sorted = values.clone();
        java.util.Arrays.sort(sorted);

        int mid = sorted.length / 2;
        if (sorted.length % 2 == 0) {
            return (sorted[mid - 1] + sorted[mid]) / 2.0;
        } else {
            return sorted[mid];
        }
    }

    @Override
    public String toString() {
        return String.format("avg=%.3f ms, min=%.3f ms, max=%.3f ms, stddev=%.3f ms",
                averageMs, minMs, maxMs, stdDevMs);
    }
}