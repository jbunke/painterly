package com.jordanbunke.rene.settings;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.rene.constants.Constants;
import com.jordanbunke.rene.math.RSMath;

public class FocusBox {
    public enum Mode {
        ITINERANT, WORST, FREE
    }

    public static final int UNIVERSAL = 1, DEFAULT_STROKES_PER_UPDATE = 250;

    private int divisions, strokesPerUpdate;
    private int x, y;
    private Mode mode;

    public FocusBox() {
        divisions = UNIVERSAL;
        strokesPerUpdate = DEFAULT_STROKES_PER_UPDATE;

        mode = Mode.FREE;

        x = 0;
        y = 0;
    }

    public void tryMode(final int strokeCount, final GameImage reference, final GameImage painting) {
        if (strokeCount % strokesPerUpdate == 0) {
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

                            Clink.writeUpdate(
                                    "Subsection similarity (" + Clink.highlight(String.valueOf(x), Clink.Mode.UPDATE) +
                                            ", " + Clink.highlight(String.valueOf(y), Clink.Mode.UPDATE) + "): " +
                                            Clink.highlight((similarity * 100) + "%", Clink.Mode.UPDATE)
                            );

                            if (similarity < minSimilarity) {
                                minSimilarity = similarity;
                                minDims[Constants.X] = x;
                                minDims[Constants.Y] = y;
                            }
                        }
                    }

                    setCoordinates(minDims[Constants.X], minDims[Constants.Y]);
                }
                case ITINERANT -> {
                    final int boxNumber = (y * divisions) + x,
                            nextBoxNumber = (boxNumber + 1) % (divisions * divisions);

                    setCoordinates(nextBoxNumber % divisions, nextBoxNumber / divisions);
                }
            }
        }
    }

    public int[] bounds(final GameImage reference) {
        return bounds(x, y, reference.getWidth(), reference.getHeight());
    }

    private int[] bounds(final int x, final int y, final int width, final int height) {
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

    public int getDivisions() {
        return divisions;
    }

    public int getStrokesPerUpdate() {
        return strokesPerUpdate;
    }

    public void setMode(final Mode mode) {
        this.mode = mode;
    }

    public void setDivisions(final int divisions) {
        this.divisions = Math.max(UNIVERSAL, divisions);

        normalizeCoordinates();
    }

    public void setStrokesPerUpdate(final int strokesPerUpdate) {
        this.strokesPerUpdate = Math.max(1, strokesPerUpdate);
    }

    public void setCoordinates(final int x, final int y) {
        this.x = x;
        this.y = y;

        normalizeCoordinates();
    }

    public void adjustCoordinates(final int deltaX, final int deltaY) {
        if (mode != Mode.FREE)
            return;

        this.x += deltaX;
        this.y += deltaY;

        normalizeCoordinates();
    }

    private void normalizeCoordinates() {
        x = Math.max(0, Math.min(x, divisions - 1));
        y = Math.max(0, Math.min(y, divisions - 1));
    }
}
