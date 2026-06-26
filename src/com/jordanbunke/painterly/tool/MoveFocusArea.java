package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;

public final class MoveFocusArea extends Tool {
    private static final MoveFocusArea INSTANCE;

    static {
        INSTANCE = new MoveFocusArea();
    }

    // TODO - fields

    private MoveFocusArea() {

    }

    public static MoveFocusArea get() {
        return INSTANCE;
    }

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        // TODO
    }

    @Override
    public void process(final Coord2D mousePos, final Project p) {
        // TODO
    }

    @Override
    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {
        // TODO
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        // TODO
    }

    @Override
    public void deselect() {
        // TODO
    }

    @Override
    public void drawOverlay(
            final GameImage viewportCanvas, final Project p,
            final int x, final int y, final int w, final int h
    ) {
        // TODO
    }
}
