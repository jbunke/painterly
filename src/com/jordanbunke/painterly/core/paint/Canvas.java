package com.jordanbunke.painterly.core.paint;

import algo.ImageScaling;
import algo.Sobel;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Colors;

public final class Canvas {
    private final Project project;
    private final GameImage scaledSource, sobel;

    // TODO
    private GameImage painting;

    private boolean showSource;

    public Canvas(final Project project) {
        this.project = project;

        final GameImage source = project.getSourceImage();

        scaledSource = project.scaleFactor == 1
                ? new GameImage(source)
                : ImageScaling.bicubic(project.getSourceImage(), project.scaleFactor);
        sobel = Sobel.calculate(source);

        painting = blankCanvas();

        showSource = false;
    }

    private GameImage blankCanvas() {
        final GameImage canvas = new GameImage(project.width, project.height);
        canvas.fill(Colors.white());

        // TODO - texture?

        return canvas.submit();
    }

    public boolean attemptStroke() {
        final GameImage copy = new GameImage(painting);
        final RectBounds strokeBounds = PaintEngine.draw(project, copy);

        // 4: reference similarity comparison
        final double oldSim = PaintEngine.similarity(scaledSource, painting, strokeBounds);
        final double newSim = PaintEngine.similarity(scaledSource, copy, strokeBounds);

        final boolean accepted = newSim > oldSim;

        if (accepted)
            painting = copy;

        return accepted;
    }

    public double globalSimilarity() {
        return PaintEngine.similarity(scaledSource, painting,
                new RectBounds(0, project.width, 0, project.height));
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

    public boolean isShowSource() {
        return showSource;
    }
}
