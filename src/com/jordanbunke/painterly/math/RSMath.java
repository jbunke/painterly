package com.jordanbunke.painterly.math;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.constants.Constants;
import com.jordanbunke.painterly.painter.BrushStroke;

import java.awt.*;

public class RSMath {
    public static double similarity(
            final GameImage reference, final GameImage painting,
            final int startX, final int startY,
            final int endX, final int endY
    ) {
        final int pixels = (endX - startX) * (endY - startY);
        double cumulativeSimilarity = 0.;

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                cumulativeSimilarity += colorSimilarity(
                        reference.getColorAt(x, y), painting.getColorAt(x, y));
            }
        }

        return cumulativeSimilarity / (double) pixels;
    }

    public static double similarity(
            final GameImage reference, final GameImage painting,
            final int[] bounds
    ) {
        return similarity(reference, painting,
                bounds[Constants.BOUND_X1], bounds[Constants.BOUND_Y1],
                bounds[Constants.BOUND_X2], bounds[Constants.BOUND_Y2]);
    }

    private static double colorSimilarity(
            final Color cRef, final Color cP
    ) {
        final double rSim = (RSColors.MAX - Math.abs(cRef.getRed() -
                cP.getRed())) / (double) RSColors.MAX;
        final double gSim = (RSColors.MAX - Math.abs(cRef.getGreen() -
                cP.getGreen())) / (double) RSColors.MAX;
        final double bSim = (RSColors.MAX - Math.abs(cRef.getBlue() -
                cP.getBlue())) / (double) RSColors.MAX;

        return (rSim + gSim + bSim) / 3.;
    }

    public static int[] getPixelInBounds(
            final int startX, final int startY,
            final int endX, final int endY
    ) {
        return new int[] {
                RNG.randomInRange(startX, endX),
                RNG.randomInRange(startY, endY),
        };
    }

    public static int[] getPixelInBounds(final int[] bounds) {
        return getPixelInBounds(
                bounds[Constants.BOUND_X1], bounds[Constants.BOUND_Y1],
                bounds[Constants.BOUND_X2], bounds[Constants.BOUND_Y2]);
    }

    public static BrushStroke generateStroke(
            final int[] strokePos, final double similarity,
            final int width, final int height
    ) {
        return new BrushStroke(
                strokePos[Constants.X], strokePos[Constants.Y], similarity,
                Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2)));
    }

    public static int[] normalizeBounds(final int[] bounds, final GameImage reference) {
        bounds[Constants.BOUND_X1] = Math.max(0,
                Math.min(bounds[Constants.BOUND_X1], reference.getWidth() - 1));
        bounds[Constants.BOUND_Y1] = Math.max(0,
                Math.min(bounds[Constants.BOUND_Y1], reference.getHeight() - 1));
        bounds[Constants.BOUND_X2] = Math.max(0,
                Math.min(bounds[Constants.BOUND_X2], reference.getWidth() - 1));
        bounds[Constants.BOUND_Y2] = Math.max(0,
                Math.min(bounds[Constants.BOUND_Y2], reference.getHeight() - 1));

        return bounds;
    }
}
