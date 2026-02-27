package com.jordanbunke.painterly.painter;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameKeyEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.constants.Constants;
import com.jordanbunke.painterly.math.RSColors;
import com.jordanbunke.painterly.math.RSMath;
import com.jordanbunke.painterly.settings.FocusBox;
import com.jordanbunke.painterly.settings.Settings;

import java.awt.*;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    private int strokeCount, attemptCount, strokesInSlice, failedAttemptsInSlice;
    private double similarity;

    private int mouseDownX, mouseDownY;

    public Painter(final GameImage reference, final Settings settings, final int[] displayDims) {
        projectFolder = Constants.OUTPUT_FOLDER.resolve(settings.getProjectName());
        FileIO.safeMakeDirectory(projectFolder);

        this.reference = ImageProcessing.scale(reference, settings.getScaleUp());
        this.settings = settings;

        width = this.reference.getWidth();
        height = this.reference.getHeight();

        displayWidth = displayDims[Constants.WIDTH];
        displayHeight = displayDims[Constants.HEIGHT];

        showingReference = false;

        mouseDownX = 0;
        mouseDownY = 0;

        strokeCount = 0;
        attemptCount = 0;

        strokesInSlice = 0;
        failedAttemptsInSlice = 0;

        similarity = 0.;
        painting = new GameImage(width, height);
        init();
    }

    private void init() {
        painting.fillRectangle(RSColors.WHITE, 0, 0, width, height);
        painting.free();

        calculateStats();
    }

    public void overridePainting(final GameImage painting) {
        this.painting = painting;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();

        for (GameEvent e : unprocessed) {
            if (e instanceof GameMouseEvent mouseEvent) {
                final Coord2D mp = mouseEvent.mousePosition;

                if (mouseEvent.action == GameMouseEvent.Action.DOWN) {
                    mouseDownX = mp.x;
                    mouseDownY = mp.y;

                    mouseEvent.markAsProcessed();
                } else if (mouseEvent.action == GameMouseEvent.Action.UP) {
                    if (mp.x == mouseDownX && mp.y == mouseDownY &&
                            settings.getFocusBox().getMode() == FocusBox.Mode.CUSTOM)
                        settings.getFocusBox().setMode(FocusBox.Mode.FREE);
                    else {
                        final double sc = width / (double) displayWidth;
                        settings.getFocusBox().setCustomBounds(
                                (int)(mouseDownX * sc), (int)(mouseDownY * sc),
                                (int)(mp.x * sc), (int)(mp.y * sc), reference);
                    }

                    mouseEvent.markAsProcessed();
                }
            }
        }

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
        // set focus box modes
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.W, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().setMode(FocusBox.Mode.WORST)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.F, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().setMode(FocusBox.Mode.FREE)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.I, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().setMode(FocusBox.Mode.ITERATE)
        );
        eventLogger.checkForMatchingKeyStroke(
                GameKeyEvent.newKeyStroke(Key.R, GameKeyEvent.Action.PRESS),
                () -> settings.getFocusBox().setMode(FocusBox.Mode.RANDOM)
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
        attemptCount++;

        if (attemptCount >= Constants.MAX_ATTEMPTS)
            attemptCount = 0;

        final int[] bounds = settings.getFocusBox().bounds(reference);

        // 1: drawing position
        final int[] strokePos = RSMath.getPixelInBounds(bounds);

        // 2: stroke breadth, curvature, length, and breadth shortening
        final BrushStroke stroke = RSMath.generateStroke(strokePos, similarity, width, height);

        // 3: color
        final int[] colorCoordinates = RSMath.getPixelInBounds(bounds);
        final Color sample = reference.getColorAt(
                colorCoordinates[Constants.X], colorCoordinates[Constants.Y]);
        final Color c = settings.getPalette().quantize(
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
            strokesInSlice++;

            if (strokeCount % settings.getStatsTick() == 0) {
                calculateStats();
                strokesInSlice = 0;
                failedAttemptsInSlice = 0;
            }

            if (strokeCount % settings.getSaveTick() == 0)
                savePainting();
        } else {
            failedAttemptsInSlice++;
        }

        settings.getFocusBox().tryMode(strokeCount, attemptCount, reference, painting);
    }

    public void calculateStats() {
        similarity = RSMath.similarity(reference, painting, 0, 0, width, height);

        Clink.writeUpdate("Stroke count: " + Clink.highlight(String.valueOf(strokeCount), Clink.Mode.UPDATE));
        Clink.writeUpdate("Similarity: " + Clink.highlight((similarity * 100) + "%", Clink.Mode.UPDATE));
        final double attemptsInSlice = strokesInSlice + failedAttemptsInSlice;
        final double successRate = attemptsInSlice == 0d ? 0d : (100 * strokesInSlice) / attemptsInSlice;
        Clink.writeUpdate("Stroke success rate: " + Clink.highlight(successRate + "%", Clink.Mode.UPDATE));
        final String t = LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
        Clink.writeUpdate("Time: " + Clink.highlight(t.substring(0, t.indexOf(".")), Clink.Mode.UPDATE));
    }

    public void savePainting() {
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
        final int[] bounds = settings.getFocusBox().bounds(reference);

        final GameImage focusBoxImage = new GameImage(width, height);
        focusBoxImage.setColor(RSColors.DEBUG);
        focusBoxImage.drawRectangle(3,
                bounds[Constants.BOUND_X1], bounds[Constants.BOUND_Y1],
                bounds[Constants.BOUND_X2] - bounds[Constants.BOUND_X1],
                bounds[Constants.BOUND_Y2] - bounds[Constants.BOUND_Y1]);

        canvas.draw(focusBoxImage.submit(), 0, 0, displayWidth, displayHeight);
    }

    private void toggleShowingReference() {
        showingReference = !showingReference;
    }

    public Settings getSettings() {
        return settings;
    }
}
