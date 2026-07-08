package com.jordanbunke.painterly.algo;

import com.jordanbunke.color_proc.ColorProc;
import com.jordanbunke.delta_time.image.GameImage;

import java.awt.*;
import java.util.function.Function;

public final class Recoloring {
    public static GameImage tintGreyscaleTexture(
            final GameImage texture, final Color tint, final int valueAverage
    ) {
        final GameImage recolored = new GameImage(texture);
        final int w = recolored.getWidth(), h = recolored.getHeight(),
                MAX = ColorProc.RGB_SCALE;

        final double rFactor = tint.getRed() / (double) MAX,
                gFactor = tint.getGreen() / (double) MAX,
                bFactor = tint.getBlue() / (double) MAX,
                iFactor = valueAverage / (double) MAX;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color pixel = recolored.getColorAt(x, y);

                final int alpha = pixel.getAlpha();

                final double intensity = ColorProc.rgbToValue(pixel) / iFactor;

                final int r = Math.min(MAX, (int) Math.round(rFactor * intensity * MAX)),
                        g = Math.min(MAX, (int) Math.round(gFactor * intensity * MAX)),
                        b = Math.min(MAX, (int) Math.round(bFactor * intensity * MAX));

                final int c = (alpha << 24) | (r << 16) | (g << 8) | b;

                recolored.setRGB(x, y, c);
            }
        }

        return recolored.submit();
    }

    public static GameImage tintGreyscaleTexture(
            final GameImage texture, final Color tint
    ) {
        return tintGreyscaleTexture(texture, tint, ColorProc.RGB_SCALE);
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

    public static Color reduceOpacity(final Color c, final double multiplier) {
        final int r = c.getRed(), g = c.getGreen(), b = c.getBlue(),
                a = (int) Math.round(multiplier * c.getAlpha());
        return new Color(r, g, b, a);
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
