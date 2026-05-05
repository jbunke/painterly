package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.Project;

public final class Canvas {
    private final GameImage reference;
    private final int scaleFactor;

    // TODO
    private GameImage accepted;

    public Canvas(final Project project) {
        reference = project.getSourceImage();
        scaleFactor = project.scaleFactor;

        accepted = new GameImage(project.width, project.height);
    }

    public boolean attemptStroke() {
        // TODO
        return false;
    }
}
