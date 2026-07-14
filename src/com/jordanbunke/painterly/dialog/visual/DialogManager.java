package com.jordanbunke.painterly.dialog.visual;

import java.util.function.Supplier;

public final class DialogManager {
    private static PopUpDialog dialog;
    private static Supplier<PopUpDialog> lastSource;

    static {
        dialog = null;
    }

    public static void set(final Supplier<PopUpDialog> source) {
        lastSource = source;
        dialog = source.get();
    }

    public static void close() {
        dialog = null;
    }

    public static PopUpDialog get() {
        return dialog;
    }

    public static boolean has() {
        return dialog != null;
    }

    public static void regen() {
        if (has())
            dialog = lastSource.get();
    }
}
