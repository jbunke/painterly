package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.viewport.Positioning;

public final class MoveFocusArea extends Tool {
    private static final MoveFocusArea INSTANCE;

    private static final int STRETCH_PX_THRESHOLD = 4;

    static {
        INSTANCE = new MoveFocusArea();
    }

    private TransformInfo is, would;
    private Coord2D start, last;

    private MoveFocusArea() {
        is = TransformInfo.dummy();
        would = TransformInfo.dummy();
        start = Positioning.INVALID;
        last = Positioning.INVALID;
    }

    public static MoveFocusArea get() {
        return INSTANCE;
    }

    private enum TransformType {
        NONE, MOVE, STRETCH
    }

    private enum Pivot {
        TOP, LEFT, BOTTOM, RIGHT, TL, TR, BL, BR;

        Cursor getCursor() {
            return switch (this) {
                case TL, BR -> Cursor.MOVE_TL_BR;
                case TR, BL -> Cursor.MOVE_TR_BL;
                // TODO - consider dedicated
                case LEFT, RIGHT -> Cursor.HORZ_SCROLL;
                case TOP, BOTTOM -> Cursor.VERT_SCROLL;
            };
        }
    }

    private record TransformInfo(TransformType type, Pivot pivot) {
        static TransformInfo dummy() {
            return new TransformInfo(TransformType.NONE, null);
        }

        boolean exists() {
            return type != TransformType.NONE;
        }

        boolean hasPivot() {
            return pivot != null;
        }

        Cursor getCursor() {
            return switch (type) {
                case NONE -> Cursor.MAIN;
                case MOVE -> Cursor.MOVE_SELECTION;
                case STRETCH -> hasPivot() ? pivot.getCursor() : Cursor.MAIN;
            };
        }
    }

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        final Coord2D targetPixel = getTargetPixel(mouseEvent.mousePosition, p);

        // TODO
    }

    @Override
    public void process(final Coord2D mousePos, final Project p) {
        // TODO
        switch (is.type) {
            case NONE -> {
                would = determineHover(p, mousePos);
            }
            case STRETCH -> {

            }
            case MOVE -> {

            }
        }
    }

    @Override
    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {
        // TODO
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        final Cursor cursor = (is.exists() ? is : would).getCursor();

        if (is.exists())
            Cursor.force(cursor);
        else if (mouseInViewport)
            Cursor.ping(cursor);
    }

    @Override
    public void deselect() {
        is = TransformInfo.dummy();
    }

    @Override
    public void drawOverlay(
            final GameImage viewportCanvas, final Project p,
            final int x, final int y, final int w, final int h
    ) {
        // TODO
    }

    private TransformInfo determineHover(
            final Project p, final Coord2D mousePos
    ) {
        final RectBounds focusArea = p.focusManager.getFocusArea();
        final Coord2D tl = getScreenPixel(
                new Coord2D(focusArea.left(), focusArea.top()), p),
                br = getScreenPixel(
                        new Coord2D(focusArea.right(), focusArea.bottom()), p),
                tr = new Coord2D(br.x, tl.y),
                bl = new Coord2D(tl.x, br.y);

        if (onCorner(mousePos, tl))
            return new TransformInfo(TransformType.STRETCH, Pivot.TL);
        else if (onCorner(mousePos, tr))
            return new TransformInfo(TransformType.STRETCH, Pivot.TR);
        else if (onCorner(mousePos, br))
            return new TransformInfo(TransformType.STRETCH, Pivot.BR);
        else if (onCorner(mousePos, bl))
            return new TransformInfo(TransformType.STRETCH, Pivot.BL);
        else if (onLine(mousePos, tl, tr))
            return new TransformInfo(TransformType.STRETCH, Pivot.TOP);
        else if (onLine(mousePos, tr, br))
            return new TransformInfo(TransformType.STRETCH, Pivot.RIGHT);
        else if (onLine(mousePos, bl, br))
            return new TransformInfo(TransformType.STRETCH, Pivot.BOTTOM);
        else if (onLine(mousePos, tl, bl))
            return new TransformInfo(TransformType.STRETCH, Pivot.LEFT);

        return new TransformInfo(TransformType.MOVE, null);
    }

    private boolean onCorner(final Coord2D a, final Coord2D b) {
        return Math.abs(a.x - b.x) <= STRETCH_PX_THRESHOLD &&
                Math.abs(a.y - b.y) <= STRETCH_PX_THRESHOLD;
    }

    private boolean onLine(
            final Coord2D mousePos, final Coord2D l0, final Coord2D l1
    ) {
        final boolean horizontal = l0.y == l1.y;
        final int mx = mousePos.x, my = mousePos.y,
                lx0 = Math.min(l0.x, l1.x), lx1 = Math.max(l0.x, l1.x),
                ly0 = Math.min(l0.y, l1.y), ly1 = Math.max(l0.y, l1.y);

        if (horizontal)
            return mx >= lx0 && mx <= lx1 &&
                    Math.abs(my - ly0) <= STRETCH_PX_THRESHOLD;

        return my >= ly0 && my <= ly1 &&
                Math.abs(mx - lx0) <= STRETCH_PX_THRESHOLD;
    }
}
