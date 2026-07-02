package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.painterly.algo.ImageScaling;
import com.jordanbunke.painterly.algo.Sobel;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.debug.LogChannel;
import com.jordanbunke.painterly.util.debug.LogManager;

public final class Canvas {
    private final Project project;
    private final GameImage scaledSource, sobel;

    // TODO
    private GameImage painting;

    private boolean showSource;

    public Canvas(final Project project) {
        this(project, blankCanvas(project.width, project.height));
    }

    public Canvas(final Project project, final GameImage painting) {
        this.project = project;

        final GameImage source = project.getSourceImage();

        scaledSource = project.scaleFactor == 1d
                ? new GameImage(source)
                : ImageScaling.bicubic(project.getSourceImage(), project.scaleFactor);
        sobel = Sobel.calculate(source);

        this.painting = painting;

        showSource = false;
    }

    private static GameImage blankCanvas(final int width, final int height) {
        final GameImage canvas = new GameImage(width, height);
        canvas.fill(Colors.white());

        // TODO - canvas texture?

        return canvas.submit();
    }

    public boolean attemptStroke() {
        final GameImage copy = new GameImage(painting);
        final BrushStroke stroke = PaintEngine.draw(project, copy);
        final RectBounds strokeBounds =
                stroke.affectedArea(copy.getWidth(), copy.getHeight());

        // 4: reference similarity comparison
        final double
                oldSim = PaintEngine.similarity(scaledSource, painting, strokeBounds),
                newSim = PaintEngine.similarity(scaledSource, copy, strokeBounds);

        final boolean accepted = newSim > oldSim;

        if (accepted)
            painting = copy;

        stroke.setAccepted(accepted);

        if (LogManager.isChannelActive(LogChannel.RECENT_STROKE_ATTEMPTS))
            project.debugData.addStroke(stroke);

        return accepted;
    }

    public void deleteActiveBounds() {
        final RectBounds bounds = project.focusManager.activeBounds();
        final int TRANSPARENT = Colors.transparent().getRGB();

        final GameImage canvas = blankCanvas(project.width, project.height),
                copy = new GameImage(painting);

        for (int x = bounds.left(); x < bounds.right(); x++)
            for (int y = bounds.top(); y < bounds.bottom(); y++)
                copy.setRGB(x, y, TRANSPARENT);

        canvas.draw(copy.submit());
        painting = canvas;

        project.progressManager.update();
    }

    public double globalSimilarity() {
        return similarity(new RectBounds(0, project.width, 0, project.height));
    }

    public double similarity(final RectBounds bounds) {
        return PaintEngine.similarity(scaledSource, painting, bounds);
    }

    public GameImage getImageForViewport() {
        return showSource ? scaledSource : painting;
    }

    public void toggleShowSource() {
        showSource = !showSource;
    }

    public GameImage getSobel() {
        return sobel;
    }

    public GameImage getPainting() {
        return painting;
    }

    public boolean isShowSource() {
        return showSource;
    }
}
