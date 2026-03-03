package com.jordanbunke.painterly.settings;

public final class RuntimeSettings {
    private enum BoolSettings {
        OVERWRITE_PROGRAM_FILE(false);

        private boolean enabled;

        BoolSettings(final boolean defaultVal) {
            enabled = defaultVal;
        }

        public void set(final boolean value) {
            enabled = value;
        }
    }

    // TODO - int settings enum

    public static boolean isOverwrite() {
        return BoolSettings.OVERWRITE_PROGRAM_FILE.enabled;
    }

    public static void setOverwrite(final boolean value) {
        BoolSettings.OVERWRITE_PROGRAM_FILE.set(value);
    }
}
