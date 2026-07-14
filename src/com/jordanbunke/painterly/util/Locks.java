package com.jordanbunke.painterly.util;

public final class Locks {
    private static boolean hover, typing;

    public static boolean canHover() {
        return !hover;
    }

    public static void resetHover() {
        hover = false;
    }

    public static void hover() {
        hover = true;
    }
}
