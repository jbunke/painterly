package com.jordanbunke.painterly.core.paint.painter;

public enum Painter {
    DEFAULT(RealPainter.get()),
    ;

    final IPainter painter;

    Painter(final IPainter painter) {
        this.painter = painter;
    }
}
