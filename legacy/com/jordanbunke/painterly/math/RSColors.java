package com.jordanbunke.painterly.math;

import com.jordanbunke.delta_time.utility.math.RNG;

import java.awt.*;

public class RSColors {
    public static final int MAX = 255, MIN = 0;

    public static final Color
            WHITE = new Color(MAX, MAX, MAX, MAX),
            DEBUG = new Color(MIN, MAX, MIN, MAX);

    public static Color random() {
        return new Color(
                RNG.randomInRange(MIN, MAX + 1),
                RNG.randomInRange(MIN, MAX + 1),
                RNG.randomInRange(MIN, MAX + 1));
    }
}
