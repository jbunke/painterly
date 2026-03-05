package com.jordanbunke.painterly.util;

import com.jordanbunke.painterly.settings.Settings;

import java.awt.*;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_THEME;

public final class Colors {
    private static final Color
            BLACK = new Color(0x000000),
            WHITE = new Color(0xffffff),
            LIGHT_GREY = new Color(0xc0c0c0),
            GREY = new Color(0x808080),
            DARK_GREY = new Color(0x404040);

    public enum SystemColor {
        DARK(BLACK),
        MID_DARK(DARK_GREY),
        MID(GREY),
        MID_LIGHT(LIGHT_GREY),
        LIGHT(WHITE);

        final Color defaultColor;

        SystemColor(final Color defaultColor) {
            this.defaultColor = defaultColor;
        }
    }

    public enum Theme {
        DEFAULT(/* TODO */);

        private final Color[] systemColors;

        Theme(final Color... colors) {
            final SystemColor[] sc = SystemColor.values();
            final int l = sc.length;

            systemColors = new Color[l];

            for (int i = 0; i < l; i++) {
                systemColors[i] = i < colors.length
                        ? colors[i] : sc[i].defaultColor;
            }
        }

        public String id() {
            return name().toLowerCase();
        }

        public static Theme fromID(final String id) {
            try {
                return Theme.valueOf(id.toUpperCase());
            } catch (IllegalArgumentException iae) {
                return null;
            }
        }
    }

    private static Theme getTheme() {
        return Settings.get(SET_ID_THEME, Theme.class);
    }

    public static Color systemColor(final SystemColor systemColor) {
        return getTheme().systemColors[systemColor.ordinal()];
    }

    public static Color bg() {
        return WHITE; // TODO
    }
}
