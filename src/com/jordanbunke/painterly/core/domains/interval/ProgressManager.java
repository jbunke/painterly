package com.jordanbunke.painterly.core.domains.interval;

import com.jordanbunke.painterly.core.Project;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TODO - describe role of class
 * */
public final class ProgressManager {
    public static final boolean GLOBAL = false, FOCUS = true;

    private final Project project;

    private double globalSimilarity, focusSimilarity;
    private boolean display;

    public ProgressManager(final Project project) {
        this.project = project;

        display = GLOBAL;
        update();
    }

    public void update() {
        globalSimilarity = project.canvas.globalSimilarity();
        focusSimilarity = project.focusManager.isWholeCanvas()
                ? globalSimilarity
                : project.canvas.similarity(project.focusManager.getFocusArea());
    }

    public String formattedSimilarity() {
        final double similarity = display == FOCUS
                ? focusSimilarity : globalSimilarity;
        return BigDecimal.valueOf(similarity * 100)
                .setScale(2 /* TODO - setting */, RoundingMode.HALF_UP) + "%";
    }

    public void setDisplayToFocus() {
        display = FOCUS;
    }

    public void setDisplayToGlobal() {
        display = GLOBAL;
    }

    public double getGlobalSimilarity() {
        return globalSimilarity;
    }

    public double getFocusSimilarity() {
        return focusSimilarity;
    }

    public boolean isDisplay() {
        return display;
    }
}
