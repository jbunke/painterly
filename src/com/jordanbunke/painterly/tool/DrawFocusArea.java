package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.viewport.Positioning;

import static com.jordanbunke.painterly.viewport.VisualMath.projectPosition;

public final class DrawFocusArea extends Tool {
    private static final DrawFocusArea INSTANCE;

    static {
        INSTANCE = new DrawFocusArea();
    }

    private boolean selecting;
    private Coord2D pivot, complement, tl, br;

    private DrawFocusArea() {
        selecting = false;
        pivot = Positioning.INVALID;
        complement = Positioning.INVALID;
        tl = Positioning.INVALID;
        br = Positioning.INVALID;

        // TODO
    }

    public static DrawFocusArea get() {
        return INSTANCE;
    }

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        final Coord2D targetPixel = getTargetPixel(mouseEvent.mousePosition, p);

        selecting = Positioning.isTargetPixelValid(targetPixel);

        if (selecting) {
            pivot = targetPixel;
            complement = targetPixel;
            updateTLBR();
        }
    }

    @Override
    public void process(final Coord2D mousePos, final Project p) {
        if (selecting) {
            final Coord2D targetPixel = getTargetPixel(mousePos, p);

            if (Positioning.isTargetPixelValid(targetPixel)) {
                complement = targetPixel;
                updateTLBR();
            }
        }
    }

    @Override
    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {
        if (selecting) {
            // TODO - send focus area to focus manager

            selecting = false;
            mouseEvent.markAsProcessed();
        }
    }

    @Override
    public void deselect() {
        selecting = false;
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        // TODO
    }

    @Override
    public void drawOverlay(
            final GameImage viewportCanvas, final Project p,
            final int x, final int y, final int w, final int h
    ) {
        if (selecting) {
            final Coord2D tlRenderPos = projectPosition(tl.x, tl.y,
                    p.width, p.height, x, y, w, h),
                    brRenderPos = projectPosition(br.x, br.y,
                            p.width, p.height, x, y, w, h);
            final int rx = tlRenderPos.x, ry = tlRenderPos.y,
                    rw = brRenderPos.x - rx, rh = brRenderPos.y - ry;

            // TODO - rendering is temp

            viewportCanvas.drawRectangle(Colors.purple(), 2f, rx, ry, rw, rh);
        }
    }

    // TODO - implementation

    private void updateTLBR() {
        tl = new Coord2D(Math.min(pivot.x, complement.x),
                Math.min(pivot.y, complement.y));
        br = new Coord2D(Math.max(pivot.x, complement.x),
                Math.max(pivot.y, complement.y));
    }
}
