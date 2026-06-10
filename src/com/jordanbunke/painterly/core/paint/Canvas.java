package com.jordanbunke.painterly.core.paint;

import algo.ImageScaling;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Colors;

public final class Canvas {
    private final GameImage sourceImage, scaledSource;
    private final int scaleFactor;

    // TODO
    private GameImage accepted;

    private boolean showSource;

    public Canvas(final Project project) {
        sourceImage = project.getSourceImage();
        scaleFactor = project.scaleFactor;
        scaledSource = scaleFactor == 1
                ? new GameImage(sourceImage)
                : ImageScaling.bicubic(sourceImage, scaleFactor);

        accepted = new GameImage(project.width, project.height);
        accepted.fill(Colors.white());
        // TODO - canvas texture?

        showSource = false;
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
