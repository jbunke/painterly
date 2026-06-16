package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.painterly.algo.ImageScaling;
import com.jordanbunke.painterly.algo.Sobel;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.settings.RuntimeSettings;
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

        if (RuntimeSettings.canDebug()) {
            stroke.setAccepted(accepted);
            project.debugData.addStroke(stroke);
        }

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
