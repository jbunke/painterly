package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.*;

import java.util.function.Supplier;

import static com.jordanbunke.delta_time.utility.DeltaTimeGlobal.getStatusOf;
import static com.jordanbunke.painterly.util.Layout.*;

public enum ProgramState implements ProgramContext {
    WORKSPACE, MENU;

    private static ProgramState state;
    private static Menu menu;
    private static Supplier<Menu> lastSource;

    private static boolean loading;

    public static ProgramState get() {
        return state;
    }

    public static void setWorkspace() {
        set(WORKSPACE, null);
    }

    public static void setMenu(final Supplier<Menu> source) {
        set(MENU, source);
    }

    private static void set(final ProgramState state, final Supplier<Menu> source) {
        loading = false;
        ProgramState.state = state;

        if (state == MENU) {
            lastSource = source;
            menu = source.get();
        }
    }

    private static void setLoading(final Supplier<Menu> source) {
        state = MENU;
        lastSource = source;
        menu = source.get();
        loading = true;
    }

    public static void load(
            final Runnable task, final ResourceCode code,
            final Runnable yieldTo
    ) {
        setLoading(() -> MenuAssembly.loading(code));

        final Thread taskThread = new Thread(() -> {
            task.run();
            if (loading)
                yieldTo.run();
        }, "Loading task");
        taskThread.start();
    }

    /**
     * Shorthand for {@link ProgramState#load(Runnable, ResourceCode, Runnable)}
     * for when the task yields to the project workspace
     * */
    public static void load(final Runnable task, final ResourceCode code) {
        load(task, code, ProgramState::setWorkspace);
    }

    public static void to(final Supplier<Menu> source) {
        loading = false;

        if (state != MENU)
            return;

        lastSource = source;
        menu = source.get();
    }

    public static void regen() {
        if (state == MENU)
            menu = lastSource.get();
    }

    public static boolean isTyping() {
        return getStatusOf(Constants.TYPING_CODE)
                .orElse(false) instanceof Boolean b && b;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        resetMouseData(eventLogger);

        if (DialogManager.has()) {
            DialogManager.get().process(eventLogger);
        } else {
            switch (state) {
                case WORKSPACE -> Workspace.get().process(eventLogger);
                case MENU -> menu.process(eventLogger);
            }
        }

        Tooltip.get().check();
    }

    private void resetMouseData(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        Tooltip.get().ping(Tooltip.NONE, mousePos);
        Cursor.reset(mousePos);
        Locks.resetHover();
    }

    @Override
    public void update(final double deltaTime) {
        if (DialogManager.has()) {
            DialogManager.get().update(deltaTime);
        } else {
            switch (state) {
                case WORKSPACE -> Workspace.get().update(deltaTime);
                case MENU -> menu.update(deltaTime);
            }
        }
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.fillRectangle(Colors.bg(), 0, 0, width(), height());

        switch (state) {
            case WORKSPACE -> Workspace.get().render(canvas);
            case MENU -> {
                // TODO - render common element (if necessary)
                // e.g. screen box border
                menu.render(canvas);
            }
        }

        if (DialogManager.has())
            DialogManager.get().render(canvas);

        Tooltip.get().render(canvas);
        Cursor.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
