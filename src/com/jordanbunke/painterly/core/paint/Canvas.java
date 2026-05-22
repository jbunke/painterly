package com.jordanbunke.painterly.core.paint;

import algo.ImageScaling;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;

public final class Canvas {
    private final GameImage sourceImage, scaledSource;
    private final int scaleFactor;

    // TODO
    private GameImage accepted;

    public Canvas(final Project project) {
        sourceImage = project.getSourceImage();
        scaleFactor = project.scaleFactor;
        scaledSource = scaleFactor == 1
                ? new GameImage(sourceImage)
                : ImageScaling.bicubic(sourceImage, scaleFactor);

        accepted = new GameImage(project.width, project.height);
    }

    public boolean attemptStroke() {
        // TODO
        return false;
    }
}
