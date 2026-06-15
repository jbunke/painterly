package com.jordanbunke.painterly.tool;

import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.viewport.Viewport;

public final class Hand extends Tool {
    private static final Hand INSTANCE;

    static {
        INSTANCE = new Hand();
    }

    private boolean panning;
    /*
    * TODO - Call deselect on current tool when active project changes;
    *  rendering this field unnecessary
    * */
    private Project project;
    private Coord2D initMousePos;
    private double initAnchorRatioX, initAnchorRatioY;

    private Hand() {
        panning = false;
        project = null;
        initMousePos = new Coord2D();
        initAnchorRatioX = 0d;
        initAnchorRatioY = 0d;
    }

    public static Hand get() {
        return INSTANCE;
    }

    @Override
    public void updateCursor(final boolean mouseInViewport) {
        if (mouseInViewport)
            Cursor.ping(panning ? Cursor.HAND_GRAB : Cursor.HAND_OPEN);
    }

    @Override
    public void onMouseDown(final GameMouseEvent mouseEvent, final Project p) {
        panning = true;
        project = p;
        initMousePos = mouseEvent.mousePosition;
        initAnchorRatioX = Viewport.get().getPositioning().getAnchorRatioX();
        initAnchorRatioY = Viewport.get().getPositioning().getAnchorRatioY();
    }

    @Override
    public void process(final Coord2D mousePos, final Project p) {
        if (panning) {
            if (!p.equals(project)) {
                panning = false;
                return;
            }

            final int mouseDX = mousePos.x - initMousePos.x,
                    mouseDY = mousePos.y - initMousePos.y;

            Viewport.get().getPositioning().pan(p, mouseDX,
                    mouseDY, initAnchorRatioX, initAnchorRatioY);
        }
    }

    @Override
    public void onMouseUp(final GameMouseEvent mouseEvent, final Project p) {
        if (panning) {
            panning = false;
            mouseEvent.markAsProcessed();
        }
    }

    @Override
    public void deselect() {
        panning = false;
    }
}
