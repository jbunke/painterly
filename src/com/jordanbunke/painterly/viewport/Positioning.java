package com.jordanbunke.painterly.viewport;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.RectBounds;
import com.jordanbunke.painterly.tool.ToolManager;

/**
 * Positioning, i.e. zoom and anchor position, of the currently active project
 * in the viewport screen box
 * */
public final class Positioning {
    private static final double MIN_FTSR = 0.475, DEF_FTSR = 0.95, MAX_FTSR = 20d,
            MIDDLE = 0.5, MIN_ANCHOR = 0d, MAX_ANCHOR = 1d,
            SCROLL_ZOOM_RATE = 1.1, CLICK_ZOOM_RATE = 1.3;
    public static final Coord2D INVALID = new Coord2D(-1, -1);

    private final Project project;

    private double fitToScreenRatio, anchorRatioX, anchorRatioY;

    public Positioning(final Project project) {
        this.project = project;

        fitToScreenRatio = DEF_FTSR;
        anchorRatioX = MIDDLE;
        anchorRatioY = MIDDLE;
    }

    public void draw(final GameImage viewportCanvas) {
        draw(viewportCanvas, project.width, project.height,
                (x, y, w, h) -> {
            viewportCanvas.draw(project.canvas.getImageForViewport(), x, y, w, h);

            // Focus area and focus box overlays
            project.focusManager.drawOverlay(viewportCanvas, x, y, w, h);

            // Tool overlay
            ToolManager.getCurrentTool().drawOverlay(
                    viewportCanvas, project, x, y, w, h);

            // TODO - future, additional overlays here
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
            final boolean in, final Coord2D mousePosInViewport
    ) {
        zoom(in, CLICK_ZOOM_RATE, true, mousePosInViewport);
    }

    public void scrollZoom(
            final boolean in, final boolean adjustAnchor,
            final Coord2D mousePosInViewport
    ) {
        zoom(in, SCROLL_ZOOM_RATE, adjustAnchor, mousePosInViewport);
    }

    public void zoom(
            final boolean in, final double zoomRate,
            final boolean adjustAnchor, final Coord2D mousePosInViewport
    ) {
        final Coord2D targetPixel = determineTargetPixel(mousePosInViewport);
        final double oldRatio = fitToScreenRatio;

        if (in)
            setFitToScreenRatio(fitToScreenRatio * zoomRate);
        else
            setFitToScreenRatio(fitToScreenRatio / zoomRate);

        if (adjustAnchor && isTargetPixelValid(targetPixel))
            adjustAnchorAfterZoom(oldRatio, targetPixel);
    }

    private void adjustAnchorAfterZoom(
            final double oldRatio, final Coord2D targetPixel
    ) {
        final Coord2D oldAnchorPixel = new Coord2D(
                (int)(anchorRatioX * project.width),
                (int)(anchorRatioY * project.height));
        final double trueZoomRate = fitToScreenRatio / oldRatio;
        final int oldDX = oldAnchorPixel.x - targetPixel.x,
                oldDY = oldAnchorPixel.y - targetPixel.y,
                dx = (int)(oldDX / trueZoomRate),
                dy = (int)(oldDY / trueZoomRate);
        final Coord2D anchorPixel = targetPixel.displace(dx, dy);

        setAnchor(anchorPixel.x / (double) project.width,
                anchorPixel.y / (double) project.height);
    }

    private void setAnchor(final double anchorRatioX, final double anchorRatioY) {
        this.anchorRatioX = MathPlus.bounded(MIN_ANCHOR, anchorRatioX, MAX_ANCHOR);
        this.anchorRatioY = MathPlus.bounded(MIN_ANCHOR, anchorRatioY, MAX_ANCHOR);
    }

    private void setFitToScreenRatio(final double fitToScreenRatio) {
        this.fitToScreenRatio = MathPlus.bounded(
                MIN_FTSR, fitToScreenRatio, MAX_FTSR);
    }

    public void reset() {
        setFitToScreenRatio(DEF_FTSR);
        setAnchor(MIDDLE, MIDDLE);
    }

    // TODO - fix
    public void fitToFocusArea() {
        if (project.focusManager.isWholeCanvas()) {
            reset();
            return;
        }

        final RectBounds focusArea = project.focusManager.getFocusArea();
        final int pw = project.width, ph = project.height,
                fx = focusArea.left(), fy = focusArea.top(),
                fw = focusArea.width(), fh = focusArea.height(),
                mx = fx + (fw / 2), my = fy + (fh / 2);
        final double anchorRatioX = mx / (double) pw,
                anchorRatioY = my / (double) ph,
                fitRatioX = (pw / (double) fw) * DEF_FTSR,
                fitRatioY = (ph / (double) fh) * DEF_FTSR,
                fitToScreenRatio = Math.min(fitRatioX, fitRatioY);

        setFitToScreenRatio(fitToScreenRatio);
        setAnchor(anchorRatioX, anchorRatioY);
    }

    public double getAnchorRatioX() {
        return anchorRatioX;
    }

    public double getAnchorRatioY() {
        return anchorRatioY;
    }

    public void pan(
            final int mouseDX, final int mouseDY,
            final double initAnchorRatioX, final double initAnchorRatioY
    ) {
        final double renderScale = determineRenderScale(
                project.width, project.height,
                Viewport.get().getWidth(), Viewport.get().getHeight());

        final int renderWidth = (int)(project.width * renderScale),
                renderHeight = (int)(project.height * renderScale);

        final double anchorDX = -mouseDX / (double) renderWidth,
                anchorDY = -mouseDY / (double) renderHeight;

        setAnchor(initAnchorRatioX + anchorDX, initAnchorRatioY + anchorDY);
    }

    public Coord2D determineScreenPixel(final Coord2D targetPixel) {
        final Viewport v = Viewport.get();
        final int tx = targetPixel.x, ty = targetPixel.y,
                pw = project.width, ph = project.height,
                vx = v.getX(), vy = v.getY(),
                vw = v.getWidth(), vh = v.getHeight();

        final double renderScale = determineRenderScale(pw, ph, vw, vh);

        final int rw = (int)(pw * renderScale),
                rh = (int)(ph * renderScale),
                middleX = vw / 2, middleY = vh / 2,
                x0 = vx + middleX - (int)(anchorRatioX * rw),
                y0 = vy + middleY - (int)(anchorRatioY * rh),
                x = x0 + (int)((tx / (double) pw) * rw),
                y = y0 + (int)((ty / (double) ph) * rh);

        return new Coord2D(x, y);
    }

    public Coord2D determineTargetPixel(final Coord2D mousePosInViewport) {
        final int mx = mousePosInViewport.x, my = mousePosInViewport.y,
                pw = project.width, ph = project.height,
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

    public boolean isTargetPixelValid(final Coord2D targetPixel) {
        final int x = targetPixel.x, y = targetPixel.y;

        return x >= 0 && y >= 0 && x < project.width && y < project.height;
    }

    public Coord2D boundTargetPixel(final Coord2D targetPixel) {
        final int x = targetPixel.x, y = targetPixel.y;

        return new Coord2D(MathPlus.bounded(0, x, project.width - 1),
                MathPlus.bounded(0, y, project.height - 1));
    }
}
