package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class StrokePoint {
    public final double x, y, angle;
    public final int roundedX, roundedY;
    public final Coord2D coord;

    public StrokePoint(final double x, final double y, final double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        roundedX = (int) Math.round(x);
        roundedY = (int) Math.round(y);
        coord = new Coord2D(roundedX, roundedY);
    }
}
