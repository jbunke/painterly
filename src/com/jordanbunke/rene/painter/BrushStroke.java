package com.jordanbunke.rene.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.RNG;
import com.jordanbunke.rene.constants.Constants;

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

        double[] last = new double[] { (double) x, (double) y };

        for (int i = 0; i < length; i++)
            last = nextStrokePoint(canvas, c, bounds,
                    last[Constants.X], last[Constants.Y], i);

        canvas.free();
        return normalizeBounds(bounds, canvas);
    }

    private double getDirectionForIndex(final int i) {
        double direction = initialDirection + (i * directionChange);

        while (direction < 0d)
            direction += CIRCLE;

        while (direction >= CIRCLE)
            direction -= CIRCLE;

        return direction;
    }

    private double getBreadthForIndex(final int i) {
        return initialBreadth +
                ((i / (double) length) * (finalBreadth - initialBreadth));
    }

    private double[] nextStrokePoint(
            final GameImage canvas, final Color c, final int[] bounds,
            final double lastTrueX, final double lastTrueY, final int i
    ) {
        final double breadth = getBreadthForIndex(i);
        final double direction = getDirectionForIndex(i);

        final double trueX = lastTrueX + Math.cos(direction),
                trueY = lastTrueY - Math.sin(direction);

        canvas.drawLine(c, (int) breadth, (int) lastTrueX,
                (int) lastTrueY, (int) trueX, (int) trueY);

        updateBounds(bounds,
                Math.max((int)(lastTrueX + (breadth / 2)) + 2, (int)(trueX + (breadth / 2)) + 2),
                Math.max((int)(lastTrueX - (breadth / 2)) - 1, (int)(trueX - (breadth / 2)) - 1),
                Math.max((int)(lastTrueY + (breadth / 2)) + 2, (int)(trueY + (breadth / 2)) + 2),
                Math.max((int)(lastTrueY - (breadth / 2)) - 1, (int)(trueY - (breadth / 2)) - 1));

        return new double[] { trueX, trueY };
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

    private int[] normalizeBounds(final int[] bounds, final GameImage reference) {
        return new int[] {
                Math.max(0, bounds[Constants.BOUND_X1]),
                Math.max(0, bounds[Constants.BOUND_Y1]),
                Math.min(reference.getWidth() - 1, bounds[Constants.BOUND_X2]),
                Math.min(reference.getHeight() - 1, bounds[Constants.BOUND_Y2])
        };
    }
}
