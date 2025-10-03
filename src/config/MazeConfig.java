package config;

/**
 * Configuration container for maze generation.
 * Immutable once created, with validation on construction.
 */
public class MazeConfig {
    // Maze dimensions
    public final int width;
    public final int height;

    // Generation parameters
    public final long seed;
    public final int historyWindow;
    public final double beta;
    public final double braidProbability;

    // Start/end positions
    public final int startX;
    public final int startY;
    public final int exitX;
    public final int exitY;

    // Runtime options
    public final boolean runBenchmark;
    public final String exportPath;
    public final boolean showVisualization;

    private MazeConfig(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.seed = builder.seed;
        this.historyWindow = builder.historyWindow;
        this.beta = builder.beta;
        this.braidProbability = builder.braidProbability;
        this.startX = builder.startX;
        this.startY = builder.startY;
        this.exitX = builder.exitX;
        this.exitY = builder.exitY;
        this.runBenchmark = builder.runBenchmark;
        this.exportPath = builder.exportPath;
        this.showVisualization = builder.showVisualization;

        validate();
    }

    private void validate() {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive");
        }
        if (historyWindow < 1) {
            throw new IllegalArgumentException("History window must be >= 1");
        }
        if (beta < 0) {
            throw new IllegalArgumentException("Beta must be non-negative");
        }
        if (braidProbability < 0 || braidProbability > 1) {
            throw new IllegalArgumentException("Braid probability must be in [0, 1]");
        }
        if (startX < 0 || startX >= width || startY < 0 || startY >= height) {
            throw new IllegalArgumentException("Start position out of bounds");
        }
        if (exitX < 0 || exitX >= width || exitY < 0 || exitY >= height) {
            throw new IllegalArgumentException("Exit position out of bounds");
        }
    }

    /**
     * Returns default configuration (11x11, seed 21).
     */
    public static MazeConfig getDefault() {
        return new Builder().build();
    }

    /**
     * Builder for fluent configuration creation.
     */
    public static class Builder {
        private int width = 11;
        private int height = 11;
        private long seed = 21L;
        private int historyWindow = 50;
        private double beta = 0.8;
        private double braidProbability = 0.08;
        private int startX = 0;
        private int startY = 0;
        private int exitX = 10;
        private int exitY = 10;
        private boolean runBenchmark = false;
        private String exportPath = null;
        private boolean showVisualization = false;

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder seed(long seed) {
            this.seed = seed;
            return this;
        }

        public Builder historyWindow(int historyWindow) {
            this.historyWindow = historyWindow;
            return this;
        }

        public Builder beta(double beta) {
            this.beta = beta;
            return this;
        }

        public Builder braidProbability(double braidProbability) {
            this.braidProbability = braidProbability;
            return this;
        }

        public Builder start(int x, int y) {
            this.startX = x;
            this.startY = y;
            return this;
        }

        public Builder exit(int x, int y) {
            this.exitX = x;
            this.exitY = y;
            return this;
        }

        public Builder runBenchmark(boolean run) {
            this.runBenchmark = run;
            return this;
        }

        public Builder exportPath(String path) {
            this.exportPath = path;
            return this;
        }

        public Builder showVisualization(boolean show) {
            this.showVisualization = show;
            return this;
        }

        public MazeConfig build() {
            // Auto-adjust exit if dimensions changed
            if (exitX == 10 && exitY == 10 && (width != 11 || height != 11)) {
                exitX = width - 1;
                exitY = height - 1;
            }
            return new MazeConfig(this);
        }
    }

    @Override
    public String toString() {
        return String.format("MazeConfig[%dx%d, seed=%d, β=%.2f, k=%d, p=%.2f]",
                width, height, seed, beta, historyWindow, braidProbability);
    }
}