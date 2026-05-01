package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.settings.Settings;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_FULLSCREEN;

import java.awt.*;
import java.util.function.Supplier;

public final class Layout {
    // layout constants

    public static final int
            DEBUG_EDGE_MARGIN = 10,
            TOOLTIP_OFFSET_LEFT = -12,
            TOOLTIP_OFFSET_RIGHT = 8,
            MENU_BAR_HEIGHT = 36,
            CONTEXT_BAR_HEIGHT = 36,
            KEY_SHORTCUT_INTERVAL_X = 4,
            KEY_SHORTCUT_TEXT_MARGIN_X = 6,
            KEY_SHORTCUT_SHADOW_MARGIN_X = 10,
            KEY_SHORTCUT_CORNER_MARGIN_X = 6,
            KEY_SHORTCUT_DROP_SHADOW = 4,
            KEY_SHORTCUT_DROP_SHADOW_EXTRA = 8,
            KEY_SHORTCUT_MAX_CLEARED = 4,
            TEXT_BUTTON_DEF_HEIGHT = 32,
            TEXT_BUTTON_PADDING_X = 20,
            TEXT_BUTTON_INTERVAL_L_Y = 32,
            TEXT_BUTTON_INTERVAL_S_Y = 16,
            TEXT_BUTTON_MARGIN_X = 6,
            TOOLTIP_LINE_INC_Y = 24,
            TOOLTIP_PADDING_X = 8,
            TOOLTIP_INITIAL_OFFSET_Y = 2;

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
        final Boolean unboxed = Settings.get(SET_ID_FULLSCREEN, Boolean.class);

        return unboxed != null && unboxed;
    }

    // screen boxes

    public enum ScreenBox {
        SCREEN(() -> 0, () -> 0, Layout::width, Layout::height),
        PROJECT_WINDOW(() -> 0, () -> MENU_BAR_HEIGHT, Layout::width,
                () -> Layout.height() - (MENU_BAR_HEIGHT + CONTEXT_BAR_HEIGHT)),
        MENU_BAR(() -> 0, () -> 0, Layout::width, () -> MENU_BAR_HEIGHT,
                MenuAssembly::menuBar),
        CONTEXT_BAR(() -> 0, () -> Layout.height() - CONTEXT_BAR_HEIGHT,
                Layout::width, () -> CONTEXT_BAR_HEIGHT,
                MenuAssembly::contextBar),
        ;

        public final Supplier<Integer> x, y, width, height;
        private final Supplier<Menu> menuBuilder;
        private Menu menu;

        static {
            for (ScreenBox sb : ScreenBox.values())
                sb.menu = sb.menuBuilder.get();
        }

        ScreenBox(
                final Supplier<Integer> x, final Supplier<Integer> y,
                final Supplier<Integer> width,
                final Supplier<Integer> height
        ) {
            this(x, y, width, height, Menu::new);
        }

        ScreenBox(
                final Supplier<Integer> x, final Supplier<Integer> y,
                final Supplier<Integer> width,
                final Supplier<Integer> height,
                final Supplier<Menu> menuBuilder
        ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.menuBuilder = menuBuilder;
            // menu = menuBuilder.get();
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

        public Coord2D offset(
                final int offsetX, final int offsetY
        ) {
            return new Coord2D(offsetX(offsetX), offsetY(offsetY));
        }

        public int offsetX(final int offset) {
            return x.get() + offset;
        }

        public int offsetY(final int offset) {
            return y.get() + offset;
        }

        public int ofWidth(final double percentage) {
            return (int)(percentage * width.get());
        }

        public int ofHeight(final double percentage) {
            return (int)(percentage * height.get());
        }

        public Menu menu() {
            return menu;
        }

        public Menu regenMenu() {
            menu = menuBuilder.get();
            return menu;
        }

        public static boolean isRendered(final ScreenBox screenBox) {
            return screenBox != SCREEN;
        }
    }
}
