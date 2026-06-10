package com.jordanbunke.painterly.tool;

import com.jordanbunke.painterly.util.Cursor;

public final class Hand extends Tool {
    private static final Hand INSTANCE;

    static {
        INSTANCE = new Hand();
    }

    private boolean panning;

    private Hand() {

    }

    public static Hand get() {
        return INSTANCE;
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        if (mouseInViewport)
            Cursor.ping(panning ? Cursor.HAND_GRAB : Cursor.HAND_OPEN);
    }
}
