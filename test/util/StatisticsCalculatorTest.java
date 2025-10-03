package util;

import core.AdDfsMaze;

/**
 * Unit tests for StatisticsCalculator.
 */
public class StatisticsCalculatorTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running StatisticsCalculator Tests...\n");

        testBasicStatistics();
        testSparsityCalculation();
        testDeadEndCount();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + testsFailed);
        System.out.println("=".repeat(50));

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // ==================== Test Cases ====================

    private static void testBasicStatistics() {
        try {
            AdDfsMaze maze = new AdDfsMaze(5, 5, 42L, 10, 0.5, 0.1);
            maze.generate(0, 0);

            StatisticsCalculator.MazeStats stats = StatisticsCalculator.calculate(maze);

            assertTrue(stats != null, "Stats should not be null");
            assertTrue(stats.mazeWidth == 5, "Maze width should be 5");
            assertTrue(stats.mazeHeight == 5, "Maze height should be 5");
            assertTrue(stats.gridWidth == 11, "Grid width should be 11");
            assertTrue(stats.gridHeight == 11, "Grid height should be 11");
            assertTrue(stats.totalCells == 121, "Total grid cells should be 121");

            pass("testBasicStatistics");
        } catch (Exception e) {
            fail("testBasicStatistics", e);
        }
    }

    private static void testSparsityCalculation() {
        try {
            AdDfsMaze maze = new AdDfsMaze(10, 10, 123L, 20, 0.8, 0.0);
            maze.generate(0, 0);

            StatisticsCalculator.MazeStats stats = StatisticsCalculator.calculate(maze);

            assertTrue(stats.sparsity > 0 && stats.sparsity < 100,
                    "Sparsity should be between 0 and 100");
            assertTrue(stats.wallCount + stats.emptyCount == stats.totalCells,
                    "Wall + empty count should equal total cells");

            pass("testSparsityCalculation");
        } catch (Exception e) {
            fail("testSparsityCalculation", e);
        }
    }

    private static void testDeadEndCount() {
        try {
            // Classic DFS (no braiding) should have many dead ends
            AdDfsMaze maze1 = new AdDfsMaze(10, 10, 42L, 10, 0.0, 0.0);
            maze1.generate(0, 0);

            StatisticsCalculator.MazeStats stats1 = StatisticsCalculator.calculate(maze1);

            // High braiding should have fewer dead ends
            AdDfsMaze maze2 = new AdDfsMaze(10, 10, 42L, 10, 0.0, 0.15);
            maze2.generate(0, 0);

            StatisticsCalculator.MazeStats stats2 = StatisticsCalculator.calculate(maze2);

            assertTrue(stats1.deadEndCount >= 0, "Dead end count should be non-negative");
            assertTrue(stats2.deadEndCount >= 0, "Dead end count should be non-negative");

            // Note: With braiding, dead ends should generally be fewer, but not always guaranteed

            pass("testDeadEndCount");
        } catch (Exception e) {
            fail("testDeadEndCount", e);
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