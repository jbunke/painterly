package com.jordanbunke.painterly;

import com.jordanbunke.painterly.settings.RuntimeSettings;

public final class DevBuild {
    private static final String
            FLAG_OVERWRITE = "-o",
            FLAG_FPS = "-fps",
            FLAG_CAN_DEBUG = "-d";

    public static void main(String[] args) {
        processArgs(args);
        Painterly.main(args);
    }

    private static void processArgs(final String[] args) {
        for (String arg : args)
            switch (arg) {
                case FLAG_OVERWRITE -> RuntimeSettings.setOverwrite(true);
                case FLAG_FPS -> RuntimeSettings.setFPS(true);
                case FLAG_CAN_DEBUG -> RuntimeSettings.setCanDebug(true);
                // TODO - additional cases
            }
    }
}
