package com.jordanbunke.painterly.menu.elements.complex.context_bar;

public enum ContextBarSection {
    INTERVAL_MODE,
    INTERVAL_PROGRESS,
    INTERVAL_TARGET,
    FOCUS_MODE,
    BOX_DIVISIONS_X,
    BOX_DIVISIONS_Y,
    SIMILARITY;

    private static ContextBarSection selected;

    static {
        selected = null;
    }

    public static ContextBarSection getSelected() {
        return selected;
    }

    public static void select(final ContextBarSection section) {
        selected = section;
    }
}
