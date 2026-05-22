package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;

import static com.jordanbunke.painterly.util.Colors.systemColor;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.PROJECT_VIEWPORT;

public final class Viewport implements ProgramContext {
    private static final Viewport INSTANCE;

    private int x, y, width, height;
    private int anchorX, anchorY;
    private Project lastProject;
    private Zoom zoom;

    static {
        INSTANCE = new Viewport();
    }

    private Viewport() {
        setDimensions();
    }

    public static Viewport get() {
        return INSTANCE;
    }

    private void setDimensions() {
        x = PROJECT_VIEWPORT.x.get();
        y = PROJECT_VIEWPORT.y.get();
        width = PROJECT_VIEWPORT.width.get();
        height = PROJECT_VIEWPORT.height.get();
    }

    public void regen() {
        setDimensions();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final Project p = ProjectManager.get().getProject();
        if (p == null)
            return;

        // TODO - check if mouse position in bounds and ping cursor based on tool
    }

    @Override
    public void update(final double deltaTime) {
        final Project p = ProjectManager.get().getProject();
        if (p == null)
            return;

        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        final Project p = ProjectManager.get().getProject();
        if (p == null)
            return;

        canvas.draw(draw(p), x, y);
    }

    private GameImage draw(final Project p) {
        final GameImage viewport = new GameImage(width, height);

        viewport.fill(systemColor(MID));

        // TODO

        return viewport.submit();
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
