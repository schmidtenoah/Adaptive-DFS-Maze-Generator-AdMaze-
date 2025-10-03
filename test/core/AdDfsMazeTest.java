package core;

/**
 * Unit tests for AdDfsMaze.
 *
 * Note: These tests use basic assertions without JUnit.
 * For production, consider adding JUnit 5 for better test framework.
 */
public class AdDfsMazeTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("Running AdDfsMaze Tests...\n");

        testBasicGeneration();
        testReproducibility();
        testDimensionValidation();
        testParameterValidation();
        testAllCellsVisited();
        testEntranceExitSetting();
        testBoundaryOpening();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Tests passed: " + testsPassed);
        System.out.println("Tests failed: " + testsFailed);
        System.out.println("=".repeat(50));

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    // ==================== Test Cases ====================

    private static void testBasicGeneration() {
        try {
            AdDfsMaze maze = new AdDfsMaze(5, 5, 42L, 10, 0.5, 0.1);
            maze.generate(0, 0);

            char[][] grid = maze.getGrid();
            assertTrue(grid != null, "Grid should not be null");
            assertTrue(grid.length == 11, "Grid height should be 11 (2*5+1)");
            assertTrue(grid[0].length == 11, "Grid width should be 11 (2*5+1)");

            pass("testBasicGeneration");
        } catch (Exception e) {
            fail("testBasicGeneration", e);
        }
    }

    private static void testReproducibility() {
        try {
            AdDfsMaze maze1 = new AdDfsMaze(10, 10, 123L, 50, 0.8, 0.08);
            AdDfsMaze maze2 = new AdDfsMaze(10, 10, 123L, 50, 0.8, 0.08);

            maze1.generate(0, 0);
            maze2.generate(0, 0);

            char[][] grid1 = maze1.getGrid();
            char[][] grid2 = maze2.getGrid();

            assertTrue(gridsEqual(grid1, grid2), "Same seed should produce identical mazes");

            pass("testReproducibility");
        } catch (Exception e) {
            fail("testReproducibility", e);
        }
    }

    private static void testDimensionValidation() {
        try {
            boolean caught = false;
            try {
                new AdDfsMaze(0, 10, 42L, 10, 0.5, 0.1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught, "Should reject zero width");

            caught = false;
            try {
                new AdDfsMaze(10, -5, 42L, 10, 0.5, 0.1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught, "Should reject negative height");

            pass("testDimensionValidation");
        } catch (Exception e) {
            fail("testDimensionValidation", e);
        }
    }

    private static void testParameterValidation() {
        try {
            boolean caught = false;

            // Invalid history window
            try {
                new AdDfsMaze(10, 10, 42L, 0, 0.5, 0.1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught, "Should reject history window < 1");

            // Invalid beta
            caught = false;
            try {
                new AdDfsMaze(10, 10, 42L, 10, -0.5, 0.1);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught, "Should reject negative beta");

            // Invalid braid probability
            caught = false;
            try {
                new AdDfsMaze(10, 10, 42L, 10, 0.5, 1.5);
            } catch (IllegalArgumentException e) {
                caught = true;
            }
            assertTrue(caught, "Should reject braid probability > 1");

            pass("testParameterValidation");
        } catch (Exception e) {
            fail("testParameterValidation", e);
        }
    }

    private static void testAllCellsVisited() {
        try {
            AdDfsMaze maze = new AdDfsMaze(8, 8, 42L, 20, 0.8, 0.05);
            maze.generate(0, 0);

            char[][] grid = maze.getGrid();
            int emptyCells = 0;

            // Count empty cells (should be exactly width * height)
            for (int y = 1; y < grid.length; y += 2) {
                for (int x = 1; x < grid[0].length; x += 2) {
                    if (grid[y][x] == ' ') {
                        emptyCells++;
                    }
                }
            }

            assertTrue(emptyCells == 64, "All 64 cells should be visited (found " + emptyCells + ")");

            pass("testAllCellsVisited");
        } catch (Exception e) {
            fail("testAllCellsVisited", e);
        }
    }

    private static void testEntranceExitSetting() {
        try {
            AdDfsMaze maze = new AdDfsMaze(5, 5, 42L, 10, 0.5, 0.1);
            maze.generate(0, 0);
            maze.setEntranceExit(0, 0, 4, 4);

            String rendered = maze.render();
            assertTrue(rendered.contains("S"), "Rendered maze should contain start marker");
            assertTrue(rendered.contains("E"), "Rendered maze should contain end marker");

            pass("testEntranceExitSetting");
        } catch (Exception e) {
            fail("testEntranceExitSetting", e);
        }
    }

    private static void testBoundaryOpening() {
        try {
            AdDfsMaze maze = new AdDfsMaze(5, 5, 42L, 10, 0.5, 0.1);
            maze.generate(0, 0);

            char[][] gridBefore = copyGrid(maze.getGrid());

            maze.openBoundaryEntrance(0, 0, AdDfsMaze.Dir.UP);

            char[][] gridAfter = maze.getGrid();

            // Check that top wall was opened
            assertTrue(gridAfter[0][1] == ' ', "Top boundary should be opened");

            pass("testBoundaryOpening");
        } catch (Exception e) {
            fail("testBoundaryOpening", e);
        }
    }

    // ==================== Helper Methods ====================

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion failed: " + message);
        }
    }

    private static boolean gridsEqual(char[][] grid1, char[][] grid2) {
        if (grid1.length != grid2.length) return false;

        for (int y = 0; y < grid1.length; y++) {
            if (grid1[y].length != grid2[y].length) return false;
            for (int x = 0; x < grid1[y].length; x++) {
                if (grid1[y][x] != grid2[y][x]) return false;
            }
        }
        return true;
    }

    private static char[][] copyGrid(char[][] grid) {
        char[][] copy = new char[grid.length][];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
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