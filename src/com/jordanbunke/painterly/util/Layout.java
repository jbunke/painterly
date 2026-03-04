package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.settings.Settings;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_FULLSCREEN;

import java.awt.*;
import java.util.function.Supplier;

public final class Layout {
    // layout constants

    public static final int
            TOOLTIP_OFFSET_LEFT = -7,
            TOOLTIP_OFFSET_RIGHT = 5,
            MENU_BAR_HEIGHT = 20,
            CONTEXT_BAR_HEIGHT = 20;

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

    // screen boxes

    public enum ScreenBox {
        SCREEN(() -> 0, () -> 0, Layout::width, Layout::height),
        MENU_BAR(() -> 0, () -> 0, Layout::width, () -> MENU_BAR_HEIGHT),
        CONTEXT_BAR(() -> 0, () -> Layout.height() - CONTEXT_BAR_HEIGHT,
                Layout::width, () -> CONTEXT_BAR_HEIGHT),
        ;

        public final Supplier<Integer> x, y, width, height;

        ScreenBox(
                final Supplier<Integer> x, final Supplier<Integer> y,
                final Supplier<Integer> width,
                final Supplier<Integer> height
        ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Coord2D pos() {
            return new Coord2D(x.get(), y.get());
        }

        public Bounds2D dims() {
            return new Bounds2D(width.get(), height.get());
        }

        public Coord2D at(
                final double percX, final double percY
        ) {
            return new Coord2D(atX(percX), atY(percY));
        }

        public int atX(final double percentage) {
            return x.get() + (int)(percentage * width.get());
        }

        public int atY(final double percentage) {
            return y.get() + (int)(percentage * height.get());
        }
    }
}
