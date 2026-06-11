package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;

// TODO - all temp
public final class BrushStroke {
    public final Coord2D position;
    public final float breadth;
    public final int length;

    // TODO

    public BrushStroke(
            final Coord2D position, final float breadth, final int length
    ) {
        this.position = position;
        this.breadth = breadth;
        this.length = length;
    }
}
