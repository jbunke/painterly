package com.jordanbunke.painterly.flow;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.events.actions.GlobalAction;
import com.jordanbunke.painterly.events.actions.ProjectAction;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.menu.elements.complex.context_bar.ContextBar;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.MenuBar;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.MenuBarManager;

import java.util.Arrays;

import static com.jordanbunke.painterly.util.Layout.ScreenBox;

public final class Workspace implements ProgramContext {
    private static final Workspace INSTANCE;

    private Menu noProjectsOpenMenu;

    static {
        INSTANCE = new Workspace();
    }

    private Workspace() {
        regen();
    }

    public static Workspace get() {
        return INSTANCE;
    }

    public void regen() {
        noProjectsOpenMenu = MenuAssembly.noProjectsOpenMenu();
        // TODO - additional menus
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // menu bar status processing
        menuBarStatus(eventLogger);

        // TODO
        // menu bar
        MenuBar.get().process(eventLogger);

        // context bar
        ContextBar.get().process(eventLogger);

        // global processing
        processGlobalActions(eventLogger);

        // active project processing
        final Project p = ProjectManager.get().getProject();

        if (p == null)
            noProjectsOpenMenu.process(eventLogger);
        else
            processProjectActions(eventLogger, p);

        // process screen boxes
        for (ScreenBox sc : ScreenBox.values())
            sc.process(eventLogger);
    }

    private void menuBarStatus(final InputEventLogger eventLogger) {
        MenuBarManager.reset();
        MenuBarManager.checkForLoggedClick(eventLogger);
    }

    private void processGlobalActions(final InputEventLogger eventLogger) {
        if (ProgramState.isTyping())
            return;

        for (GlobalAction ga : GlobalAction.values())
            ga.tryForMatchingKeyStroke(eventLogger);
    }

    private void processProjectActions(
            final InputEventLogger eventLogger, final Project p
    ) {
        if (ProgramState.isTyping())
            return;

        for (ProjectAction pa : ProjectAction.values())
            pa.tryForMatchingKeyStroke(eventLogger, p);
    }

    @Override
    public void update(final double deltaTime) {
        // update menu bar
        MenuBar.get().update(deltaTime);

        // update context bar
        ContextBar.get().update(deltaTime);

        // active project processing
        final Project p = ProjectManager.get().getProject();

        if (p == null)
            noProjectsOpenMenu.update(deltaTime);
        else
            p.update();

        // update screen boxes
        for (ScreenBox sc : ScreenBox.values())
            sc.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        renderProjectButtons(canvas);

        // render project
        if (!ProjectManager.get().hasProject())
            noProjectsOpenMenu.render(canvas);

        // render screen boxes
        Arrays.stream(ScreenBox.values())
                .filter(ScreenBox::isRendered)
                .forEach(sc -> sc.render(canvas));

        // render context bar
        ContextBar.get().render(canvas);

        // render menu bar
        MenuBar.get().render(canvas);
    }

    private void renderProjectButtons(final GameImage canvas) {
        // TODO - render project buttons
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
