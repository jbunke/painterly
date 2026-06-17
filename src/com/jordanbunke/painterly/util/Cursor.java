package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public enum Cursor {
    UNASSIGNED, MAIN, POINTER, RETICLE, TEXT,
    VERT_SCROLL, HORZ_SCROLL, HAND_OPEN, HAND_GRAB, NONE;

    public String id() {
        return name().toLowerCase();
    }

    private static Cursor cursor;
    private static Coord2D mousePos;

    private final GameImage image;

    static {
        cursor = UNASSIGNED;
        mousePos = new Coord2D();
    }

    Cursor() {
        image = Graphics.readCursor(this);
    }

    public static void reset(final Coord2D mousePos) {
        cursor = UNASSIGNED;
        Cursor.mousePos = mousePos;
    }

    public static void ping(final Cursor cursor) {
        if (Cursor.cursor == UNASSIGNED)
            Cursor.cursor = cursor;
    }

    public static void force(final Cursor cursor) {
        Cursor.cursor = cursor;
    }

    public static void render(final GameImage canvas) {
        // Set to main cursor if unassigned
        ping(MAIN);

        final Coord2D crp = Layout.cursorRenderPos(cursor.image, mousePos);
        canvas.draw(cursor.image, crp.x, crp.y);
    }

    public static Cursor get() {
        return cursor;
    }
}
