package com.jordanbunke.painterly.settings;

public final class RuntimeSettings {
    private enum BoolSettings {
        OVERWRITE_PROGRAM_FILE(false),
        WRITE_FPS(true),
        CAN_DEBUG(false),
        PROFILER_ON(false);

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

    public static boolean isFPS() {
        return BoolSettings.WRITE_FPS.enabled;
    }

    public static void setFPS(final boolean value) {
        BoolSettings.WRITE_FPS.set(value);
    }

    public static boolean canDebug() {
        return BoolSettings.CAN_DEBUG.enabled;
    }

    public static void setCanDebug(final boolean value) {
        BoolSettings.CAN_DEBUG.set(value);
    }

    public static boolean isProfilerOn() {
        return BoolSettings.PROFILER_ON.enabled;
    }

    public static void setProfilerOn(final boolean value) {
        BoolSettings.PROFILER_ON.set(value);
    }

    public static void toggleProfilerOn() {
        setProfilerOn(canDebug() && !isProfilerOn());
    }
}
