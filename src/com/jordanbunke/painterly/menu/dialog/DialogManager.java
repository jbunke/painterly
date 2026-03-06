package com.jordanbunke.painterly.menu.dialog;

public final class DialogManager {
    // TODO
    private static PopUpDialog DIALOG;

    static {
        DIALOG = null;
    }

    public static PopUpDialog get() {
        return DIALOG;
    }

    public static boolean has() {
        return DIALOG != null;
    }
}
