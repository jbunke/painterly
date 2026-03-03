package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.awt.*;
import java.nio.file.Path;
import java.util.function.Function;

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

    // TODO

    // ADDITIONAL

    public static GameImage drawTooltip(final String text) {
        // TODO

        return GameImage.dummy();
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
