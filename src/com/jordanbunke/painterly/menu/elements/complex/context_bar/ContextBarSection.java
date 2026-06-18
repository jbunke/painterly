package com.jordanbunke.painterly.menu.elements.complex.context_bar;

public enum ContextBarSection {
    TOOL,
    STROKE_COUNT,
    INTERVAL_PROGRESS,
    INTERVAL_TARGET,
    FOCUS_BOX_MODE,
    DIVS_X,
    DIVS_Y,
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
