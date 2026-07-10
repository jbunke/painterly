package com.jordanbunke.painterly.util;

import java.nio.file.Path;

public final class Constants {
    public static final String
            NAME_CODE = "name", VERSION_CODE = "version",
            IS_DEVBUILD_CODE = "devbuild",
            IS_DEMO_CODE = "demo",
            SPEC_FILENAME = "project.json",
            SOURCE_FILENAME = "source.png",
            PAINTING_FILENAME = "painting.png";

    public static final Path PROGRAM_FILE = Path.of("program.json"),
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
            MAX_ANGLE_SAMPLE_PROB = 0.7,
            MAX_ANGLE_VARIANCE = Math.PI / 8.,
            MIN_STROKE_BREADTH = 10d /* TODO - better as setting */,
            LINE_SOBEL_THRESHOLD = 0.7,
            LINE_BREADTH_PROB = 0.5,
            LINE_BREADTH_MULTIPLIER = 0.3,
            MIN_STROKE_LENGTH_MULTIPLIER = 0.25,
            MAX_STROKE_LENGTH_SIZE_RATIO = 0.25,
            PRIORITIZE_WORST_EXPONENT = 3d;

    public static final long MAX_CANVAS_PIXELS = 6000L * 6000L;

    public static final String PNG = "png", JPEG = "jpg";

    public static final String[] RASTER_FORMATS = new String[] { PNG, JPEG };

    public static final String
            NESTED_MENU_BAR_EXPANDER = ">",
            TYPING_CODE = "typing";
}
