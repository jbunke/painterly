package com.jordanbunke.painterly.settings;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.constants.Constants;
import com.jordanbunke.painterly.math.RSMath;

public class FocusBox {
    public enum Mode {
        ITERATE, WORST, FREE, RANDOM, CUSTOM
    }

    public enum TickMode {
        STROKE, ATTEMPT
    }

    public static final int UNIVERSAL = 1, DEFAULT_BOX_TICK = 250;

    private int divisions, boxTick;
    private int x, y;
    private Mode mode;
    private TickMode tickMode;

    private final int[] customBounds;

    public FocusBox() {
        divisions = UNIVERSAL;
        boxTick = DEFAULT_BOX_TICK;

        mode = Mode.FREE;
        tickMode = TickMode.STROKE;

        x = 0;
        y = 0;

        customBounds = new int[] { 0, 0, 1, 1 };
    }

    public void tryMode(
            final int strokeCount, final int attemptCount,
            final GameImage reference, final GameImage painting
    ) {
        final int tick = tickMode == TickMode.STROKE
                ? strokeCount : attemptCount;

        if (tick % boxTick == 0) {
            switch (mode) {
                case WORST -> {
                    final int[] minDims = new int[2];
                    double minSimilarity = Constants.MAX_SIMILARITY;

                    for (int x = 0; x < divisions; x++) {
                        for (int y = 0; y < divisions; y++) {
                            final int[] bounds = bounds(x, y, reference.getWidth(), reference.getHeight());
                            final double similarity = RSMath.similarity(
                                    reference, painting,
                                    bounds[Constants.BOUND_X1],
                                    bounds[Constants.BOUND_Y1],
                                    bounds[Constants.BOUND_X2],
                                    bounds[Constants.BOUND_Y2]
                            );

                            if (similarity < minSimilarity) {
                                minSimilarity = similarity;
                                minDims[Constants.X] = x;
                                minDims[Constants.Y] = y;
                            }
                        }
                    }

                    Clink.writeUpdate(
                            "Lowest similarity (" + Clink.highlight(String.valueOf(minDims[Constants.X]), Clink.Mode.UPDATE) +
                                    ", " + Clink.highlight(String.valueOf(minDims[Constants.Y]), Clink.Mode.UPDATE) + "): " +
                                    Clink.highlight((minSimilarity * 100) + "%", Clink.Mode.UPDATE)
                    );

                    setCoordinates(minDims[Constants.X], minDims[Constants.Y]);
                }
                case ITERATE -> {
                    final int boxNumber = (y * divisions) + x,
                            nextBoxNumber = (boxNumber + 1) % (divisions * divisions);

                    setCoordinates(nextBoxNumber % divisions, nextBoxNumber / divisions);
                }
                case RANDOM -> setCoordinates(RNG.randomInRange(0, divisions),
                        RNG.randomInRange(0, divisions));
            }
        }
    }

    public int[] bounds(final GameImage reference) {
        return bounds(x, y, reference.getWidth(), reference.getHeight());
    }

    private int[] bounds(final int x, final int y, final int width, final int height) {
        if (mode == Mode.CUSTOM)
            return customBounds;

        final int[] bounds = new int[4];

        bounds[Constants.BOUND_X1] = (int)((x / (double) divisions) * width);
        bounds[Constants.BOUND_Y1] = (int)((y / (double) divisions) * height);
        bounds[Constants.BOUND_X2] = (int)(((x + 1) / (double) divisions) * width);
        bounds[Constants.BOUND_Y2] = (int)(((y + 1) / (double) divisions) * height);

        return bounds;
    }

    public Mode getMode() {
        return mode;
    }

    public TickMode getTickMode() {
        return tickMode;
    }

    public int getDivisions() {
        return divisions;
    }

    public int getBoxTick() {
        return boxTick;
    }

    public void setCustomBounds(
            final int mouseDownX, final int mouseDownY,
            final int mouseUpX, final int mouseUpY,
            final GameImage reference
    ) {
        mode = Mode.CUSTOM;

        customBounds[Constants.BOUND_X1] = Math.min(mouseDownX, mouseUpX);
        customBounds[Constants.BOUND_X2] = Math.max(mouseDownX, mouseUpX);
        customBounds[Constants.BOUND_Y1] = Math.min(mouseDownY, mouseUpY);
        customBounds[Constants.BOUND_Y2] = Math.max(mouseDownY, mouseUpY);

        RSMath.normalizeBounds(customBounds, reference);
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public void setTickMode(final TickMode tickMode) {
        this.tickMode = tickMode;
    }

    public void setDivisions(final int divisions) {
        this.divisions = Math.max(UNIVERSAL, divisions);

        normalizeCoordinates();
    }

    public void setBoxTick(final int boxTick) {
        this.boxTick = Math.max(1, boxTick);
    }

    public void setCoordinates(final int x, final int y) {
        this.x = x;
        this.y = y;

        normalizeCoordinates();
    }

    public void adjustCoordinates(final int deltaX, final int deltaY) {
        if (mode == Mode.CUSTOM) {
            setMode(Mode.FREE);
            return;
        }

        this.x += deltaX;
        this.y += deltaY;

        normalizeCoordinates();
    }

    private void normalizeCoordinates() {
        x = Math.max(0, Math.min(x, divisions - 1));
        y = Math.max(0, Math.min(y, divisions - 1));
    }
}
