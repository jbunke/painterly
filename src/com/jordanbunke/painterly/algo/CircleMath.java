package com.jordanbunke.painterly.algo;

import com.jordanbunke.delta_time.utility.math.RNG;

public final class CircleMath {
    public static final double CIRCLE = 2 * Math.PI;

    public static double randomAngle() {
        return fractionOfCircle(RNG.randomInRange(0.0, 1.0));
    }

    public static double fractionOfCircle(final double ratio) {
        return ratio * CIRCLE;
    }

    public static double augmentAngle(final double t, final double dt) {
        return normalizeAngle(t + dt);
    }

    public static double normalizeAngle(double angle) {
        while (angle >= CIRCLE)
            angle -= CIRCLE;
        while (angle < 0)
            angle += CIRCLE;

        return angle;
    }

    public static double angleDifference(final double a, final double b) {
        double diff = normalizeAngle(a - b);

        if (diff > fractionOfCircle(0.5)) {
            diff -= CIRCLE;
        }

        return diff;
    }
}
