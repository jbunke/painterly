package com.jordanbunke.painterly.core.paint.painter;

public final class PainterManager {
    private static Painter currentPainter;

    static {
        currentPainter = Painter.DEFAULT; // TODO - setting
    }

    public static IPainter get() {
        return currentPainter.painter;
    }

    // TODO - set
}
