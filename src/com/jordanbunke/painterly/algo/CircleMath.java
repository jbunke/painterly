package com.jordanbunke.painterly.algo;

public final class CircleMath {
    public static final double CIRCLE = 2 * Math.PI;

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
}
