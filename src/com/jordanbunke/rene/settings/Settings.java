package com.jordanbunke.rene.settings;

import com.jordanbunke.clink.Clink;

public class Settings {
    // DEFAULTS
    private static final double MIN_PROB = 0.0, MAX_PROB = 1.0;
    public static final double DEFAULT_SAMPLE_PROB = 0.5;
    public static final int DEFAULT_STROKES_TO_CALC_SIM = 250, DEFAULT_STROKES_TO_SAVE = 1000;

    // IMMUTABLE
    private final int scaleUp;
    private final String projectName;

    // MUTABLE
    private boolean active;
    private double sampleProb;
    private int strokesToCalculateSimilarity, strokesToSavePainting;
    private Palette palette;

    // FOCUS BOX
    private final FocusBox focusBox;

    public Settings(final String projectName, final int scaleUp) {
        this.projectName = projectName;
        this.scaleUp = scaleUp;

        active = true;

        setStrokesToCalculateSimilarity(DEFAULT_STROKES_TO_CALC_SIM);
        setStrokesToSavePainting(DEFAULT_STROKES_TO_SAVE);

        setSampleProb(DEFAULT_SAMPLE_PROB);

        palette = Palette.ALL;
        focusBox = new FocusBox();
    }

    private static double normalizeProbability(final double p) {
        return Math.min(Math.max(MIN_PROB, p), MAX_PROB);
    }

    // SETTERS

    public void setSampleProb(final double sampleProb) {
        this.sampleProb = normalizeProbability(sampleProb);
    }

    public void setStrokesToCalculateSimilarity(final int strokesToCalculateSimilarity) {
        this.strokesToCalculateSimilarity = strokesToCalculateSimilarity;
    }

    public void setStrokesToSavePainting(final int strokesToSavePainting) {
        this.strokesToSavePainting = strokesToSavePainting;
    }

    public void setPalette(final int paletteIndex) {
        final Palette[] palettes = Palette.values();

        if (paletteIndex >= 0 && paletteIndex < palettes.length)
            this.palette = Palette.values()[paletteIndex];
        else
            Clink.writeError("Index " +
                    Clink.highlight(String.valueOf(paletteIndex), Clink.Mode.ERROR) +
                    " is out of bounds for palette assignment (0 to " +
                    (palettes.length - 1) + ")");
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

    public int getStrokesToCalculateSimilarity() {
        return strokesToCalculateSimilarity;
    }

    public int getStrokesToSavePainting() {
        return strokesToSavePainting;
    }

    public double getSampleProb() {
        return sampleProb;
    }

    public Palette getPalette() {
        return palette;
    }

    public FocusBox getFocusBox() {
        return focusBox;
    }

    public int getScaleUp() {
        return scaleUp;
    }

    public String getProjectName() {
        return projectName;
    }
}
