package com.jordanbunke.rene.painter;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.contexts.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameKeyEvent;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.RNG;
import com.jordanbunke.rene.constants.Constants;
import com.jordanbunke.rene.math.RSColors;
import com.jordanbunke.rene.math.RSMath;
import com.jordanbunke.rene.settings.Settings;

import java.awt.*;
import java.nio.file.Path;

public class Painter implements ProgramContext {

    // immutable
    private final Path projectFolder;
    private final GameImage reference;
    private final Settings settings;
    private final int width, height, displayWidth, displayHeight;

    // mutable
    private boolean showingReference;

    // updated
    private GameImage painting;
    private int strokeCount;
    private double similarity;

    public Painter(final GameImage reference, final Settings settings, final int[] displayDims) {
        projectFolder = Constants.OUTPUT_FOLDER.resolve(settings.getProjectName());
        FileIO.safeMakeDirectory(projectFolder);

        this.reference = ImageProcessing.scale(reference, settings.getScaleUp());
        this.settings = settings;

        width = reference.getWidth();
        height = reference.getHeight();

        displayWidth = displayDims[Constants.WIDTH];
        displayHeight = displayDims[Constants.HEIGHT];

        showingReference = false;

        strokeCount = 0;
        similarity = 0.;
        painting = new GameImage(width, height);
        init();
    }

    private void init() {
        painting.fillRectangle(RSColors.WHITE, 0, 0, width, height);
        painting.free();

        calculateSimilarity();
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
        // arrow keys for free focus box manipulation
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.DOWN_ARROW, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().adjustCoordinates(0, 1)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.UP_ARROW, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().adjustCoordinates(0, -1)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.RIGHT_ARROW, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().adjustCoordinates(1, 0)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.LEFT_ARROW, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().adjustCoordinates(-1, 0)
        );
        // save
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.S, GameKeyEvent.Action.PRESS),
                this::savePainting
        );
    }

    @Override
    public void update(final double deltaTime) {
        if (settings.isActive())
            attemptStroke();
    }

    private void attemptStroke() {
        final int[] bounds = settings.getFocusBox().bounds(reference);

        // 1: drawing position
        final int[] strokePos = RSMath.getPixelInBounds(bounds);

        // 2: stroke breadth, curvature, length, and breadth shortening
        final BrushStroke stroke = RSMath.generateStroke(strokePos, similarity, width, height);

        // 3: color
        final int[] colorCoordinates = RSMath.getPixelInBounds(bounds);
        final Color sample = ImageProcessing.colorAtPixel(
                reference, colorCoordinates[Constants.X], colorCoordinates[Constants.Y]
        );
        final Color c = settings.getPalette().palettize(
                RNG.prob(settings.getSampleProb()) ? sample : RSColors.random());

        final GameImage modified = new GameImage(painting);
        final int[] strokeBounds = stroke.draw(modified, c);

        // 4: reference similarity comparison
        final double oldSim = RSMath.similarity(reference, painting, strokeBounds);
        final double newSim = RSMath.similarity(reference, modified, strokeBounds);

        // 5: draw or discard & stats update
        if (newSim > oldSim) {
            painting = modified;
            strokeCount++;

            if (strokeCount % settings.getStrokesToCalculateSimilarity() == 0)
                calculateSimilarity();

            if (strokeCount % settings.getStrokesToSavePainting() == 0)
                savePainting();

            settings.getFocusBox().tryMode(strokeCount, reference, painting);
        }
    }

    private void calculateSimilarity() {
        similarity = RSMath.similarity(reference, painting, 0, 0, width, height);

        Clink.writeUpdate("Stroke count: " + Clink.highlight(String.valueOf(strokeCount), Clink.Mode.UPDATE));
        Clink.writeUpdate("Similarity: " + Clink.highlight((similarity * 100) + "%", Clink.Mode.UPDATE));
    }

    private void savePainting() {
        final String project = settings.getProjectName();

        GameImageIO.writeImage(projectFolder.resolve(project + " painting.png"), painting);
        GameImageIO.writeImage(projectFolder.resolve(project + " reference.png"), reference);

        Clink.writeUpdate("Saved project \"" + project + "\"");
    }

    @Override
    public void render(final GameImage canvas) {
        canvas.draw(showingReference ? reference : painting,
                0, 0, displayWidth, displayHeight);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {
        if (!showingReference) {
            final int[] bounds = settings.getFocusBox().bounds(reference);

            final GameImage focusBoxImage = new GameImage(width, height);
            focusBoxImage.setColor(RSColors.DEBUG);
            focusBoxImage.drawRectangle(10,
                    bounds[Constants.BOUND_X1], bounds[Constants.BOUND_Y1],
                    bounds[Constants.BOUND_X2] - bounds[Constants.BOUND_X1],
                    bounds[Constants.BOUND_Y2] - bounds[Constants.BOUND_Y1]);

            canvas.draw(focusBoxImage.submit(), 0, 0, displayWidth, displayHeight);
        }
    }

    private void toggleShowingReference() {
        showingReference = !showingReference;
    }
}
