package config;

/**
 * Predefined configuration profiles for different maze styles.
 */
public enum AlgorithmProfile {
    /**
     * Classic DFS without anti-persistence (straight corridors).
     */
    CLASSIC(0.0, 10, 0.0),

    /**
     * Highly winding paths with strong anti-persistence.
     */
    WINDING(1.2, 80, 0.05),

    /**
     * Open mazes with many loops and few dead ends.
     */
    OPEN(0.5, 40, 0.15),

    /**
     * Balanced complexity (default-like).
     */
    COMPLEX(0.8, 50, 0.12),

    /**
     * Minimal braiding, moderate anti-persistence.
     */
    SPARSE(0.6, 30, 0.03);

    private final double beta;
    private final int historyWindow;
    private final double braidProbability;

    AlgorithmProfile(double beta, int historyWindow, double braidProbability) {
        this.beta = beta;
        this.historyWindow = historyWindow;
        this.braidProbability = braidProbability;
    }

    /**
     * Applies this profile to a config builder.
     */
    public void applyTo(MazeConfig.Builder builder) {
        builder.beta(beta)
                .historyWindow(historyWindow)
                .braidProbability(braidProbability);
    }

    /**
     * Creates a new config from this profile with given dimensions.
     */
    public MazeConfig createConfig(int width, int height, long seed) {
        MazeConfig.Builder builder = new MazeConfig.Builder()
                .width(width)
                .height(height)
                .seed(seed);
        applyTo(builder);
        return builder.build();
    }

    /**
     * Parses profile name (case-insensitive).
     */
    public static AlgorithmProfile fromString(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown profile: " + name + ". Available: classic, winding, open, complex, sparse");
        }
    }

    @Override
    public String toString() {
        return String.format("%s (β=%.1f, k=%d, p=%.2f)",
                name().toLowerCase(), beta, historyWindow, braidProbability);
    }
}