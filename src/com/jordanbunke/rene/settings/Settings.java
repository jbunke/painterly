package com.jordanbunke.rene.settings;

public class Settings {
    // DEFAULTS
    private static final double MIN_PROB = 0.0, MAX_PROB = 1.0;
    public static final double DEFAULT_SAMPLE_PROB = 0.5;

    // IMMUTABLE
    private final int scaleUp;
    private final String projectName;

    // MUTABLE
    private double sampleProb;

    public Settings(final String projectName, final int scaleUp) {
        this.projectName = projectName;
        this.scaleUp = scaleUp;

        setSampleProb(DEFAULT_SAMPLE_PROB);
    }

    private static double normalizeProbability(final double p) {
        return Math.min(Math.max(MIN_PROB, p), MAX_PROB);
    }

    // SETTERS

    public void setSampleProb(final double sampleProb) {
        this.sampleProb = normalizeProbability(sampleProb);
    }

    // GETTERS

    public double getSampleProb() {
        return sampleProb;
    }
}
