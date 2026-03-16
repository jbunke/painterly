package com.jordanbunke.painterly;

import com.jordanbunke.painterly.settings.RuntimeSettings;

public final class DevBuild {
    private static final String
            FLAG_OVERWRITE = "-o",
            FLAG_FPS = "-fps";

    public static void main(String[] args) {
        processArgs(args);
        Painterly.main(args);
    }

    private static void processArgs(final String[] args) {
        for (String arg : args)
            switch (arg) {
                case FLAG_OVERWRITE -> RuntimeSettings.setOverwrite(true);
                case FLAG_FPS -> RuntimeSettings.setFPS(true);
                // TODO - additional cases
            }
    }
}
