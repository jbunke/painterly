package com.jordanbunke.painterly;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time._core.GameManager;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.painterly.constants.Constants;
import com.jordanbunke.painterly.constants.PermLoaded;
import com.jordanbunke.painterly.painter.Painter;
import com.jordanbunke.painterly.settings.CommandParser;
import com.jordanbunke.painterly.settings.FocusBox;
import com.jordanbunke.painterly.settings.Palette;
import com.jordanbunke.painterly.settings.ProjectSettings;

import java.nio.file.Path;
import java.util.Set;

public class Launcher {

    public static void main(final String[] args) {
        processArgs(args);
        welcome();

        final GameImage reference = retrieveReference();
        final ProjectSettings projectSettings = initializeSettings();

        // launch painter and window
        final int[] displayDims = calculateDisplayDims(reference);
        final GameWindow window = new GameWindow(
                Constants.PROGRAM_NAME + " | " + projectSettings.getProjectName(),
                displayDims[Constants.WIDTH], displayDims[Constants.HEIGHT], PermLoaded.ICON,
                true, false, false
        );

        final Painter painter = new Painter(reference, projectSettings, displayDims);
        final GameManager manager = new GameManager(0, painter);
        final Program g = new Program(window, manager, Constants.HZ, Constants.FPS);
        g.getDebugger().muteChannel(GameDebugger.FRAME_RATE);

        commandCycle(painter);
    }

    private static void commandCycle(final Painter painter) {
        boolean quit = false;

        while (!quit) {
            final String command = Clink.promptForString("").trim().toLowerCase();
            quit = CommandParser.parse(command, painter);
        }

        System.exit(200);
    }

    private static void welcome() {
        Clink.writeUpdate("Welcome to " +
                Clink.highlight(Constants.PROGRAM_NAME, Clink.Mode.UPDATE) +
                ", a guided painting program by Jordan Bunke.");
    }

    private static void processArgs(final String[] args) {
        for (String arg : args)
            switch (arg.toLowerCase()) {
                case Constants.DISABLE_ANSI -> Clink.disableANSI();
                case Constants.ENABLE_ANSI -> Clink.enableANSI();
                case Constants.AUTO_ASSIGN -> Clink.autoAssignANSIEnabled();
            }
    }

    private static GameImage retrieveReference() {
        final Path filepath = Path.of(Clink.promptForString("Filepath for reference image?"));
        return GameImageIO.readImage(filepath);
    }

    private static ProjectSettings initializeSettings() {
        final String projectName = Clink.promptForString("Project name?");
        final int scaleUp = Clink.promptForIntOrDefault(
                "Scale factor for painting resolution?", 1);

        final ProjectSettings projectSettings = new ProjectSettings(projectName, scaleUp);

        projectSettings.setSampleProb(Clink.promptForDoubleOrDefault(
                "Sample colour from reference probability?",
                ProjectSettings.DEFAULT_SAMPLE_PROB));
        projectSettings.setPalette(Clink.promptForInt(
                "Color quantization intensity index? (from " +
                        Clink.highlight(String.valueOf(0), Clink.Mode.PROMPT) +
                        " for no quantization to " +
                        Clink.highlight(String.valueOf(Palette.values().length - 1), Clink.Mode.PROMPT) +
                        " for maximum quantization)"));
        projectSettings.getFocusBox().setMode(
                FocusBox.Mode.valueOf(
                        Clink.promptForOptionOrDefault(
                                "Focus box mode? (" +
                                        Clink.highlight(FocusBox.Mode.ITERATE.name(), Clink.Mode.PROMPT) +
                                        ", " + Clink.highlight(FocusBox.Mode.RANDOM.name(), Clink.Mode.PROMPT) +
                                        ", " + Clink.highlight(FocusBox.Mode.WORST.name(), Clink.Mode.PROMPT) +
                                        ", or " + Clink.highlight(FocusBox.Mode.FREE.name(), Clink.Mode.PROMPT) +
                                        ")",
                                Set.of(
                                        FocusBox.Mode.ITERATE.name(),
                                        FocusBox.Mode.RANDOM.name(),
                                        FocusBox.Mode.WORST.name(),
                                        FocusBox.Mode.FREE.name()
                                ), FocusBox.Mode.FREE.name(), s -> s.toUpperCase().trim())));
        projectSettings.getFocusBox().setDivisions(Clink.promptForIntOrDefault(
                "# of rows and columns for focus areas?", FocusBox.UNIVERSAL));
        // TODO: more mutable settings

        return projectSettings;
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
