package com.jordanbunke.rene.constants;

import java.awt.*;
import java.nio.file.Path;

public class Constants {
    public static final String PROGRAM_NAME = "René Sanse";

    public static final int DIM_MAX_Y = Toolkit.getDefaultToolkit().getScreenSize().height - 80;
    public static final int X = 0, Y = 1, WIDTH = 0, HEIGHT = 1,
            BOUND_X1 = 0, BOUND_Y1 = 1, BOUND_X2 = 2, BOUND_Y2 = 3;

    public static final double MAX_SIMILARITY = 1d, SIM_DAMP_EXPONENT = 4.5, HZ = 50d, FPS = 10d;

    public static final Path OUTPUT_FOLDER = Path.of("rs_output");
}
