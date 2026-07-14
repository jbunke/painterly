package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.viewport.Viewport;

public final class Zoom extends Tool {
    private static final Zoom INSTANCE;

    static {
        INSTANCE = new Zoom();
    }

    private Zoom() {}

    public static Zoom get() {
        return INSTANCE;
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        if (mouseInViewport)
            Cursor.ping(Cursor.ZOOM);
    }

    @Override
    public void deselect() {}

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        final Viewport v = Viewport.get();
        final Coord2D mousePosInViewport =
                mouseEvent.mousePosition.displace(-v.getX(), -v.getY()),
                targetPixel = getTargetPixel(mouseEvent.mousePosition, p);

        if (p.positioning.isTargetPixelValid(targetPixel)) {
            final boolean in = mouseEvent.button != GameMouseEvent.Button.RIGHT;
            p.positioning.clickZoom(in, mousePosInViewport);
        }
    }
}
