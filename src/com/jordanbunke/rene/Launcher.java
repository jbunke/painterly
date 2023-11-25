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
import com.jordanbunke.rene.settings.Settings;

import java.nio.file.Path;
import java.util.Arrays;

public class Launcher {

    private static final String EMPTY = "";

    public static void main(final String[] args) {
        welcome();

        final GameImage reference = retrieveReference(args);
        final Settings settings = initializeSettings();

        // launch painter and window
        final int[] dims = calculateDims(reference);
        final GameWindow window = new GameWindow(
                Constants.PROGRAM_NAME + " | " + settings.getProjectName(),
                dims[Constants.WIDTH], dims[Constants.HEIGHT], PermLoaded.ICON,
                true, false, false
        );

        final Painter painter = new Painter(reference, settings, dims);
        final GameManager manager = new GameManager(0, painter);
        final Game g = new Game(window, manager);
        g.getDebugger().muteChannel(GameDebugger.FRAME_RATE);

        // command cycle
    }

    private static void welcome() {
        Clink.write(Clink.CLI_TEXT_GREEN_BOLD + "Welcome to " +
                Clink.CLI_TEXT_YELLOW_BOLD + "René Sansartiste" +
                Clink.CLI_TEXT_GREEN_BOLD + ", an unguided painting program by Jordan Bunke." +
                Clink.CLI_TEXT_RESET, true);
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
        // TODO - more mutable settings

        return settings;
    }

    private static int[] calculateDims(final GameImage r) {
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
