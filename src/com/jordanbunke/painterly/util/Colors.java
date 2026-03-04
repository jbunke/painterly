package com.jordanbunke.painterly.util;

import java.awt.*;

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

        Color defaultColor;

        SystemColor(final Color defaultColor) {
            this.defaultColor = defaultColor;
        }
    }

    public enum Theme {
        DEFAULT(/* TODO */);

        private Color[] systemColors;

        Theme(final Color... colors) {
            final SystemColor[] sc = SystemColor.values();
            final int l = sc.length;

            systemColors = new Color[l];

            for (int i = 0; i < l; i++) {
                systemColors[i] = i < colors.length
                        ? colors[i] : sc[i].defaultColor;
            }
        }
    }

    // TODO - theme determination and system color retrieval

    public static Color bg() {
        return WHITE; // TODO
    }
}
