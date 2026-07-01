package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.flow.Workspace;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.menu.elements.complex.context_bar.ContextBar;
import com.jordanbunke.painterly.menu.elements.complex.context_bar.ContextBarSection;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.MenuBar;
import com.jordanbunke.painterly.menu.elements.complex.project_bar.ProjectBar;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.viewport.Viewport;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_FULLSCREEN;

import java.awt.*;
import java.util.function.Supplier;

public final class Layout {
    // layout constants

    public static final int
            DEBUG_EDGE_MARGIN = 10,
            FPS_ALLOTTED_WIDTH = 80,
            MENU_BAR_PROJECT_BAR_GAP_WIDTH = 80,
            MIN_PROJECT_BUTTONS_TO_RENDER = 5,
            DIALOG_MARGIN = 10,
            DIALOG_CONTENT_MIN_HEIGHT = 30, // TODO - test
            DIALOG_CONTENT_TOP_OFFSET_Y = 50, // TODO - test
            DIALOG_MIN_SCREEN_HEIGHT_DIFF = 200, // TODO - test
            DIALOG_RESOLUTION_BUTTON_WIDTH = 120, // TODO - test
            DIALOG_ROW_INCREMENT = 45, // TODO - test
            ICON_DIM = 24, // TODO - test
            SLIDER_HEIGHT = ICON_DIM,
            SLIDER_DEF_WIDTH = 100,
            SLIDER_BALL_DIM = ICON_DIM,
            SLIDER_SHELL_HEIGHT = SLIDER_HEIGHT - 8,
            TOOLTIP_OFFSET_LEFT = -12,
            TOOLTIP_OFFSET_RIGHT = 8,
            MENU_BAR_DIVIDER_WIDTH = 50, // TODO - test
            MENU_BAR_HEIGHT = 32,
            MENU_BAR_PADDING_X = 10, // TODO - test
            MENU_BAR_SEPARATOR_HEIGHT = 1,
            CONTEXT_BAR_HEIGHT = 32, // TODO - potentially expand for icons
            CONTEXT_BAR_PADDING_X = 8,
            KEY_SHORTCUT_INTERVAL_X = 4,
            KEY_SHORTCUT_TEXT_MARGIN_X = 6,
            KEY_SHORTCUT_SHADOW_MARGIN_X = 10,
            KEY_SHORTCUT_CORNER_MARGIN_X = 6,
            KEY_SHORTCUT_DROP_SHADOW = 4,
            KEY_SHORTCUT_DROP_SHADOW_EXTRA = 8,
            KEY_SHORTCUT_MAX_CLEARED = 4,
            STANDARD_FOLLOW_X = 10, // TODO - test
            TEXTBOX_DEF_WIDTH = 200,
            TEXTBOX_SEG_INC = 1, // TODO - test; copied from TDSM
            TEXT_BUTTON_AFTER_LABEL_OFFSET_Y = -5,
            TEXT_BUTTON_DEF_HEIGHT = 32, // TODO - better encapsulation
            TEXT_BUTTON_PADDING_X = 20,
            TEXT_BUTTON_INTERVAL_L_Y = 32,
            TEXT_BUTTON_INTERVAL_S_Y = 16,
            TEXT_BUTTON_TEXT_OFFSET_X = 6,
            TEXT_BUTTON_TEXT_OFFSET_Y = 5,
            TOOLTIP_LINE_INC_Y = 24,
            TOOLTIP_PADDING_X = 8,
            TOOLTIP_INITIAL_OFFSET_Y = 2;

    public static final double
            FIT_TO_SCREEN_RATIO = 0.8;

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

    public static void toggleFullscreen() {
        Settings.set(SET_ID_FULLSCREEN, !isFullscreen());

        onResize();
    }

    private static void onResize() {
        determineSize();
        Painterly.get().remakeWindow();

        regenAll();
    }

    private static void regenAll() {
        MenuBar.regen();
        ContextBar.regen();
        ProjectBar.regen();
        DialogManager.regen();
        ProgramState.regen();
        Workspace.get().regen();
        Viewport.get().regen();
        EnumUtils.stream(ScreenBox.class).forEach(ScreenBox::regenBackground);
    }

    // dialog boxes

    public static int defaultDialogWidth() {
        return width() / 2;
    }

    public static int defaultDialogHeight() {
        return height() / 2;
    }

    public static int maxDialogHeight() {
        return height() - DIALOG_MIN_SCREEN_HEIGHT_DIFF;
    }

    public static int dialogBottomHeight() {
        return TEXT_BUTTON_DEF_HEIGHT + (int)(2.5 * DIALOG_MARGIN);
    }

    public static int dialogTitleStripeHeight() {
        return DIALOG_CONTENT_TOP_OFFSET_Y - DIALOG_MARGIN;
    }

    // screen boxes

    public enum ScreenBox implements ProgramContext {
        SCREEN(() -> 0, () -> 0, Layout::width, Layout::height),
        PROJECT_VIEWPORT(() -> 0, () -> MENU_BAR_HEIGHT, Layout::width,
                () -> Layout.height() - (MENU_BAR_HEIGHT + CONTEXT_BAR_HEIGHT)),
        MENU_BAR(() -> 0, () -> 0, Layout::width, () -> MENU_BAR_HEIGHT,
                MenuAssembly::menuBar),
        CONTEXT_BAR(() -> 0, () -> Layout.height() - CONTEXT_BAR_HEIGHT,
                Layout::width, () -> CONTEXT_BAR_HEIGHT,
                MenuAssembly::contextBar),
        ;

        public final Supplier<Integer> x, y, width, height;
        private final Supplier<Menu> bgBuilder;
        private Menu background;

        static {
            for (ScreenBox sb : ScreenBox.values())
                sb.background = sb.bgBuilder.get();
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
                final Supplier<Menu> bgBuilder
        ) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.bgBuilder = bgBuilder;
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
            return background;
        }

        public void regenBackground() {
            background = bgBuilder.get();
        }

        public static boolean isRendered(final ScreenBox screenBox) {
            return screenBox != SCREEN;
        }

        @Override
        public void process(final InputEventLogger eventLogger) {
            if (this == PROJECT_VIEWPORT && ProjectManager.get().hasProject())
                Viewport.get().process(eventLogger);
        }

        @Override
        public void update(final double deltaTime) {
            if (this == PROJECT_VIEWPORT && ProjectManager.get().hasProject())
                Viewport.get().update(deltaTime);
        }

        @Override
        public void render(final GameImage canvas) {
            background.render(canvas);

            if (this == PROJECT_VIEWPORT && ProjectManager.get().hasProject())
                Viewport.get().render(canvas);
        }

        @Override
        public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
    }

    // misc.

    public static int defaultContextBarElementWidth() {
        return ScreenBox.CONTEXT_BAR.width.get() / ContextBarSection.values().length;
    }

    public static Coord2D follow(final MenuElement ref, final int bufferX) {
        return ref.getRenderPosition().displaceX(ref.getWidth() + bufferX);
    }

    public static Coord2D follow(final MenuElement ref) {
        return follow(ref, 0);
    }
}
