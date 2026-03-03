package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.painterly.menu.MenuAssembly;

public final class Workspace implements ProgramContext {
    private static final Workspace INSTANCE;
    // private Menu menu;

    static {
        INSTANCE = new Workspace();
    }

    private Workspace() {
        // menu = MenuAssembly.stub();
    }

    public static Workspace get() {
        return INSTANCE;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
        // global processing
        // active project processing
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        // TODO
        // global rendering
        // active project rendering
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
