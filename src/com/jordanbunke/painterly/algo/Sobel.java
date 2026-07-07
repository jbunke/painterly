package com.jordanbunke.painterly.algo;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.theme.Colors;

import java.awt.*;

import static com.jordanbunke.color_proc.ColorProc.*;
import static com.jordanbunke.painterly.algo.CircleMath.*;

public final class Sobel {
    private static final Kernel KERNEL_X, KERNEL_Y;

    private static final double
            LUM_R_WEIGHT = 0.2126,
            LUM_G_WEIGHT = 0.7152,
            LUM_B_WEIGHT = 0.0722;

    static {
        KERNEL_X = new Kernel(3, 3, -1, 0, 1, -2, 0, 2, -1, 0, 1);
        KERNEL_Y = new Kernel(3, 3, -1, -2, -1, 0, 0, 0, 1, 2, 1);
    }

    public static GameImage calculate(final GameImage source) {
        return convolution(intensity(source));
    }

    private static int[][] intensity(final GameImage source) {
        final int w = source.getWidth(), h = source.getHeight();
        final int[][] intensity = new int[w][h];

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                intensity[x][y] = grayscale(source.getColorAt(x, y));

        return intensity;
    }

    private static int grayscale(final Color color) {
        return (int) (
                LUM_R_WEIGHT * color.getRed() +
                LUM_G_WEIGHT * color.getGreen() +
                LUM_B_WEIGHT * color.getBlue());
    }

    private static GameImage convolution(
            final int[][] input
    ) {
        final int B_X = 1, B_Y = 1;
        final int w = input.length, h = input[0].length;

        final GameImage output = new GameImage(w, h);

        output.fill(Colors.black());

        for (int x = B_X; x < w - B_X; x++) {
            for (int y = B_Y; y < h - B_Y; y++) {
                int gX = 0;
                int gY = 0;

                // neighbourhood
                for (int i = -B_X; i <= B_X; i++) {
                    for (int j = -B_Y; j <= B_Y; j++) {
                        final int sourcePixel = input[x + i][y + j];

                        gX += sourcePixel * KERNEL_X.at(i + 1, j + 1);
                        gY += sourcePixel * KERNEL_Y.at(i + 1, j + 1);
                    }
                }

                final Color pixel = getColorFromSobelOperators(gX, gY);
                output.setRGB(x, y, pixel.getRGB());
            }
        }

        return output.submit();
    }

    private static Color getColorFromSobelOperators(
            final int gX, final int gY
    ) {
        // overall edge intensity
        final int g = (int) Math.sqrt((gX * gX) + (gY * gY));

        // edge angle
        final double angle = Math.atan2(gY, gX);

        // hue, saturation and value:
        // hue is determined by edge angle,
        // saturation and value by edge intensity
        final double h = (angle + Math.PI) / (2 * Math.PI),
                sv = Math.min(g, RGB_SCALE) / (double) RGB_SCALE;

        return fromHSV(h, sv, sv);
    }

    public static double edgeIntensity(
            final int x, final int y, final Project p
    ) {
        return rgbToValue(getSobelPixel(x, y, p));
    }

    public static double edgeDirection(
            final int x, final int y, final Project p
    ) {
        final double hue = rgbToHue(getSobelPixel(x, y, p));

        // return angle perpendicular to Sobel direction indicated by hue
        return augmentAngle(fractionOfCircle(hue), fractionOfCircle(0.25));
    }

    private static Color getSobelPixel(
            final int x, final int y, final Project p
    ) {
        final GameImage sobel = p.canvas.getSobel();
        return sobel.getColorAt(x, y);
    }
}
