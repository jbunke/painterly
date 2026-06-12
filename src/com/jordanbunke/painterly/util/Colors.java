package com.jordanbunke.painterly.util;

import com.jordanbunke.painterly.settings.Settings;

import java.awt.*;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_THEME;

public final class Colors {
    private static final Color
            DEBUG = new Color(0x00ff00),
            TRANSPARENT = new Color(0, 0, 0, 0),
            VEIL = new Color(0xc0c0c0c0, true),
            INVALID_TEXT = new Color(0xd04040),
            INVALID_TEXT_BG = new Color(0xf0a0a0),
            HIGHLIGHT_OVERLAY = new Color(0x6080f0),
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

    public static Color dialogVeil() {
        return VEIL;
    }

    public static Color transparent() {
        return TRANSPARENT;
    }

    public static Color bg() {
        return WHITE; // TODO
    }

    public static Color black() {
        return BLACK;
    }

    public static Color white() {
        return WHITE;
    }

    public static Color invalidText() {
        return INVALID_TEXT;
    }

    public static Color invalidTextBG() {
        return INVALID_TEXT_BG;
    }

    public static Color highlightOverlay() {
        return HIGHLIGHT_OVERLAY;
    }

    public static Color debug() {
        return DEBUG;
    }
}
