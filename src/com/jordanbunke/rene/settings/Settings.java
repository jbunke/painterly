package com.jordanbunke.rene.settings;

import com.jordanbunke.clink.Clink;

public class Settings {
    // DEFAULTS
    private static final double MIN_PROB = 0.0, MAX_PROB = 1.0;
    public static final double DEFAULT_SAMPLE_PROB = 0.5;

    // IMMUTABLE
    private final int scaleUp;
    private final String projectName;

    // MUTABLE
    private boolean active;
    private double sampleProb;

    public Settings(final String projectName, final int scaleUp) {
        this.projectName = projectName;
        this.scaleUp = scaleUp;

        active = true;

        setSampleProb(DEFAULT_SAMPLE_PROB);
    }

    private static double normalizeProbability(final double p) {
        return Math.min(Math.max(MIN_PROB, p), MAX_PROB);
    }

    // SETTERS

    public void setSampleProb(final double sampleProb) {
        this.sampleProb = normalizeProbability(sampleProb);
    }

    public void activate() {
        active = true;
        Clink.writeUpdate("Activated painter");
    }

    public void deactivate() {
        active = false;
        Clink.writeUpdate("Deactivated painter");
    }

    public void toggleActive() {
        if (active)
            deactivate();
        else
            activate();
    }

    // GETTERS

    public boolean isActive() {
        return active;
    }

    public double getSampleProb() {
        return sampleProb;
    }

    public String getProjectName() {
        return projectName;
    }
}
