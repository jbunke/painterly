package com.jordanbunke.painterly.settings.update;

import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.settings.Settings;

public final class VersionHandler {
    public static void startup() {
        // TODO - determine startup messages based on last opened version

        // Update last opened version to current version
        Settings.set(Settings.SET_ID_VERSION, ProgramInfo.getVersion());
    }
}
