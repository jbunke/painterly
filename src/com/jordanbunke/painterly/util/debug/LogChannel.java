package com.jordanbunke.painterly.util.debug;

import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.settings.Settings;

import java.util.function.Consumer;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.settings.Settings.SettingID.*;

public enum LogChannel {
    FOCUS_BOX_REASONING(SET_ID_LOG_CHANNEL_FOCUS_BOX_REASONING_OBD,
            RC_CHANNEL_ID_FOCUS_BOX_REASONING),
    SAVE_EXPORT(true, RC_CHANNEL_ID_SAVE_EXPORT),
    INTERVAL_STATS(SET_ID_LOG_CHANNEL_INTERVAL_STATS_OBD,
            RC_CHANNEL_ID_INTERVAL_STATS),
    FPS(false, RC_NA),
    RECENT_STROKES(false, RC_NA),
    ;

    public final boolean defaultValue;
    public final ResourceCode channelCode;
    public final Consumer<Boolean> onSet;

    LogChannel(final boolean defaultValue, final ResourceCode channelCode) {
        this.defaultValue = defaultValue;
        this.channelCode = channelCode;
        this.onSet = b -> {};
    }

    LogChannel(final Settings.SettingID id, final ResourceCode channelCode) {
        this.defaultValue = Settings.get(id, Boolean.class);
        this.channelCode = channelCode;
        this.onSet = b -> Settings.set(id, b);
    }
}
