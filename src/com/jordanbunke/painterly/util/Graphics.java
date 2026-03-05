package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;

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

    // TEXT & TEXT UI

    public static TextBuilder uiText(final Color color, final double textSize) {
        return ProgramFont.DEFAULT.getBuilder(textSize, Text.Orientation.CENTER, color);
    }

    public static TextBuilder bigUIText(final Color color) {
        return uiText(color, 2.0);
    }

    public static TextBuilder uiText(final Color color) {
        return uiText(color, 1.0);
    }

    // TODO

    // UI ELEMENTS

    public static int naiveButtonWidth(final String label) {
        final GameImage textImage = uiText(systemColor(DARK))
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

        final GameImage textImage = uiText(textColor)
                .addText(tb.getLabel()).build().draw();
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

    // TODO

    // ADDITIONAL UI

    public static GameImage drawTooltip(final String text) {
        final Color textColor = systemColor(DARK);
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> uiText(textColor).addText(l).build().draw())
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
