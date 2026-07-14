package com.jordanbunke.painterly.util;

import java.nio.file.Path;

public final class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild",
            IS_DEMO_CODE = "demo",
            IS_RELEASE_CODE = "release",
            SPEC_FILENAME = "project.json",
            SOURCE_FILENAME = "source.png",
            PAINTING_FILENAME = "painting.png";

    public static final Path PROGRAM_FILE = Path.of("program.json"),
            RELEASE_FILE = Path.of("release"),
            VERSION_FILE = Path.of("version"),
            INTERNAL_SETTINGS_FILEPATH = Path.of("data", ".settings.json");

    public static final double TICK_HZ = 60d, FPS = 60d;

    public static final int
            LOADING_LABEL_TICKS = 20,
            TEXTBOX_DEF_MAX_LENGTH = 20,
            TOOLTIP_TICKS = 24,
            MAX_RECENT_STROKES_DEBUG = 10,
            MAX_BOX_DIVS = 40 /* TODO - test */,
            MIN_INTERVAL_TARGET = 1,
            MAX_INTERVAL_TARGET = 250,
            DEBUG_MESSAGE_FRAMES = (int)(5 * FPS),
            MIN_AUTOSAVE_FREQUENCY = 250,
            DEF_AUTOSAVE_FREQUENCY = 1000,
            MAX_AUTOSAVE_FREQUENCY = 5000,
            MAX_PROJECTS_ALLOWED = 10;

    public static final double
            PRIORITIZE_WORST_EXPONENT = 3d,
            MAX_W_TO_H_RATIO = 3d;

    public static final long MAX_CANVAS_PIXELS = 3500L * 3500L;

    public static final String PNG = "png", JPEG = "jpg";

    public static final String[] RASTER_FORMATS = new String[] { PNG, JPEG };

    public static final String
            NESTED_MENU_BAR_EXPANDER = ">",
            TYPING_CODE = "typing";
}
