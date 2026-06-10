package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.image.GameImage;

public final class Positioning {
    private static final double MIN_FTSR = 0.8, MAX_FTSR = 20d,
            MIDDLE = 0.5, MIN_ANCHOR = 0d, MAX_ANCHOR = 1d;

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
}
