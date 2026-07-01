package com.jordanbunke.painterly.core.domains.focus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.resources.StringVariableMap;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.debug.LogManager;
import com.jordanbunke.painterly.util.debug.LogMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.resources.StringVariableMap.ID.*;
import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.debug.LogChannel.*;

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

    public void focusBoxAsNewFocusArea() {
        if (!entireArea)
            setFocusArea(currentBoxBounds(), true);
    }

    public void resetFocusArea() {
        setFocusArea(new RectBounds(0, project.width, 0, project.height),
                true);
    }

    public void setFocusArea(
            final RectBounds focusArea, final boolean clearFocusBoxes
    ) {
        if (!this.focusArea.equals(focusArea)) {
            this.focusArea = focusArea;
            wholeCanvas = focusArea.width() == project.width &&
                    focusArea.height() == project.height;

            if (clearFocusBoxes)
                clearFocusBoxes();
            project.progressManager.update();
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
        setDivsX(divsX + dx);
    }

    public void augmentDivsY(final int dy) {
        setDivsY(divsY + dy);
    }

    public void augmentX(final int dx) {
        setX(x + dx);
    }

    public void augmentY(final int dy) {
        setY(y + dy);
    }

    public void setDivsX(final int divsX) {
        this.divsX = MathPlus.bounded(1, divsX, maxDivsX);
        setX(x);
        determineIfEntireArea();
    }

    public void setDivsY(final int divsY) {
        this.divsY = MathPlus.bounded(1, divsY, maxDivsY);
        setY(y);
        determineIfEntireArea();
    }

    public void setX(final int x) {
        this.x = MathPlus.bounded(0, x, divsX - 1);
    }

    public void setY(final int y) {
        this.y = MathPlus.bounded(0, y, divsY - 1);
    }

    private void determineIfEntireArea() {
        entireArea = divsX == 1 && divsY == 1;
    }

    private void determineDivMaxima() {
        maxDivsX = Math.min(Constants.MAX_BOX_DIVS, focusArea.width());
        maxDivsY = Math.min(Constants.MAX_BOX_DIVS, focusArea.height());
    }

    public RectBounds getFocusArea() {
        return focusArea;
    }

    public RectBounds activeBounds() {
        return entireArea ? focusArea : currentBoxBounds();
    }

    private RectBounds currentBoxBounds() {
        return bounds(x, y);
    }

    public RectBounds bounds(final int x, final int y) {
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

                StringVariableMap.post(PW_RANK, String.valueOf(divisions - index));
                StringVariableMap.post(PW_DIVISIONS, String.valueOf(divisions));
                LogManager.log(new LogMessage(PRIORITIZE_WORST_DECISIONS,
                        RC_LOG_PRIORITIZE_WORST));
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

        if (!entireArea)
            boxOverlay(viewportCanvas, x, y, w, h);
    }

    private void areaOverlay(
            final GameImage viewportCanvas,
            final int x, final int y, final int w, final int h
    ) {
        final int AREA_OPACITY = 0x80;
        drawAreaOverlay(viewportCanvas, focusArea,
                project, AREA_OPACITY, x, y, w, h);
    }

    private void boxOverlay(
            final GameImage viewportCanvas,
            final int x, final int y, final int w, final int h
    ) {
        final int BOX_OPACITY = 0xc0;
        drawBoxOverlay(viewportCanvas, currentBoxBounds(),
                project, BOX_OPACITY, x, y, w, h);
    }

    public FocusBoxMode getFocusBoxMode() {
        return focusBoxMode;
    }

    public int getDivsX() {
        return divsX;
    }

    public int getDivsY() {
        return divsY;
    }

    public boolean isWholeCanvas() {
        return wholeCanvas;
    }

    public boolean isEntireArea() {
        return entireArea;
    }
}
