package com.jordanbunke.painterly.menu.elements.complex.project_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.MenuBar;

import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.MENU_BAR;

public final class ProjectBar extends MenuElement {
    private static ProjectBar INSTANCE;

    private final ProjectButton[] buttons;

    static {
        INSTANCE = build();
    }

    private ProjectBar(final int x, final ProjectButton[] buttons) {
        super(new Coord2D(x, MENU_BAR.y.get()),
                new Bounds2D(1, 1), Anchor.LEFT_TOP, true);

        this.buttons = buttons;
    }

    public static ProjectBar get() {
        return INSTANCE;
    }

    private static ProjectBar build() {
        final int initialX = MenuBar.get().endX() +
                MENU_BAR_PROJECT_BAR_GAP_WIDTH,
                numProjects = ProjectManager.get().getNumberOfProjects(),
                width = ((MENU_BAR.width.get() - FPS_ALLOTTED_WIDTH) - initialX),
                buttonWidth = width / Math.max(numProjects, MIN_PROJECT_BUTTONS_TO_RENDER);

        final ProjectButton[] buttons = new ProjectButton[numProjects];

        for (int i = 0; i < numProjects; i++) {
            final int x = initialX + (i * buttonWidth);
            buttons[i] = ProjectButton.init(x, buttonWidth, i).build();
        }

        return new ProjectBar(initialX, buttons);
    }

    public static void regen() {
        INSTANCE = build();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        for (ProjectButton button : buttons)
            button.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        for (ProjectButton button : buttons)
            button.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        for (ProjectButton button : buttons)
            button.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
