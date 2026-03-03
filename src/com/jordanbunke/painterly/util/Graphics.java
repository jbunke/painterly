package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;

import java.nio.file.Path;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors");

    // TODO

    // IO

    public static GameImage readIcon(final String code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    public static GameImage readCursor(final Cursor cursor) {
        final Path cursorFile = CURSORS_FOLDER.resolve(
                cursor.name().toLowerCase() + ".png");
        return ResourceLoader.loadImageResource(cursorFile);
    }

    // TEXT & TEXT UI

    // TODO

    // ADDITIONAL

    public static GameImage drawTooltip(final String text) {
        // TODO

        return GameImage.dummy();
    }
}
