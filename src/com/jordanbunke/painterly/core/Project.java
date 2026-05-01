package com.jordanbunke.painterly.core;

import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.core.domains.focus.FocusManager;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.core.paint.Canvas;

import java.nio.file.Path;

public final class Project implements ProgramContext {
    public final int scaleFactor, width, height;

    private final StrokeManager strokeManager;
    private final FocusManager focusManager;

    private final GameImage reference;
    private final Canvas canvas;

    private String name;
    private Path folder;

    private boolean painting;

    public Project(
            final String name, final Path folder,
            final GameImage reference, final int scaleFactor
    ) {
        this.name = name;
        this.folder = folder;
        this.reference = reference;

        // TODO - throw exception if invalid
        this.scaleFactor = scaleFactor;
        this.width = reference.getWidth() * scaleFactor;
        this.height = reference.getHeight() * scaleFactor;

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

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        // TODO
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public GameImage getReference() {
        return reference;
    }
}
