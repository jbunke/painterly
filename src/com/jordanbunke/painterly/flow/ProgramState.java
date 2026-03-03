package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Tooltip;

import static com.jordanbunke.painterly.util.Layout.*;

public enum ProgramState implements ProgramContext {
    WORKSPACE, MENU;

    private static ProgramState state;
    private static Menu menu;

    private static boolean loading;

    public static ProgramState get() {
        return state;
    }

    public static void set(final ProgramState state, final Menu menu) {
        loading = false;
        ProgramState.state = state;

        if (state == MENU)
            ProgramState.menu = menu;
    }

    public static boolean isLoading() {
        return loading;
    }

    public static void setLoading(final Menu menu) {
        state = MENU;
        ProgramState.menu = menu;
        loading = true;
    }

    public static void to(final Menu menu) {
        loading = false;

        if (state != MENU)
            return;

        ProgramState.menu = menu;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        Tooltip.get().ping(Tooltip.NONE, mousePos);
        Cursor.reset(mousePos);

        switch (state) {
            case WORKSPACE -> Workspace.get().process(eventLogger);
            case MENU -> menu.process(eventLogger);
        }

        Tooltip.get().check();
    }

    @Override
    public void update(final double deltaTime) {
        switch (state) {
            case WORKSPACE -> Workspace.get().update(deltaTime);
            case MENU -> menu.update(deltaTime);
        }
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.fillRectangle(Colors.bg(), 0, 0, width(), height());

        switch (state) {
            case WORKSPACE -> Workspace.get().render(canvas);
            case MENU -> {
                menu.render(canvas);
                // TODO - render common element (if necessary)
            }
        }

        Tooltip.get().render(canvas);
        Cursor.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
