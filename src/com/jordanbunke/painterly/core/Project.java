package com.jordanbunke.painterly.core;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;

public final class Project implements ProgramContext {
    /**
     * {@code true} is attempted strokes; {@code false} is completed strokes
     * */
    private boolean tickMode;
    // TODO


    public boolean isTickMode() {
        return tickMode;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        // TODO
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
