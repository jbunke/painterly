package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;

import java.util.Arrays;

import static com.jordanbunke.painterly.util.Layout.ScreenBox;

public final class Workspace implements ProgramContext {
    private static final Workspace INSTANCE;

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
        processGlobalActions(eventLogger);

        // active project processing
    }

    private void processGlobalActions(final InputEventLogger eventLogger) {
        // TODO - return if typing
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        renderProjectButtons(canvas);

        // render project
        final Project p = ProjectManager.get().getProject();

        if (p != null)
            p.render(canvas);
        else {
            renderInPlaceOfProject(canvas);
        }

        // render screen box menus
        Arrays.stream(ScreenBox.values())
                .filter(ScreenBox::isRendered)
                .forEach(sc -> sc.menu().render(canvas));
    }

    private void renderProjectButtons(final GameImage canvas) {
        // TODO - render project buttons
    }

    private void renderInPlaceOfProject(final GameImage canvas) {
        // TODO
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
