package com.jordanbunke.painterly.theme;

import com.jordanbunke.color_proc.ColorProc;

import java.awt.*;

public final class Colors {
    private static final Color
            DEBUG = new Color(0x00ff00),
            FAILURE = new Color(0xff0000),
            PURPLE = new Color(0xe000c0),
            TRANSPARENT = new Color(0, 0, 0, 0),
            VEIL = new Color(0xc0c0c0c0, true),
            INVALID_TEXT_BG = new Color(0xd04040),
            INVALID_TEXT = new Color(0xf0a0a0),
            HIGHLIGHT_OVERLAY = new Color(0x6080f0),
            BLACK = new Color(0x000000),
            WHITE = new Color(0xffffff),
            LIGHTER_GREY = new Color(0xc0c0c0),
            LIGHT_GREY = new Color(0xa0a0a0),
            GREY = new Color(0x808080),
            DARK_GREY = new Color(0x404040);

    public enum SystemColor {
        DARK(BLACK),
        MID_DARK(DARK_GREY),
        MID(GREY),
        MID_LIGHT(LIGHT_GREY),
        LIGHT_TEXT(WHITE),
        LIGHT_BG(LIGHTER_GREY);

        final Color defaultColor;

        SystemColor(final Color defaultColor) {
            this.defaultColor = defaultColor;
        }
    }

    public static Color systemColor(final SystemColor systemColor) {
        return systemColor.defaultColor;
    }

    public static Color dialogVeil() {
        return VEIL;
    }

    public static Color transparent() {
        return TRANSPARENT;
    }

    public static Color black() {
        return BLACK;
    }

    public static Color white() {
        return WHITE;
    }

    public static Color darkGrey() {
        return DARK_GREY;
    }

    public static Color grey() {
        return GREY;
    }

    public static Color lighterGrey() {
        return LIGHTER_GREY;
    }

    public static Color lightGrey() {
        return LIGHT_GREY;
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

    public static Color success() {
        return DEBUG;
    }

    public static Color failure() {
        return FAILURE;
    }

    public static Color purple() {
        return PURPLE;
    }

    public static Color focusArea(final int alpha) {
        // TODO
        return ColorProc.fromHSV(0.12, 0.6, 1d, alpha);
    }

    public static Color focusBox(final int alpha) {
        // TODO
        return ColorProc.fromHSV(0.4, 0.6, 1d, alpha);
    }

    public static Color debug() {
        return DEBUG;
    }
}
