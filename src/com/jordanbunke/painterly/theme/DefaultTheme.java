package com.jordanbunke.painterly.theme;

public final class DefaultTheme extends Theme {
    private static final DefaultTheme INSTANCE;

    private DefaultTheme() {
        // TODO
    }

    static {
        INSTANCE = new DefaultTheme();
    }

    public static DefaultTheme get() {
        return INSTANCE;
    }

    // TODO
}
