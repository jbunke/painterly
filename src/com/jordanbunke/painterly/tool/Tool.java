package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Cursor;

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

    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {

    }

    public void onMouseClick(final GameMouseEvent mouseEvent, final Project p) {

    }

    public void process(final Coord2D mousePos, final Project p) {

    }
}
