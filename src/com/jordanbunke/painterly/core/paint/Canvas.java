package com.jordanbunke.painterly.core.paint;

import algo.ImageScaling;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Colors;

public final class Canvas {
    private final Project project;
    private final GameImage scaledSource;

    // TODO
    private GameImage accepted;

    private boolean showSource;

    public Canvas(final Project project) {
        this.project = project;
        scaledSource = project.scaleFactor == 1
                ? new GameImage(project.getSourceImage())
                : ImageScaling.bicubic(project.getSourceImage(), project.scaleFactor);

        accepted = initializeCanvas();

        showSource = false;
    }

    private GameImage initializeCanvas() {
        final GameImage canvas = new GameImage(project.width, project.height);
        canvas.fill(Colors.white());

        // TODO - texture?

        return canvas.submit();
    }

    public boolean attemptStroke() {
        // TODO
        return false;
    }

    public GameImage getImageForViewport() {
        return showSource ? scaledSource : accepted;
    }

    public void toggleShowSource() {
        showSource = !showSource;
    }

    public boolean isShowSource() {
        return showSource;
    }
}
