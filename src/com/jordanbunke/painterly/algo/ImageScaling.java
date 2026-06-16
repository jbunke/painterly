package com.jordanbunke.painterly.algo;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;

public final class ImageScaling {
    public static GameImage bicubic(
            final GameImage source,
            final int scaleFactor
    ) {
        return bicubic(source, scaleFactor, scaleFactor);
    }

    public static GameImage bicubic(
            final GameImage source,
            final double scaleW, final double scaleH
    ) {
        final int srcW = source.getWidth(),
                srcH = source.getHeight();
        final int newW = (int)(srcW * scaleW),
                newH = (int)(srcH * scaleH);

        final GameImage dst = new GameImage(newW, newH);

        for (int y = 0; y < newH; y++) {

            final float gy = ((float) y * srcH) / newH;

            final int sy = (int) gy;
            final float ty = gy - sy;

            for (int x = 0; x < newW; x++) {

                final float gx = ((float) x * srcW) / newW;

                final int sx = (int) gx;
                final float tx = gx - sx;

                final float[] arrA = new float[4],
                        arrR = new float[4],
                        arrG = new float[4],
                        arrB = new float[4];

                for (int m = -1; m <= 2; m++) {

                    final float[] rowA = new float[4],
                            rowR = new float[4],
                            rowG = new float[4],
                            rowB = new float[4];

                    final int py = MathPlus.bounded(0, sy + m, srcH - 1);

                    for (int n = -1; n <= 2; n++) {

                        final int px = MathPlus.bounded(0, sx + n, srcW - 1);
                        final int c = source.getRGB(px, py);

                        rowA[n + 1] = (c >> 24) & 0xff;
                        rowR[n + 1] = (c >> 16) & 0xff;
                        rowG[n + 1] = (c >> 8) & 0xff;
                        rowB[n + 1] = c & 0xff;
                    }

                    arrA[m + 1] = cubic(tx, rowA);
                    arrR[m + 1] = cubic(tx, rowR);
                    arrG[m + 1] = cubic(tx, rowG);
                    arrB[m + 1] = cubic(tx, rowB);
                }

                final int a = MathPlus.bounded(0, Math.round(cubic(ty, arrA)), 0xff),
                        r = MathPlus.bounded(0, Math.round(cubic(ty, arrR)), 0xff),
                        g = MathPlus.bounded(0, Math.round(cubic(ty, arrG)), 0xff),
                        b = MathPlus.bounded(0, Math.round(cubic(ty, arrB)), 0xff);

                final int rgba = (a << 24) | (r << 16) | (g << 8) | b;

                dst.setRGB(x, y, rgba);
            }
        }

        return dst.submit();
    }

    /**
     * Catmull–Rom spline interpolation
     * */
    private static float cubic(
            final float t, final float... p
    ) {
        if (p.length != 4) throw new NumberFormatException();

        float a = -0.5f * p[0] + 1.5f * p[1] - 1.5f * p[2] + 0.5f * p[3];
        float b = p[0] - 2.5f * p[1] + 2f * p[2] - 0.5f * p[3];
        float c = -0.5f * p[0] + 0.5f * p[2];
        float d = p[1];

        return ((a * t + b) * t + c) * t + d;
    }
}
