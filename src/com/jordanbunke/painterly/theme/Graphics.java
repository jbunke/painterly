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
import com.jordanbunke.painterly.util.ProgramFont;
import com.jordanbunke.painterly.util.ProgramFont.FontFormatter;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.theme.Colors.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.*;
import static com.jordanbunke.painterly.viewport.VisualMath.projectPosition;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors"),
            MISC_IMG_FOLDER = Path.of("img_misc");

    public static final GameImage ICON;
    private static final GameImage CONTROL_POINT, HIGHLIGHT;
    public static final GameImage[] MENU_ANIMATION;

    static {
        ICON = readMiscImage(RC_ICON);
        CONTROL_POINT = readMiscImage(RC_CONTROL_POINT);
        HIGHLIGHT = readMiscImage(RC_HIGHLIGHT);
        MENU_ANIMATION = readGIF(RC_MENU_BG);
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

    public static GameImage[] readGIF(final ResourceCode code) {
        final Path gifFile = MISC_IMG_FOLDER.resolve(code.id() + ".gif");
        return ResourceLoader.loadGIFResourceFrames(gifFile);
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
        final Color textColor = white(),
                backgroundColor = black(),
                shadowColor = darkGrey(),
                outlineColor = lightGrey();

        final GameImage textImage = new FontFormatter(FONT_DEF)
                .setTextSize(1.0)
                .setColor(textColor).realize().addText(keyAsString)
                .build().draw();

        final int w = textImage.getWidth() + (2 * KEY_SHORTCUT_TEXT_MARGIN_X),
                keyTopHeight = textImage.getHeight(),
                h = keyTopHeight + KEY_SHORTCUT_DROP_SHADOW;
        final GameImage key = new GameImage(w, h),
                keyTop = new GameImage(w, keyTopHeight),
                shadow = new GameImage(w, h);

        keyTop.fill(backgroundColor);
        keyTop.drawRectangle(outlineColor, 2f, 0, 0, w, keyTopHeight);
        smoothCorners(keyTop, outlineColor);

        shadow.fill(shadowColor);
        shadow.drawRectangle(outlineColor, 2f, 0, 0, w, h);
        smoothCorners(shadow, outlineColor);

        key.draw(shadow.submit());
        key.draw(keyTop.submit());
        key.draw(textImage, KEY_SHORTCUT_TEXT_MARGIN_X, 0);

        return key.submit();
    }

    // ADDITIONAL UI

    public static void drawDFAReticle(
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

    public static GameImage[] splitTextLines(final String text, final Color textColor) {
        return Arrays.stream(text.split("\n"))
                .map(l -> new ProgramFont.FontFormatter(FONT_DEF).realize()
                        .setColor(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
    }

    public static GameImage blankCanvas(final int width, final int height) {
        final GameImage canvas = new GameImage(width, height);
        canvas.fill(Colors.white());

        // TODO - canvas texture?

        return canvas.submit();
    }

    public static void smoothCorners(
            final GameImage image, final Color borderColor
    ) {
        final int GAP_W = 2, GAP_H = 2,
                w = image.getWidth(), h = image.getHeight(),
                transparent = transparent().getRGB();

        for (int x = 0; x < GAP_W; x++) {
            for (int y = 0; y < GAP_H; y++) {
                final boolean replace = x == GAP_W - 1 && y == GAP_H - 1;
                final int c = replace ? borderColor.getRGB() : transparent;

                image.setRGB(x, y, c);
                image.setRGB(w - (1 + x), y, c);
                image.setRGB(x, h - (1 + y), c);
                image.setRGB(w - (1 + x), h - (1 + y), c);
            }
        }
    }

    public static void circleOnly(final GameImage image) {
        final int w = image.getWidth(), h = image.getHeight();

        if (w != h)
            return;

        final int r = w / 2, transparent = transparent().getRGB();
        final double mp = r - 0.5;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final double d = Math.sqrt(
                        Math.pow(Math.abs(x - mp), 2) +
                                Math.pow(Math.abs(y - mp), 2));

                if (d > r)
                    image.setRGB(x, y, transparent);
            }
        }
    }

    public static void innerOutline(
            final GameImage image, final Color match, final Color outlineColor
    ) {
        final int oc = outlineColor.getRGB(), w = image.getWidth(), h = image.getHeight();
        final GameImage stampImage = new GameImage(w, h);

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                if (edgeOfContiguousColor(image, x, y, match))
                    stampImage.setRGB(x, y, oc);

        image.draw(stampImage.submit());
    }

    private static boolean edgeOfContiguousColor(
            final GameImage image, final int x, final int y, final Color c
    ) {
        return colorAtPixel(image, x, y, c) &&
                !(colorAtPixel(image, x - 1, y, c) &&
                        colorAtPixel(image, x + 1, y, c) &&
                        colorAtPixel(image, x, y - 1, c) &&
                        colorAtPixel(image, x, y + 1, c));
    }

    private static boolean colorAtPixel(
            final GameImage image, final int x, final int y, final Color c
    ) {
        final int w = image.getWidth(), h = image.getHeight();
        if (x < 0 || x >= w || y < 0 || y >= h)
            return false;

        return c.equals(image.getColorAt(x, y));
    }

    @SuppressWarnings("unused")
    private static boolean notTransparentWithTransparentNeighbor(
            final GameImage image, final int x, final int y
    ) {
        return !transparentOrOOB(image, x, y) &&
                (transparentOrOOB(image, x - 1, y) ||
                        transparentOrOOB(image, x + 1, y) ||
                        transparentOrOOB(image, x, y - 1) ||
                        transparentOrOOB(image, x, y + 1));
    }

    private static boolean transparentOrOOB(
            final GameImage image, final int x, final int y
    ) {
        final int w = image.getWidth(), h = image.getHeight();
        if (x < 0 || x >= w || y < 0 || y >= h)
            return true;

        return image.getColorAt(x, y).getAlpha() == 0;
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
}
