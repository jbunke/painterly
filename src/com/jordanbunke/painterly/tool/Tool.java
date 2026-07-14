package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.viewport.Viewport;

public abstract class Tool {
    /**
     * Each tool must implement its own frame-by-frame cursor update logic.
     * Implementations are permitted to update cursors via ping
     * ({@link Cursor#ping(Cursor)}) or force ({@link Cursor#force(Cursor)})
     * depending on need.
     *
     * @param mouseInViewport   Passed for instances where cursor update should
     *                          only occur if the mouse position is inside the
     *                          viewport
     * */
    public abstract void updateCursor(final boolean mouseInViewport);

    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {

    }

    /**
     * @param mouseEvent    Should be marked as processed by implementing
     *                      methods iff mouse up event captures meaningful
     *                      tool behaviour.
     * */
    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {

    }

    public void onMouseClick(final GameMouseEvent mouseEvent, final Project p) {

    }

    public void process(final Coord2D mousePos, final Project p) {

    }

    /**
     * Abort tool behaviour if tool is deselected during behaviour
     * (i.e. between mouse-down and mouse-up events)
     * */
    public abstract void deselect();

    public void drawOverlay(
            final GameImage viewportCanvas, final Project p,
            final int x, final int y, final int w, final int h
    ) {

    }

    // HELPERS

    Coord2D getTargetPixel(final Coord2D mousePos, final Project p) {
        final Viewport v = Viewport.get();
        final Coord2D mousePosInViewport =
                mousePos.displace(-v.getX(), -v.getY());

        return p.positioning.determineTargetPixel(mousePosInViewport);
    }

    Coord2D getScreenPixel(final Coord2D projectPixel, final Project p) {
        return p.positioning.determineScreenPixel(projectPixel);
    }
}
