package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.painterly.core.Project;

/**
 * Positioning, i.e. zoom and anchor position, of the currently active project
 * in the viewport screen box
 * */
public final class Positioning {
    private static final double MIN_FTSR = 0.95, MAX_FTSR = 20d,
            MIDDLE = 0.5, MIN_ANCHOR = 0d, MAX_ANCHOR = 1d,
            SCROLL_ZOOM_RATE = 1.1;

    private double fitToScreenRatio, anchorRatioX, anchorRatioY;

    public Positioning() {
        fitToScreenRatio = MIN_FTSR;
        anchorRatioX = MIDDLE;
        anchorRatioY = MIDDLE;
    }

    public void draw(final GameImage viewportCanvas, final Project p) {
        draw(viewportCanvas, p.width, p.height,
                (x, y, w, h) -> {
            viewportCanvas.draw(p.canvas.getImageForViewport(), x, y, w, h);
            // TODO - overlays
        });
    }

    public void draw(
            final GameImage viewportCanvas,
            final int projectWidth, final int projectHeight,
            final IProjection projection
    ) {
        final double renderScale = determineRenderScale(
                projectWidth, projectHeight,
                viewportCanvas.getWidth(), viewportCanvas.getHeight());
        final int width = (int)(projectWidth * renderScale),
                height = (int)(projectHeight * renderScale),
                middleX = viewportCanvas.getWidth() / 2,
                middleY = viewportCanvas.getHeight() / 2,
                x = middleX - (int)(anchorRatioX * width),
                y = middleY - (int)(anchorRatioY * height);

        projection.project(x, y, width, height);
    }

    private double determineRenderScale(
            final int projectWidth, final int projectHeight,
            final int viewportWidth, final int viewportHeight
    ) {
        final double scaleX = (viewportWidth * fitToScreenRatio) / (double) projectWidth,
                scaleY = (viewportHeight * fitToScreenRatio) / (double) projectHeight;

        return Math.min(scaleX, scaleY);
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

    private void setAnchor(final double anchorRatioX, final double anchorRatioY) {
        this.anchorRatioX = MathPlus.bounded(MIN_ANCHOR, anchorRatioX, MAX_ANCHOR);
        this.anchorRatioY = MathPlus.bounded(MIN_ANCHOR, anchorRatioY, MAX_ANCHOR);
    }

    public void reset() {
        fitToScreenRatio = MIN_FTSR;
        setAnchor(MIDDLE, MIDDLE);
    }

    public double getAnchorRatioX() {
        return anchorRatioX;
    }

    public double getAnchorRatioY() {
        return anchorRatioY;
    }

    public void pan(
            final Project p, final int mouseDX, final int mouseDY,
            final double initAnchorRatioX, final double initAnchorRatioY
    ) {
        final double renderScale = determineRenderScale(p.width, p.height,
                Viewport.get().getWidth(), Viewport.get().getHeight());

        final int renderWidth = (int)(p.width * renderScale),
                renderHeight = (int)(p.height * renderScale);

        final double anchorDX = -mouseDX / (double) renderWidth,
                anchorDY = -mouseDY / (double) renderHeight;

        setAnchor(initAnchorRatioX + anchorDX, initAnchorRatioY + anchorDY);
    }
}
