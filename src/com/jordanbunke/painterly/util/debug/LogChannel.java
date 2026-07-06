package com.jordanbunke.painterly.util.debug;

public enum LogChannel {
    PRIORITIZE_WORST_DECISIONS(true),
    SAVE_EXPORT(true),
    INTERVAL_STATS(true),
    FPS(false),
    RECENT_STROKES(false)
    ;

    public final boolean defaultValue;

    LogChannel(final boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
}
