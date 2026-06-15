package com.jordanbunke.painterly.core.domains.focus;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.util.Constants;

public final class FocusManager {
    private final Project project;

    private FocusBoxMode focusBoxMode;
    private boolean entireArea;
    private RectBounds focusArea;
    private int divsX, divsY, x, y, maxDivsX, maxDivsY;

    public FocusManager(final Project project) {
        this.project = project;

        focusArea = new RectBounds(0, project.width, 0, project.height);
        entireArea = true;
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

            entireArea = true;
            divsX = 1;
            divsY = 1;
            x = 0;
            y = 0;
            determineDivMaxima();
        }
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
        // TODO
    }
}
