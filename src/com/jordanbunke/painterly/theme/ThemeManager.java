package com.jordanbunke.painterly.theme;

import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Layout;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_THEME;

public final class ThemeManager {
    private static ThemeEnum currentTheme;

    static {
        currentTheme = Settings.get(SET_ID_THEME, ThemeEnum.class);
    }

    public static Theme get() {
        return currentTheme.theme;
    }

    public static void set(final ThemeEnum theme) {
        if (currentTheme == theme)
            return;

        currentTheme = theme;
        Settings.set(SET_ID_THEME, currentTheme);
        Layout.regenAll();
    }
}
