package com.jordanbunke.painterly.theme;

import com.jordanbunke.color_proc.ColorProc;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.ProgramFont.FontFormatter;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.theme.Colors.*;
import static com.jordanbunke.painterly.theme.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.*;
import static com.jordanbunke.painterly.viewport.VisualMath.projectPosition;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors"),
            MISC_IMG_FOLDER = Path.of("img_misc");

    public static final GameImage ICON;
    private static final GameImage CONTROL_POINT, HIGHLIGHT;

    static {
        ICON = readMiscImage(RC_ICON);
        CONTROL_POINT = readMiscImage(RC_CONTROL_POINT);
        HIGHLIGHT = readMiscImage(RC_HIGHLIGHT);
    }

    // IO

    public static GameImage readIcon(final ResourceCode code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.id() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    public static GameImage readCursor(final Cursor cursor) {
        final Path cursorFile = CURSORS_FOLDER.resolve(
                cursor.id() + ".png");
        return ResourceLoader.loadImageResource(cursorFile);
    }

    public static GameImage readMiscImage(final ResourceCode code) {
        final Path iconFile = MISC_IMG_FOLDER.resolve(code.id() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    // UI ELEMENTS

    public static void stampIcon(
            final GameImage image, final ResourceCode iconCode
    ) {
        if (iconCode == null || iconCode == RC_NA)
            return;

        final int h = image.getHeight(), y = (h - ICON_DIM) / 2;

        final GameImage icon = readIcon(iconCode);

        image.draw(icon, MENU_BAR_PADDING_X, y);
    }

    public static void stampText(
            final GameImage image, final TextButton tb,
            final Color textColor, final boolean hasIcon
    ) {
        final GameImage textImage = new FontFormatter(FONT_DEF).realize()
                .setColor(textColor).addText(tb.getLabel()).build().draw();
        final int w = tb.getWidth();

        final int x = switch (tb.getAlignment()) {
            case LEFT -> hasIcon
                    ? MENU_BAR_PADDING_X + ICON_DIM + MENU_BAR_PADDING_X
                    : TEXT_BUTTON_TEXT_OFFSET_X;
            case CENTER -> (w - textImage.getWidth()) / 2;
            case RIGHT -> w - (TEXT_BUTTON_TEXT_OFFSET_X + textImage.getWidth());
        };

        image.draw(textImage, x, TEXT_BUTTON_TEXT_OFFSET_Y);
    }

    public static GameImage drawCheckbox(
            final boolean highlighted, final boolean checked
    ) {
        // TODO - review
        final GameImage icon = readIcon(checked
                ? RC_CHECKED_TRUE : RC_CHECKED_FALSE);

        return highlighted ? highlightIcon(icon) : icon;
    }

    /**
     * Keep rerouting function here for readability in
     * {@link com.jordanbunke.painterly.menu.elements.textbox.Textbox}
     * */
    public static GameImage drawTextbox(
            final Bounds2D dims,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        return ThemeManager.get().drawTextbox(dims, prefix, text, suffix,
                cursorIndex, selectionIndex, valid, highlighted, typing);
    }

    public static GameImage drawKeyboardShortcut(final KeyboardShortcut shortcut) {
        final String[] stringArray = shortcut.asStringArray();

        return Arrays.stream(stringArray).map(Graphics::drawKey)
                .reduce((a, b) -> {
                    // here
                    final int bX = a.getWidth() + KEY_SHORTCUT_INTERVAL_X,
                            w = bX + b.getWidth(), h = a.getHeight();

                    final GameImage combined = new GameImage(w, h);
                    combined.draw(a);
                    combined.draw(b, bX, 0);

                    return combined.submit();
                }).orElse(GameImage.dummy());
    }

    public static GameImage drawKey(final String keyAsString) {
        // TODO - temp implementation

        final Color textColor = systemColor(LIGHT),
                backgroundColor = systemColor(DARK),
                accentColor = systemColor(MID_DARK);

        final GameImage textImage = new FontFormatter(FONT_DEF)
                .setTextSize(1.0)
                .setColor(textColor).realize().addText(keyAsString)
                .build().draw();

        final int w = textImage.getWidth() + (2 * KEY_SHORTCUT_TEXT_MARGIN_X),
                h = textImage.getHeight() + KEY_SHORTCUT_DROP_SHADOW;
        final GameImage key = new GameImage(w, h);

        key.fill(backgroundColor);

        int shadowY = h - KEY_SHORTCUT_DROP_SHADOW;
        key.fillRectangle(accentColor, 0, shadowY, w, KEY_SHORTCUT_DROP_SHADOW);

        // round out drop shadow
        keyShadowCurve(key, accentColor);

        // clear corners
        clearKeyCorners(key);

        key.draw(textImage, KEY_SHORTCUT_TEXT_MARGIN_X, 0);

        return key.submit();
    }

    private static void keyShadowCurve(
            final GameImage key, final Color accentColor
    ) {
        final int w = key.getWidth(), h = key.getHeight(),
                margin = KEY_SHORTCUT_SHADOW_MARGIN_X;

        for (int x = 0; x < margin; x++) {
            final int extraH = (int)(Math.pow((margin - x) / (double)margin, 3.) * KEY_SHORTCUT_DROP_SHADOW_EXTRA),
                    y = h - (KEY_SHORTCUT_DROP_SHADOW + extraH),
                    x2 = w - (x + 1);

            key.fillRectangle(accentColor, x, y, 1, extraH);
            key.fillRectangle(x2, y, 1, extraH);
        }
    }

    private static void clearKeyCorners(final GameImage key) {
        final int w = key.getWidth(), h = key.getHeight(),
                margin = KEY_SHORTCUT_CORNER_MARGIN_X;

        for (int x = 0; x < margin; x++) {
            final int ys = (int)(Math.pow((margin - x) / (double)margin, 3.) * KEY_SHORTCUT_MAX_CLEARED),
                    x2 = w - (x + 1);

            final int transparent = transparent().getRGB();
            for (int y = 0; y < ys; y++) {
                final int y2 = h - (y + 1);

                key.setRGB(x, y, transparent);
                key.setRGB(x2, y, transparent);
                key.setRGB(x, y2, transparent);
                key.setRGB(x2, y2, transparent);
            }
        }
    }

    // ADDITIONAL UI

    public static void drawViewportReticle(
            final GameImage viewportCanvas, final Coord2D mousePosInViewport
    ) {
        final int w = viewportCanvas.getWidth(),
                h = viewportCanvas.getHeight();
        final int x = mousePosInViewport.x,
                y = mousePosInViewport.y;

        if (x >= 0 && x < w && y >= 0 && y < h) {
            // TODO - consider different color
            final Color color = Colors.focusArea(ColorProc.RGB_SCALE);
            viewportCanvas.drawLine(color, 2f, x, 0, x, h);
            viewportCanvas.drawLine(color, 2f, 0, y, w, y);
        }
    }

    public static void drawAreaOverlay(
            final GameImage viewportCanvas, final RectBounds bounds,
            final Project project, final int opacity,
            final int x, final int y, final int w, final int h
    ) {
        drawBoundsOverlay(viewportCanvas, bounds, project,
                Colors.focusArea(opacity), x, y, w, h);
    }

    public static void drawBoxOverlay(
            final GameImage viewportCanvas, final RectBounds bounds,
            final Project project, final int opacity,
            final int x, final int y, final int w, final int h
    ) {
        drawBoundsOverlay(viewportCanvas, bounds, project,
                Colors.focusBox(opacity), x, y, w, h);
    }

    public static void drawBoundsOverlay(
            final GameImage viewportCanvas, final RectBounds bounds,
            final Project project, final Color color,
            final int x, final int y, final int w, final int h
    ) {
        final Coord2D tlRenderPos = projectPosition(
                bounds.left(), bounds.top(),
                project.width, project.height, x, y, w, h),
                brRenderPos = projectPosition(
                        bounds.right(), bounds.bottom(),
                        project.width, project.height, x, y, w, h);
        final int rx = tlRenderPos.x, ry = tlRenderPos.y,
                rw = brRenderPos.x - rx, rh = brRenderPos.y - ry;

        viewportCanvas.drawRectangle(color, 2f, rx, ry, rw, rh);
    }

    public static void drawControlPoints(
            final GameImage viewportCanvas,
            final RectBounds bounds, final Project project,
            final int x, final int y, final int w, final int h
    ) {
        final GameImage cp = CONTROL_POINT;

        final Coord2D tlRenderPos = projectPosition(
                bounds.left(), bounds.top(),
                project.width, project.height, x, y, w, h),
                brRenderPos = projectPosition(
                        bounds.right(), bounds.bottom(),
                        project.width, project.height, x, y, w, h);
        final int rx = tlRenderPos.x, ry = tlRenderPos.y,
                rw = brRenderPos.x - rx, rh = brRenderPos.y - ry,
                ox = cp.getWidth() / 2, oy = cp.getHeight() / 2,
                l = rx - ox, t = ry - oy, r = l + rw, b = t + rh,
                mx = l + (rw / 2), my = t + (rh / 2);

        viewportCanvas.draw(cp, l, t);
        viewportCanvas.draw(cp, r, t);
        viewportCanvas.draw(cp, l, b);
        viewportCanvas.draw(cp, r, b);
        viewportCanvas.draw(cp, mx, t);
        viewportCanvas.draw(cp, mx, b);
        viewportCanvas.draw(cp, l, my);
        viewportCanvas.draw(cp, r, my);
    }

    // HELPER

    public static GameImage blankCanvas(final int width, final int height) {
        final GameImage canvas = new GameImage(width, height);
        canvas.fill(Colors.white());

        // TODO - canvas texture?

        return canvas.submit();
    }

    public static int standardTextWidth(final String text) {
        return new FontFormatter(FONT_DEF).realize().addText(text)
                .build().draw().getWidth();
    }

    public static int naiveButtonWidth(final String label) {
        return standardTextWidth(label) + TEXT_BUTTON_PADDING_X;
    }

    // ALGORITHMS

    public static GameImage highlightIcon(final GameImage icon) {
        final GameImage highlight = new GameImage(HIGHLIGHT);
        highlight.draw(icon);
        return highlight.submit();
    }

    public static GameImage pixelWiseTransformation(
            final GameImage input, final Function<Color, Color> f
    ) {
        final GameImage output = new GameImage(input);

        final int w = output.getWidth(), h = output.getHeight();

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                output.setRGB(x, y, f.apply(input.getColorAt(x, y)).getRGB());

        return output.submit();
    }

    public static Color greyscale(final Color in) {
        final int avg = (in.getRed() + in.getGreen() + in.getBlue()) / 3;
        return new Color(avg, avg, avg, in.getAlpha());
    }

    public static Color shiftRGB(final Color base, final int shift) {
        return new Color(
                shiftChannel(base.getRed(), Math.abs(shift)),
                shiftChannel(base.getGreen(), Math.abs(shift)),
                shiftChannel(base.getBlue(), Math.abs(shift)));
    }

    private static int shiftChannel(final int c, final int shift) {
        final int MIDDLE = 0x80;
        final boolean increase = Math.signum((double) (MIDDLE - c)) >= 0.0;

        return c + (increase ? shift : -shift);
    }
}
