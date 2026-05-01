package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.menu.MenuAssembly;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.jordanbunke.painterly.util.Layout.ScreenBox;

public final class Workspace implements ProgramContext {
    private static final Workspace INSTANCE;

    private Menu noProjectsOpenMenu;

    static {
        INSTANCE = new Workspace();
    }

    private Workspace() {
        resetNoProjectsOpenMenu();
    }

    public static Workspace get() {
        return INSTANCE;
    }

    private void resetNoProjectsOpenMenu() {
        noProjectsOpenMenu = MenuAssembly.noProjectsOpenMenu();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
        // global processing
        processGlobalActions(eventLogger);

        // active project processing
        doIfProjectOrNot(p -> p.process(eventLogger),
                () -> noProjectsOpenMenu.process(eventLogger));
    }

    private void processGlobalActions(final InputEventLogger eventLogger) {
        // TODO - return if typing
    }

    @Override
    public void update(final double deltaTime) {
        doIfProjectOrNot(p -> p.update(deltaTime),
                () -> noProjectsOpenMenu.update(deltaTime));
    }

    @Override
    public void render(final GameImage canvas) {
        renderProjectButtons(canvas);

        // render project
        doIfProjectOrNot(p -> p.render(canvas),
                () -> noProjectsOpenMenu.render(canvas));

        // render screen box menus
        Arrays.stream(ScreenBox.values())
                .filter(ScreenBox::isRendered)
                .forEach(sc -> sc.menu().render(canvas));
    }

    private void doIfProjectOrNot(
            final Consumer<Project> ifProject, final Runnable not
    ) {
        final Project p = ProjectManager.get().getProject();

        if (p != null)
            ifProject.accept(p);
        else
            not.run();
    }

    private void renderProjectButtons(final GameImage canvas) {
        // TODO - render project buttons
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
