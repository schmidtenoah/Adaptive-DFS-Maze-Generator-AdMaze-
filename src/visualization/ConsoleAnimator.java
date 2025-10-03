package visualization;

import core.AdDfsMaze;

/**
 * Console-based animation for maze generation.
 * Uses ANSI escape codes for terminal animation.
 *
 * Note: Requires terminal with ANSI support (most Linux/Mac terminals, Windows Terminal).
 */
public class ConsoleAnimator {

    private static final String CLEAR_SCREEN = "\033[2J";
    private static final String CURSOR_HOME = "\033[H";
    private static final String HIDE_CURSOR = "\033[?25l";
    private static final String SHOW_CURSOR = "\033[?25h";

    private final int delayMs;
    private final boolean useColors;

    /**
     * Creates animator with default settings (50ms delay, colors enabled).
     */
    public ConsoleAnimator() {
        this(50, true);
    }

    /**
     * Creates animator with custom settings.
     */
    public ConsoleAnimator(int delayMs, boolean useColors) {
        this.delayMs = delayMs;
        this.useColors = useColors;
    }

    /**
     * Animates maze generation step-by-step.
     *
     * Note: This would require AdDfsMaze to support callbacks/listeners.
     * For now, this is a placeholder for future implementation.
     */
    public void animate(AdDfsMaze maze) {
        System.out.println("Console animation requires callback support in AdDfsMaze.");
        System.out.println("This is a placeholder for future implementation.");
        System.out.println();
        System.out.println("To implement:");
        System.out.println("1. Add GenerationListener interface to AdDfsMaze");
        System.out.println("2. Call listener methods during generation");
        System.out.println("3. ConsoleAnimator implements the listener");
        System.out.println("4. Renders maze state after each step");
    }

    /**
     * Clears screen and moves cursor to top-left.
     */
    private void clearScreen() {
        System.out.print(CLEAR_SCREEN + CURSOR_HOME);
        System.out.flush();
    }

    /**
     * Hides cursor for cleaner animation.
     */
    private void hideCursor() {
        System.out.print(HIDE_CURSOR);
        System.out.flush();
    }

    /**
     * Shows cursor after animation.
     */
    private void showCursor() {
        System.out.print(SHOW_CURSOR);
        System.out.flush();
    }

    /**
     * Sleeps for animation delay.
     */
    private void delay() {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Example of how animated output would look (static demo).
     */
    public static void demoAnimation() {
        ConsoleAnimator animator = new ConsoleAnimator();
        animator.hideCursor();

        System.out.println("Demo: Animated maze generation would look like this:");
        System.out.println("(Each step shows the maze growing)");
        System.out.println();

        String[] frames = {
                "S \n████",
                "S   \n██████",
                "S     ██\n████████",
                "S     ██\n████  ████",
                "S     ██  E\n████  ████"
        };

        for (String frame : frames) {
            animator.clearScreen();
            System.out.println("Generating maze...");
            System.out.println();
            System.out.println(frame);
            animator.delay();
        }

        animator.showCursor();
        System.out.println("\n\nAnimation complete!");
    }
}