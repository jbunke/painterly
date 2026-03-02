package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.painterly.settings.Settings;

import java.awt.*;

public final class Layout {
    // TODO - layout constants

    // TODO - constant processing functions

    // mutable fields
    private static Bounds2D size;

    static {
        determineSize();
    }

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
        final Boolean unboxed = Settings.get(Settings.SET_ID_FULLSCREEN, Boolean.class);

        return unboxed != null && unboxed;
    }
}
