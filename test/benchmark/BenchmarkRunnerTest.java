package benchmark;

/**
 * Unit tests for BenchmarkRunner.
 */
public class BenchmarkRunnerTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running BenchmarkRunner Tests...\n");

        testPerformanceMetricsCalculation();
        testBenchmarkResultCreation();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + testsFailed);
        System.out.println("=".repeat(50));

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // ==================== Test Cases ====================

    private static void testPerformanceMetricsCalculation() {
        try {
            java.util.List<BenchmarkResult> results = new java.util.ArrayList<>();
            results.add(new BenchmarkResult(10, 10, 1.0, 1000));
            results.add(new BenchmarkResult(10, 10, 2.0, 1000));
            results.add(new BenchmarkResult(10, 10, 3.0, 1000));

            PerformanceMetrics metrics = PerformanceMetrics.compute(results);

            assertTrue(metrics.averageMs == 2.0, "Average should be 2.0");
            assertTrue(metrics.minMs == 1.0, "Min should be 1.0");
            assertTrue(metrics.maxMs == 3.0, "Max should be 3.0");
            assertTrue(metrics.medianMs == 2.0, "Median should be 2.0");

            pass("testPerformanceMetricsCalculation");
        } catch (Exception e) {
            fail("testPerformanceMetricsCalculation", e);
        }
    }

    private static void testBenchmarkResultCreation() {
        try {
            BenchmarkResult result = new BenchmarkResult(20, 30, 5.5, 10000);

            assertTrue(result.getWidth() == 20, "Width should be 20");
            assertTrue(result.getHeight() == 30, "Height should be 30");
            assertTrue(result.getTimeMs() == 5.5, "Time should be 5.5 ms");
            assertTrue(result.getMemoryBytes() == 10000, "Memory should be 10000 bytes");
            assertTrue(result.getCellCount() == 600, "Cell count should be 600");

            double timePerCell = result.getTimePerCell();
            assertTrue(timePerCell > 0, "Time per cell should be positive");

            pass("testBenchmarkResultCreation");
        } catch (Exception e) {
            fail("testBenchmarkResultCreation", e);
        }
    }

    // ==================== Helper Methods ====================

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static void pass(String testName) {
        System.out.println("✓ " + testName);
        testsPassed++;
    }

    private static void fail(String testName, Exception e) {
        System.out.println("✗ " + testName + ": " + e.getMessage());
        testsFailed++;
    }
}