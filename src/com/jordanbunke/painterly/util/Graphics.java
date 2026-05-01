package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.Button;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.ProgramFont.FontFormatter;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.*;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors");

    // TODO

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

    // UI ELEMENTS

    public static int naiveButtonWidth(final String label) {
        final GameImage textImage = new FontFormatter(FONT_DEF)
                .setColor(systemColor(DARK)).realize()
                .addText(label).build().draw();

        return textImage.getWidth() + TEXT_BUTTON_PADDING_X;
    }

    public static GameImage drawTextButton(final TextButton tb) {
        // TODO - temp implementation

        // final ButtonType type = tb.getButtonType();
        final boolean highlight = tb.isHighlighted();

        final Color textColor, bgColor, accentColor;

        bgColor = systemColor(highlight ? MID_DARK : DARK);
        accentColor = systemColor(highlight ? MID_LIGHT : MID);
        textColor = systemColor(LIGHT);

        final GameImage textImage = new FontFormatter(FONT_DEF).realize()
                .setColor(textColor).addText(tb.getLabel()).build().draw();
        final GameImage button = new GameImage(tb.getWidth(), tb.getHeight());

        final int w = button.getWidth(), h = button.getHeight();

        // background
        button.fill(bgColor);

        // draw text
        final int x = switch (tb.getAlignment()) {
            case LEFT -> TEXT_BUTTON_MARGIN_X;
            case CENTER -> (w - textImage.getWidth()) / 2;
            case RIGHT -> w - (TEXT_BUTTON_MARGIN_X + textImage.getWidth());
        };
        final int y = (h - textImage.getHeight()) / 2;

        button.draw(textImage, x, y);

        // border
        button.drawRectangle(accentColor, 4f, 0, 0, w, h);

        return button.submit();
    }

    public static GameImage drawVertScrollBar(
            final int w, final int h, final int barH,
            final int barY, final Button b
    ) {
        // TODO - copied from TDSM -- review

        final GameImage scrollSpace = new GameImage(w, h),
                scrollBar = new GameImage(w, barH);

        final Color c = b.outcomes(systemColor(LIGHT),
                systemColor(MID_LIGHT), systemColor(MID_DARK)),
                accent = b.outcomes(systemColor(MID_LIGHT),
                        systemColor(MID_DARK), systemColor(DARK));

        scrollBar.fill(c);
        scrollBar.drawLine(accent, 1f, 0, barH - 2, w, barH - 2);
        scrollBar.drawRectangle(systemColor(DARK), 1f, 0, 0, w - 1, barH - 1);
        // TODO - clearCorners(scrollBar);

        scrollSpace.draw(scrollBar, 0, barY);

        return scrollSpace.submit();
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

    // TODO

    // ADDITIONAL UI

    public static GameImage drawTooltip(final String text) {
        final Color textColor = systemColor(DARK);
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> new FontFormatter(FONT_DEF).realize()
                        .setColor(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
        final int ls = lines.length,
                w = Arrays.stream(lineImages)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(systemColor(MID_LIGHT));

        for (int l = 0; l < ls; l++) {
            final GameImage line = lineImages[l];
            final int x = (w - line.getWidth()) / 2,
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        return tooltip.submit();
    }

    // ALGORITHMS

    public static GameImage highlightIcon(
            final GameImage icon
    ) {
        final int w = icon.getWidth(), h = icon.getHeight();
        final GameImage highlight = new GameImage(icon);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = icon.getColorAt(x, y);

                if (c.getAlpha() == 0 && hasAdjacent(icon, x, y))
                    highlight.dot(/* TODO */ Colors.bg(), x, y);
            }
        }

        return highlight.submit();
    }

    private static boolean hasAdjacent(
            final GameImage image, final int x, final int y
    ) {
        return notTransparent(image, x - 1, y) ||
                notTransparent(image, x + 1, y) ||
                notTransparent(image, x, y - 1) ||
                notTransparent(image, x, y + 1);
    }

    private static boolean notTransparent(
            final GameImage image, final int x, final int y
    ) {
        if (x < 0 || x >= image.getWidth() ||
                y < 0 || y >= image.getHeight())
            return false;

        return image.getColorAt(x, y).getAlpha() > 0;
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
}
