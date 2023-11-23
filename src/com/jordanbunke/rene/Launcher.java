package com.jordanbunke.rene;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.rene.settings.Settings;

import java.nio.file.Path;
import java.util.Arrays;

public class Launcher {

    private static final String EMPTY = "";

    public static void main(final String[] args) {
        welcome();

        final GameImage reference = retrieveReference(args);
        final Settings settings = initializeSettings();

        // TODO: launch window, start painter, command cycle
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
}
