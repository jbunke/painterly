package com.jordanbunke.painterly.core;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.domains.debug.DebugData;
import com.jordanbunke.painterly.core.domains.focus.FocusManager;
import com.jordanbunke.painterly.core.domains.interval.ProgressManager;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.core.paint.Canvas;

import java.nio.file.Path;

public final class Project {
    public final double scaleFactor;
    public final int width, height;

    private String name;
    private Path folder;

    private final GameImage sourceImage;

    public final StrokeManager strokeManager;
    public final ProgressManager progressManager;
    public final FocusManager focusManager;
    public final DebugData debugData;
    public final Canvas canvas;

    private boolean painting;

    public Project(
            final String name, final Path folder,
            final GameImage sourceImage, final double scaleFactor
    ) {
        this.name = name;
        this.folder = folder;
        this.sourceImage = sourceImage;

        // TODO - throw exception if invalid
        this.scaleFactor = scaleFactor;
        this.width = (int)(sourceImage.getWidth() * scaleFactor);
        this.height = (int)(sourceImage.getHeight() * scaleFactor);

        canvas = new Canvas(this);
        debugData = new DebugData(this);
        strokeManager = new StrokeManager(this);
        focusManager = new FocusManager(this);
        progressManager = new ProgressManager(this);

        painting = false;
    }

    // TODO - load from archive / file

    public void toggleSimulation() {
        painting = !painting;
    }

    public void disable() {
        painting = false;
    }

    public void update() {
        // TODO - temp

        if (painting) attemptStroke();
    }

    private void attemptStroke() {
        final boolean strokeAccepted = canvas.attemptStroke(),
                intervalCompleted = strokeManager.tallyStroke(strokeAccepted);

        if (intervalCompleted)
            focusManager.tryUpdateBox();
    }

    public GameImage getSourceImage() {
        return sourceImage;
    }

    public boolean isPainting() {
        return painting;
    }

    public String getName() {
        return name;
    }
}
