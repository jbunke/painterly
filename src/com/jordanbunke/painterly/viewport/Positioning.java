package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;

/**
 * Positioning, i.e. zoom and anchor position, of the currently active project
 * in the viewport screen box
 * */
public final class Positioning {
    private static final double MIN_FTSR = 0.8, MAX_FTSR = 20d,
            MIDDLE = 0.5, MIN_ANCHOR = 0d, MAX_ANCHOR = 1d,
            SCROLL_ZOOM_RATE = 1.1;

    private double fitToScreenRatio, xAnchorRatio, yAnchorRatio;

    public Positioning() {
        fitToScreenRatio = MIN_FTSR;
        xAnchorRatio = MIDDLE;
        yAnchorRatio = MIDDLE;
    }

    public void draw(final GameImage viewportCanvas, final GameImage projectImage) {
        final double renderScale = determineRenderScale(
                projectImage.getWidth(), projectImage.getHeight(),
                viewportCanvas.getWidth(), viewportCanvas.getHeight());
        final int width = (int)(projectImage.getWidth() * renderScale),
                height = (int)(projectImage.getHeight() * renderScale),
                middleX = viewportCanvas.getWidth() / 2,
                middleY = viewportCanvas.getHeight() / 2,
                x = middleX - (int)(xAnchorRatio * width),
                y = middleY - (int)(yAnchorRatio * height);

        // TODO - overlays

        viewportCanvas.draw(projectImage, x, y, width, height);
    }

    private double determineRenderScale(
            final int projectWidth, final int projectHeight,
            final int viewportWidth, final int viewportHeight
    ) {
        final double xScale = (viewportWidth * fitToScreenRatio) / (double) projectWidth,
                yScale = (viewportHeight * fitToScreenRatio) / (double) projectHeight;

        return Math.min(xScale, yScale);
    }

    public void scrollZoom(final boolean in) {
        zoom(in, SCROLL_ZOOM_RATE);
    }

    public void zoom(final boolean in, final double zoomRate) {
        // TODO - account for target pixel

        if (in)
            fitToScreenRatio *= zoomRate;
        else
            fitToScreenRatio /= zoomRate;

        fitToScreenRatio = MathPlus.bounded(MIN_FTSR, fitToScreenRatio, MAX_FTSR);
    }

    private void setAnchor(final double xAnchorRatio, final double yAnchorRatio) {
        this.xAnchorRatio = MathPlus.bounded(MIN_ANCHOR, xAnchorRatio, MAX_ANCHOR);
        this.yAnchorRatio = MathPlus.bounded(MIN_ANCHOR, yAnchorRatio, MAX_ANCHOR);
    }
}
