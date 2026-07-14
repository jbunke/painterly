package com.jordanbunke.painterly.util;

import java.awt.*;
import java.net.URI;

public final class InternetUtils {
    public static void visitSite(final String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception ignored) {}
    }
}
