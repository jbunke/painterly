package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class VisualMath {
    public static Coord2D projectPosition(
            final int origX, final int origY,
            final int origW, final int origH,
            final int offsetX, final int offsetY,
            final int projectedW, final int projectedH
    ) {
        final double atX = origX / (double) origW, atY = origY / (double) origH;

        return new Coord2D(offsetX + (int)(projectedW * atX),
                offsetY + (int)(projectedH * atY));
    }
}
