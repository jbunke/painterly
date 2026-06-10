package com.jordanbunke.painterly.core;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.domains.focus.FocusManager;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.core.paint.Canvas;

import java.nio.file.Path;

public final class Project {
    public final int scaleFactor, width, height;

    private String name;
    private Path folder;

    private final GameImage sourceImage;

    public final StrokeManager strokeManager;
    public final FocusManager focusManager;
    public final Canvas canvas;

    private boolean painting;

    public Project(
            final String name, final Path folder,
            final GameImage sourceImage, final int scaleFactor
    ) {
        this.name = name;
        this.folder = folder;
        this.sourceImage = sourceImage;

        // TODO - throw exception if invalid
        this.scaleFactor = scaleFactor;
        this.width = sourceImage.getWidth() * scaleFactor;
        this.height = sourceImage.getHeight() * scaleFactor;

        canvas = new Canvas(this);
        strokeManager = new StrokeManager(this);
        focusManager = new FocusManager(this);

        painting = false;
    }

    // TODO - load from archive / file

    private void attemptStroke() {
        final boolean strokeAccepted = canvas.attemptStroke(),
                intervalCompleted = strokeManager.tallyStroke(strokeAccepted);

        if (intervalCompleted)
            focusManager.tryUpdateBox();
    }

    public StrokeManager getStrokeManager() {
        return strokeManager;
    }

    public GameImage getSourceImage() {
        return sourceImage;
    }
}
