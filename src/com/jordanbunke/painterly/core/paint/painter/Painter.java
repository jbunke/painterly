package com.jordanbunke.painterly.core.paint.painter;

public enum Painter {
    DEFAULT(DefaultPainter.get()),
    ;

    final IPainter painter;

    Painter(final IPainter painter) {
        this.painter = painter;
    }
}
