package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.settings.Settings;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_FULLSCREEN;

import java.awt.*;

public final class Layout {
    // layout constants

    public static final int
            TOOLTIP_OFFSET_LEFT = -7,
            TOOLTIP_OFFSET_RIGHT = 5;

    // constant processing functions

    public static Coord2D tooltipRenderPos(
            final GameImage tooltip, final Coord2D mousePos
    ) {
        final boolean left = mousePos.x <= width() / 2,
                top = mousePos.y <= height() / 2;
        final int w = tooltip.getWidth(), h = tooltip.getHeight(),
                x = mousePos.x - (left ? TOOLTIP_OFFSET_LEFT
                        : w + TOOLTIP_OFFSET_RIGHT),
                y = mousePos.y - (top ? 0 : h);

        return new Coord2D(x, y);
    }

    public static Coord2D cursorRenderPos(
            final GameImage cursor, final Coord2D mousePos
    ) {
        final int w = cursor.getWidth(), h = cursor.getHeight();
        return mousePos.displace(new Coord2D(w / 2, h / 2).scale(-1));
    }

    // mutable fields

    private static Bounds2D size;

    // startup

    static {
        determineSize();
    }

    // mutable processing functions

    private static void determineSize() {
        final boolean fullscreen = isFullscreen();

        if (fullscreen) {
            final Dimension screenDims = Toolkit.getDefaultToolkit().getScreenSize();
            size = new Bounds2D(screenDims.width, screenDims.height);
        } else {
            // TODO - temporary
            final int width = 1600, height = 900;
            size = new Bounds2D(width, height);
        }
    }

    public static int width() {
        return size.width();
    }

    public static int height() {
        return size.height();
    }

    public static boolean isFullscreen() {
        final Boolean unboxed = Settings.get(SET_ID_FULLSCREEN.get(), Boolean.class);

        return unboxed != null && unboxed;
    }
}
