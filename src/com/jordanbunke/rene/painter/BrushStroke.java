package com.jordanbunke.rene.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.RNG;
import com.jordanbunke.rene.constants.Constants;
import com.jordanbunke.rene.math.RSMath;

import java.awt.*;

public class BrushStroke {
    private static final double CIRCLE = Math.PI * 2,
            MAX_DIR_CHANGE = CIRCLE / 500.,
            MAX_BREADTH_DIAG_DIVIDEND = 50.;
    private static final int MAX_LENGTH_DIAG_DIVIDEND = 10;

    private final int x, y, length;
    private final double
            initialBreadth, finalBreadth,
            initialDirection, directionChange;

    public BrushStroke(
            final int x, final int y, final double similarity, final double diagonal
    ) {
        this.x = x;
        this.y = y;

        initialDirection = RNG.randomInRange(0., CIRCLE);
        directionChange = RNG.randomInRange(-MAX_DIR_CHANGE, MAX_DIR_CHANGE);

        initialBreadth = determineInitialBreadth(similarity, diagonal);
        finalBreadth = RNG.randomInRange(initialBreadth / 2d, initialBreadth);

        length = determineLength(similarity, diagonal);
    }

    private double similarityDampening(final double similarity) {
        return Constants.MAX_SIMILARITY - Math.pow(similarity, Constants.SIM_DAMP_EXPONENT);
    }

    private double determineInitialBreadth(
            final double similarity, final double diagonal
    ) {
        return 2d + (RNG.randomInRange(0., diagonal / MAX_BREADTH_DIAG_DIVIDEND) *
                similarityDampening(similarity));
    }

    private int determineLength(
            final double similarity, final double diagonal
    ) {
        return 2 + (int)(RNG.randomInRange(0d, diagonal / MAX_LENGTH_DIAG_DIVIDEND) *
                similarityDampening(similarity));
    }

    public int[] draw(final GameImage canvas, final Color c) {
        final int[] bounds = new int[] { x, y, x + 1, y + 1 };

        double[] p = new double[] { (double) x, (double) y };
        strokePoint(canvas, c, initialDirection, bounds, p, 0);

        canvas.free();
        return RSMath.normalizeBounds(bounds, canvas);
    }

    private void strokePoint(
            final GameImage canvas, final Color c,
            final double direction,
            final int[] bounds, final double[] p,
            final int i
    ) {
        if (i >= length)
            return;

        final double breadth = getBreadthForIndex(i);

        final double[] np = new double[] {
                p[Constants.X] + Math.cos(direction),
                p[Constants.Y] - Math.sin(direction)
        };

        canvas.drawLine(c, (int) breadth,
                (int) p[Constants.X], (int) p[Constants.Y],
                (int) np[Constants.X], (int) np[Constants.Y]);

        updateBounds(bounds,
                Math.max((int)(p[Constants.X] + (breadth / 2)) + 2,
                        (int)(np[Constants.X] + (breadth / 2)) + 2),
                Math.max((int)(p[Constants.X] - (breadth / 2)) - 1,
                        (int)(np[Constants.X] - (breadth / 2)) - 1),
                Math.max((int)(p[Constants.Y] + (breadth / 2)) + 2,
                        (int)(np[Constants.Y] + (breadth / 2)) + 2),
                Math.max((int)(p[Constants.Y] - (breadth / 2)) - 1,
                        (int)(np[Constants.Y] - (breadth / 2)) - 1));

        strokePoint(canvas, c,
                normalizeDirection(direction + directionChange),
                bounds, np, i + 1);
    }

    private double normalizeDirection(double d) {
        while (d < 0d)
            d += CIRCLE;

        while (d >= CIRCLE)
            d -= CIRCLE;

        return d;
    }

    private double getBreadthForIndex(final int i) {
        return initialBreadth +
                ((i / (double) length) * (finalBreadth - initialBreadth));
    }

    private void updateBounds(
            final int[] bounds,
            final int maxX, final int minX,
            final int maxY, final int minY
    ) {
        bounds[Constants.BOUND_X1] = Math.min(minX, bounds[Constants.BOUND_X1]);
        bounds[Constants.BOUND_Y1] = Math.min(minY, bounds[Constants.BOUND_Y1]);
        bounds[Constants.BOUND_X2] = Math.max(maxX, bounds[Constants.BOUND_X2]);
        bounds[Constants.BOUND_Y2] = Math.max(maxY, bounds[Constants.BOUND_Y2]);
    }
}
