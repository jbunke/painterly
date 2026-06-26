package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.tool.ToolManager;

/**
 * Positioning, i.e. zoom and anchor position, of the currently active project
 * in the viewport screen box
 * */
public final class Positioning {
    private static final double MIN_FTSR = 0.95, MAX_FTSR = 20d,
            MIDDLE = 0.5, MIN_ANCHOR = 0d, MAX_ANCHOR = 1d,
            SCROLL_ZOOM_RATE = 1.1, CLICK_ZOOM_RATE = 1.3;
    public static final Coord2D INVALID = new Coord2D(-1, -1);

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

            // Focus area and focus box overlays
            p.focusManager.drawOverlay(viewportCanvas, x, y, w, h);

            // Tool overlay
            ToolManager.getCurrentTool().drawOverlay(viewportCanvas, p, x, y, w, h);

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

    public void clickZoom(
            final boolean in, final Project p, final Coord2D mousePosInViewport
    ) {
        zoom(in, CLICK_ZOOM_RATE, p, true, mousePosInViewport);
    }

    public void scrollZoom(
            final boolean in, final Project p,
            final boolean adjustAnchor, final Coord2D mousePosInViewport
    ) {
        zoom(in, SCROLL_ZOOM_RATE, p, adjustAnchor, mousePosInViewport);
    }

    public void zoom(
            final boolean in, final double zoomRate, final Project p,
            final boolean adjustAnchor, final Coord2D mousePosInViewport
    ) {
        final Coord2D targetPixel = determineTargetPixel(p, mousePosInViewport);
        final double oldRatio = fitToScreenRatio;

        if (in)
            fitToScreenRatio *= zoomRate;
        else
            fitToScreenRatio /= zoomRate;

        fitToScreenRatio = MathPlus.bounded(MIN_FTSR, fitToScreenRatio, MAX_FTSR);

        if (adjustAnchor && isTargetPixelValid(targetPixel, p))
            adjustAnchorAfterZoom(oldRatio, targetPixel, p);
    }

    private void adjustAnchorAfterZoom(
            final double oldRatio, final Coord2D targetPixel,
            final Project p
    ) {
        final Coord2D oldAnchorPixel = new Coord2D(
                (int)(anchorRatioX * p.width),
                (int)(anchorRatioY * p.height));
        final double trueZoomRate = fitToScreenRatio / oldRatio;
        final int oldDX = oldAnchorPixel.x - targetPixel.x,
                oldDY = oldAnchorPixel.y - targetPixel.y,
                dx = (int)(oldDX / trueZoomRate),
                dy = (int)(oldDY / trueZoomRate);
        final Coord2D anchorPixel = targetPixel.displace(dx, dy);

        setAnchor(anchorPixel.x / (double) p.width,
                anchorPixel.y / (double) p.height);
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

    public Coord2D determineTargetPixel(
            final Project p, final Coord2D mousePosInViewport
    ) {
        final int mx = mousePosInViewport.x, my = mousePosInViewport.y,
                pw = p.width, ph = p.height,
                vw = Viewport.get().getWidth(),
                vh = Viewport.get().getHeight();

        final double renderScale = determineRenderScale(pw, ph, vw, vh);

        final int rw = (int)(pw * renderScale),
                rh = (int)(ph * renderScale),
                middleX = vw / 2, middleY = vh / 2,
                x0 = middleX - (int)(anchorRatioX * rw),
                y0 = middleY - (int)(anchorRatioY * rh),
                x = (int)(((mx - x0) / (double) rw) * pw),
                y = (int)(((my - y0) / (double) rh) * ph);

        return new Coord2D(x, y);
    }

    public static boolean isTargetPixelValid(
            final Coord2D targetPixel, final Project p
    ) {
        final int x = targetPixel.x, y = targetPixel.y,
                pw = p.width, ph = p.height;

        return x >= 0 && y >= 0 && x < pw && y < ph;
    }

    public static Coord2D boundTargetPixel(
            final Coord2D targetPixel, final Project p
    ) {
        final int x = targetPixel.x, y = targetPixel.y,
                pw = p.width, ph = p.height;

        return new Coord2D(MathPlus.bounded(0, x, pw - 1),
                MathPlus.bounded(0, y, ph - 1));
    }
}
