package com.jordanbunke.painterly.util;

import java.nio.file.Path;

public final class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild";

    public static final Path PROGRAM_FILE = Path.of("program.json"),
            VERSION_FILE = Path.of("version"),
            INTERNAL_SETTINGS_FILEPATH = Path.of("data", ".settings.json");

    public static final double TICK_HZ = 60d, FPS = 60d;

    public static final int
            TEXTBOX_DEF_MAX_LENGTH = 20,
            TOOLTIP_TICKS = 24;

    public static final long MAX_CANVAS_PIXELS = 10000L * 10000L;

    public static final String PNG = "png", JPEG = "jpg";

    public static final String[] RASTER_FORMATS = new String[] { PNG, JPEG };

    public static final String TYPING_CODE = "typing";
}
