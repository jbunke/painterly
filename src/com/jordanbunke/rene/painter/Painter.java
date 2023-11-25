package com.jordanbunke.rene.painter;

import com.jordanbunke.delta_time.contexts.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameKeyEvent;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.rene.constants.Constants;
import com.jordanbunke.rene.settings.Settings;

public class Painter implements ProgramContext {

    // immutable
    private final GameImage reference;
    private final Settings settings;
    private final int canvasWidth, canvasHeight;

    // mutable
    private boolean showingReference;

    public Painter(final GameImage reference, final Settings settings, final int[] dims) {
        this.reference = reference;
        this.settings = settings;

        canvasWidth = dims[Constants.WIDTH];
        canvasHeight = dims[Constants.HEIGHT];

        showingReference = false;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // activate / deactivate
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.SPACE, GameKeyEvent.Action.PRESS),
                settings::toggleActive
        );
        // show reference
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.M, GameKeyEvent.Action.PRESS),
                this::toggleShowingReference
        );
        /* TODO
         * S to save
         * Arrow keys to move active subsection if mode allows
         * Draw bounding box with mouse arrow */
    }

    @Override
    public void update(final double deltaTime) {
        // TODO: decision-making logic: steps & execution
        // here
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.draw(showingReference
                        ? reference
                        : /* TODO */ new GameImage(canvasWidth, canvasHeight),
                0, 0, canvasWidth, canvasHeight);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {

    }

    private void toggleShowingReference() {
        showingReference = !showingReference;
    }
}
