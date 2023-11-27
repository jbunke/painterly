package com.jordanbunke.rene;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.game.Game;
import com.jordanbunke.delta_time.game.GameManager;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.rene.constants.Constants;
import com.jordanbunke.rene.constants.PermLoaded;
import com.jordanbunke.rene.painter.Painter;
import com.jordanbunke.rene.settings.FocusBox;
import com.jordanbunke.rene.settings.Palette;
import com.jordanbunke.rene.settings.Settings;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

public class Launcher {

    private static final String EMPTY = "";

    public static void main(final String[] args) {
        welcome();

        final GameImage reference = retrieveReference(args);
        final Settings settings = initializeSettings();

        // launch painter and window
        final int[] displayDims = calculateDisplayDims(reference);
        final GameWindow window = new GameWindow(
                Constants.PROGRAM_NAME + " | " + settings.getProjectName(),
                displayDims[Constants.WIDTH], displayDims[Constants.HEIGHT], PermLoaded.ICON,
                true, false, false
        );

        final Painter painter = new Painter(reference, settings, displayDims);
        final GameManager manager = new GameManager(0, painter);
        final Game g = new Game(window, manager, Constants.HZ, Constants.FPS);
        g.getDebugger().muteChannel(GameDebugger.FRAME_RATE);

        // TODO: command cycle
    }

    private static void welcome() {
        Clink.writeUpdate("Welcome to " +
                Clink.highlight(Constants.PROGRAM_NAME, Clink.Mode.UPDATE) +
                ", a guided painting program by Jordan Bunke.");
    }

    private static GameImage retrieveReference(final String[] args) {
        final String concatenated = Arrays.stream(args).
                reduce((a, b) -> a + " " + b).orElse(EMPTY);

        if (concatenated.equals(EMPTY)) {
            final Path filepath = Path.of(Clink.promptForString("Filepath for reference image?"));
            return GameImageIO.readImage(filepath);
        }

        return GameImageIO.readImage(Path.of(concatenated));
    }

    private static Settings initializeSettings() {
        final String projectName = Clink.promptForString("Project name?");
        final int scaleUp = Clink.promptForIntOrDefault(
                "Scale factor for painting resolution?", 1);

        final Settings settings = new Settings(projectName, scaleUp);

        settings.setSampleProb(Clink.promptForDoubleOrDefault(
                "Sample colour from reference probability?",
                Settings.DEFAULT_SAMPLE_PROB));
        settings.setPalette(Clink.promptForInt(
                "Color palettization intensity index? (from " +
                        Clink.highlight(String.valueOf(0), Clink.Mode.PROMPT) +
                        " for no palettization to " +
                        Clink.highlight(String.valueOf(Palette.values().length - 1), Clink.Mode.PROMPT) +
                        " for maximum palettization)"));
        settings.getFocusBox().setMode(
                FocusBox.Mode.valueOf(
                        Clink.promptForOptionOrDefault(
                                "Focus box mode? (" +
                                        Clink.highlight(FocusBox.Mode.ITINERANT.name(), Clink.Mode.PROMPT) +
                                        ", " + Clink.highlight(FocusBox.Mode.WORST.name(), Clink.Mode.PROMPT) +
                                        ", or " + Clink.highlight(FocusBox.Mode.FREE.name(), Clink.Mode.PROMPT) +
                                        ")",
                                Set.of(
                                        FocusBox.Mode.ITINERANT.name(),
                                        FocusBox.Mode.WORST.name(),
                                        FocusBox.Mode.FREE.name()
                                ), FocusBox.Mode.FREE.name(), s -> s.toUpperCase().trim())));
        settings.getFocusBox().setDivisions(Clink.promptForIntOrDefault(
                "# of rows and columns for focus areas?", FocusBox.UNIVERSAL));
        // TODO: more mutable settings

        return settings;
    }

    private static int[] calculateDisplayDims(final GameImage r) {
        final double ratio = r.getWidth() / (double) r.getHeight(), STANDARD = 16 / 9.;

        if (ratio <= STANDARD)
            return new int[] {
                    (int) Math.max(1., Constants.DIM_MAX_Y * ratio),
                    Constants.DIM_MAX_Y };

        return new int[] {
                (int) (Constants.DIM_MAX_Y * STANDARD),
                (int) (Constants.DIM_MAX_Y * (STANDARD / ratio))
        };
    }
}
