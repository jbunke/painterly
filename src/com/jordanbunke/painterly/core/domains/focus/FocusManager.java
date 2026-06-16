package com.jordanbunke.painterly.core.domains.focus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.jordanbunke.painterly.viewport.VisualMath.projectPosition;

public final class FocusManager {
    private final Project project;

    private FocusBoxMode focusBoxMode;
    private boolean entireArea, wholeCanvas;
    private RectBounds focusArea;
    private int divsX, divsY, x, y, maxDivsX, maxDivsY;

    public FocusManager(final Project project) {
        this.project = project;

        focusArea = new RectBounds(0, project.width, 0, project.height);
        entireArea = true;
        wholeCanvas = true;
        focusBoxMode = FocusBoxMode.FREE;

        divsX = 1;
        divsY = 1;
        x = 0;
        y = 0;
        determineDivMaxima();
    }

    public Coord2D strokePosition() {
        final RectBounds bounds = activeBounds();

        return new Coord2D(
                RNG.randomInRange(bounds.left(), bounds.right()),
                RNG.randomInRange(bounds.top(), bounds.bottom()));
    }

    public void setFocusBoxMode(final FocusBoxMode focusBoxMode) {
        this.focusBoxMode = focusBoxMode;
    }

    public void resetFocusArea() {
        setFocusArea(new RectBounds(0, project.width, 0, project.height));
    }

    public void setFocusArea(final RectBounds focusArea) {
        if (!this.focusArea.equals(focusArea)) {
            this.focusArea = focusArea;
            wholeCanvas = focusArea.width() == project.width &&
                    focusArea.height() == project.height;

            clearFocusBoxes();
        }
    }

    public void clearFocusBoxes() {
        entireArea = true;
        divsX = 1;
        divsY = 1;
        x = 0;
        y = 0;
        determineDivMaxima();
    }

    public void augmentDivsX(final int dx) {
        entireArea = false;
        setDivsX(divsX + dx);
    }

    public void augmentDivsY(final int dy) {
        entireArea = false;
        setDivsY(divsY + dy);
    }

    public void augmentX(final int dx) {
        entireArea = false;
        setX(x + dx);
    }

    public void augmentY(final int dy) {
        entireArea = false;
        setY(y + dy);
    }

    public void setDivsX(final int divsX) {
        this.divsX = MathPlus.bounded(1, divsX, maxDivsX);
        setX(x);
    }

    public void setDivsY(final int divsY) {
        this.divsY = MathPlus.bounded(1, divsY, maxDivsY);
        setY(y);
    }

    public void setX(final int x) {
        this.x = MathPlus.bounded(0, x, divsX - 1);
    }

    public void setY(final int y) {
        this.y = MathPlus.bounded(0, y, divsY - 1);
    }

    private void determineDivMaxima() {
        maxDivsX = Math.min(Constants.MAX_BOX_DIVS, focusArea.width());
        maxDivsY = Math.min(Constants.MAX_BOX_DIVS, focusArea.height());
    }

    public RectBounds getFocusArea() {
        return focusArea;
    }

    private RectBounds activeBounds() {
        return entireArea ? focusArea : currentBoxBounds();
    }

    private RectBounds currentBoxBounds() {
        return bounds(x, y);
    }

    private RectBounds bounds(final int x, final int y) {
        final int x0 = focusArea.left(), y0 = focusArea.top(),
                w = focusArea.width(), h = focusArea.height();

        return new RectBounds(
                x0 + (int)((x / (double) divsX) * w),
                x0 + (int)(((x + 1) / (double) divsX) * w),
                y0 + (int)((y / (double) divsY) * h),
                y0 + (int)(((y + 1) / (double) divsY) * h));
    }

    public void tryUpdateBox() {
        if (divsX == 1 && divsY == 1)
            return;

        switch (focusBoxMode) {
            case RANDOM -> {
                setX(RNG.randomInRange(0, divsX));
                setY(RNG.randomInRange(0, divsY));
            }
            case FORWARDS -> {
                final int boxNumber = (y * divsX) + x;
                setFromBoxNumber(boxNumber + 1);
            }
            case BACKWARDS -> {
                final int boxNumber = (y * divsX) + x;
                setFromBoxNumber(boxNumber - 1);
            }
            case WORST -> {
                int worstX = 0, worstY = 0;
                double leastSimilar = 1d;

                for (int x = 0; x < divsX; x++) {
                    for (int y = 0; y < divsY; y++) {
                        final RectBounds bounds = bounds(x, y);
                        final double similarity =
                                project.canvas.similarity(bounds);

                        if (similarity < leastSimilar) {
                            leastSimilar = similarity;
                            worstX = x;
                            worstY = y;
                        }
                    }
                }

                setX(worstX);
                setY(worstY);
            }
            case PRIORITIZE_WORST -> {
                final int divisions = divsX * divsY;
                final List<Pair<Coord2D, Double>> boxes =
                        new ArrayList<>(divisions);

                for (int x = 0; x < divsX; x++) {
                    for (int y = 0; y < divsY; y++) {
                        final RectBounds bounds = bounds(x, y);
                        final double similarity =
                                project.canvas.similarity(bounds);

                        boxes.add(new Pair<>(new Coord2D(x, y), similarity));
                    }
                }

                boxes.sort(Comparator.comparingDouble(Pair::b));

                final int index = (int)(Math.pow(RNG.randomInRange(0d, 1d),
                        Constants.PRIORITIZE_WORST_EXPONENT) * divisions);

                setX(boxes.get(index).a().x);
                setY(boxes.get(index).a().y);
            }
        }
    }

    private void setFromBoxNumber(int boxNumber) {
        final int divisions = divsX * divsY;

        while (boxNumber >= divisions)
            boxNumber -= divisions;
        while (boxNumber < 0)
            boxNumber += divisions;

        setX(boxNumber % divsX);
        setY(boxNumber / divsX);
    }

    public void drawOverlay(
            final GameImage viewportCanvas,
            final int x, final int y, final int w, final int h
    ) {
        if (!wholeCanvas)
            areaOverlay(viewportCanvas, x, y, w, h);

        if (!entireArea && (divsX > 1 || divsY > 1))
            boxOverlay(viewportCanvas, x, y, w, h);
    }

    private void areaOverlay(
            final GameImage viewportCanvas,
            final int x, final int y, final int w, final int h
    ) {
        final int AREA_OPACITY = 0x80;

        boundsOverlay(viewportCanvas, focusArea,
                Colors.focusArea(AREA_OPACITY), x, y, w, h);
    }

    private void boxOverlay(
            final GameImage viewportCanvas,
            final int x, final int y, final int w, final int h
    ) {
        final int BOX_OPACITY = 0xc0;

        boundsOverlay(viewportCanvas, currentBoxBounds(),
                Colors.focusBox(BOX_OPACITY), x, y, w, h);
    }

    private void boundsOverlay(
            final GameImage viewportCanvas, final RectBounds bounds,
            final Color color,
            final int x, final int y, final int w, final int h
    ) {
        final Coord2D tlRenderPos = projectPosition(
                bounds.left(), bounds.top(),
                project.width, project.height, x, y, w, h),
                brRenderPos = projectPosition(
                        bounds.right(), bounds.bottom(),
                        project.width, project.height, x, y, w, h);
        final int rx = tlRenderPos.x, ry = tlRenderPos.y,
                rw = brRenderPos.x - rx, rh = brRenderPos.y - ry;

        viewportCanvas.drawRectangle(color, 2f, rx, ry, rw, rh);
    }
}
