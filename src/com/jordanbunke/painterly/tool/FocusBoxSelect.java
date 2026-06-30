package com.jordanbunke.painterly.tool;

import com.jordanbunke.color_proc.ColorProc;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.util.Cursor;

import static com.jordanbunke.painterly.util.Graphics.drawBoxOverlay;

public final class FocusBoxSelect extends Tool {
    private static final FocusBoxSelect INSTANCE;

    private static final int INVALID = -1;

    static {
        INSTANCE = new FocusBoxSelect();
    }

    private int x, y;
    private RectBounds bounds;

    private FocusBoxSelect() {
        reset();
    }

    public static FocusBoxSelect get() {
        return INSTANCE;
    }

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        final Coord2D mousePos = mouseEvent.mousePosition;
        updateCoordinates(mousePos, p);

        if (validCoordinate()) {
            p.focusManager.setX(x);
            p.focusManager.setY(y);
        }
    }

    @Override
    public void process(final Coord2D mousePos, final Project p) {
        updateCoordinates(mousePos, p);
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        if (mouseInViewport && validCoordinate())
            Cursor.ping(Cursor.POINTER);
    }

    @Override
    public void deselect() {
        reset();
    }

    @Override
    public void drawOverlay(
            final GameImage viewportCanvas, final Project p,
            final int x, final int y, final int w, final int h
    ) {
        if (bounds != null)
            drawBoxOverlay(viewportCanvas, bounds, p,
                    ColorProc.RGB_SCALE, x, y, w, h);
    }

    private void updateCoordinates(final Coord2D mousePos, final Project p) {
        final Coord2D targetPixel = getTargetPixel(mousePos, p);
        final RectBounds focusArea = p.focusManager.getFocusArea();

        if (focusArea.isInside(targetPixel)) {
            final Coord2D faPos = targetPixel.displace(
                    -focusArea.left(), -focusArea.top());
            final int divsX = p.focusManager.getDivsX(),
                    divsY = p.focusManager.getDivsY(),
                    w = focusArea.width(),
                    h = focusArea.height();

            x = (int)((faPos.x / (double) w) * divsX);
            y = (int)((faPos.y / (double) h) * divsY);
            bounds = p.focusManager.bounds(x, y);
        } else
            reset();
    }

    private boolean validCoordinate() {
        return x >= 0 && y >= 0;
    }

    private void reset() {
        x = INVALID;
        y = INVALID;
        bounds = null;
    }
}
