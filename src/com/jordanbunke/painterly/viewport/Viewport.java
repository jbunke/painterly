package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.events.GameMouseScrollEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.core.paint.BrushStroke;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.theme.Colors;
import com.jordanbunke.painterly.util.debug.LogChannel;
import com.jordanbunke.painterly.util.debug.LogManager;

import java.awt.*;
import java.util.List;

import static com.jordanbunke.painterly.theme.Colors.systemColor;
import static com.jordanbunke.painterly.theme.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.PROJECT_VIEWPORT;
import static com.jordanbunke.painterly.viewport.VisualMath.*;

public final class Viewport implements ProgramContext {
    private static final Viewport INSTANCE;

    // viewport screen coordinates and dimensions
    private int x, y, width, height;

    private Project lastProject;
    private Positioning positioning;

    static {
        INSTANCE = new Viewport();
    }

    private Viewport() {
        setDimensions();

        lastProject = null;
        positioning = new Positioning();
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

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        final boolean mouseInBounds =
                mousePos.x >= x && mousePos.x < x + width &&
                        mousePos.y >= y && mousePos.y < y + height;

        // non-tool misc. mouse actions
        processMouseActions(eventLogger, p, mousePos, mouseInBounds);

        // process tool
        ToolManager.getCurrentTool().process(mousePos, p);

        // tool considerations for cursor ping or force
        ToolManager.getCurrentTool().updateCursor(mouseInBounds);
    }

    // Note:    Project actions driven by key inputs are processed in the
    //          Workspace class
    private void processMouseActions(
            final InputEventLogger eventLogger, final Project p,
            final Coord2D mousePos, final boolean mouseInBounds
    ) {
        final List<GameEvent> events = eventLogger.getUnprocessedEvents();

        for (GameEvent event : events) {
            if (event instanceof GameMouseEvent me) {
                switch (me.action) {
                    case DOWN -> {
                        if (mouseInBounds) {
                            ToolManager.getCurrentTool().onMouseDown(me, p);
                            me.markAsProcessed();
                        }
                    }
                    case CLICK -> {
                        if (mouseInBounds) {
                            ToolManager.getCurrentTool().onMouseClick(me, p);
                            me.markAsProcessed();
                        }
                    }
                    // Invocation not dependent on mouse being in bounds, and
                    // mouse event not necessarily marked as processed.
                    case UP -> ToolManager.getCurrentTool().onMouseUp(me, p);
                }
            }
            else if (event instanceof GameMouseScrollEvent mse) {
                mse.markAsProcessed();

                if (KeyboardShortcut.areModKeysPressed(true, true, eventLogger))
                    p.focusManager.augmentDivsX(-mse.clicksScrolled);
                else if (KeyboardShortcut.areModKeysPressed(false, true, eventLogger))
                    p.focusManager.augmentDivsY(-mse.clicksScrolled);
                else
                    positioning.scrollZoom(mse.clicksScrolled < 0 /* TODO - account for inverse scroll zoom setting */,
                            p, mouseInBounds, mousePos.displace(-x, -y));
            }
        }
    }

    @Override
    public void update(final double deltaTime) {
        final Project p = ProjectManager.get().getProject();
        if (p == null)
            return;

        if (!p.equals(lastProject)) {
            lastProject = p;
            positioning = new Positioning();
        }
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        final Project p = ProjectManager.get().getProject();
        if (p == null)
            return;

        canvas.draw(draw(p), x, y);

        if (LogManager.isChannelActive(LogChannel.RECENT_STROKE_ATTEMPTS))
            canvas.draw(debugDraw(p), x, y);
    }

    private GameImage draw(final Project p) {
        final GameImage viewport = new GameImage(width, height);

        viewport.fill(systemColor(MID));

        positioning.draw(viewport, p);

        return viewport.submit();
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    private GameImage debugDraw(final Project p) {
        final GameImage debugOverlay = new GameImage(width, height);

        positioning.draw(debugOverlay, p.width, p.height,
                (x, y, w, h) -> {
            final List<BrushStroke> recent = p.debugData.getRecentStrokes();

            for (BrushStroke stroke : recent) {
                final Color successColor = stroke.wasAccepted()
                        ? Colors.success() : Colors.failure(),
                        edgeColor = stroke.alongEdge
                                ? Colors.purple() : successColor;
                final RectBounds box = stroke.affectedArea(p.width, p.height);

                // TODO - change once stroke is spline

                final Coord2D tl = projectPosition(box.left(),
                        box.top(), p.width, p.height, x, y, w, h),
                        br = projectPosition(box.right(),
                                box.bottom(), p.width, p.height, x, y, w, h),
                        start = projectPosition(stroke.position.x,
                                stroke.position.y, p.width, p.height,
                                x, y, w, h),
                        end = projectPosition(stroke.endPosition.x,
                                stroke.endPosition.y, p.width, p.height,
                                x, y, w, h);

                debugOverlay.drawRectangle(successColor, 2f,
                        tl.x, tl.y, br.x - tl.x, br.y - tl.y);
                debugOverlay.drawLine(edgeColor, 2f,
                        start.x, start.y, end.x, end.y);
            }
        });

        return debugOverlay.submit();
    }

    public Positioning getPositioning() {
        return positioning;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
