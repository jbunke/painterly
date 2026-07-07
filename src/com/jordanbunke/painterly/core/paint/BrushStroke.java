package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.LinkedList;
import java.util.List;

public final class BrushStroke {
    // public final Coord2D position, endPosition;
    public final StrokePoint[] points;
    public final float breadth;

    // metadata
    // public final boolean alongEdge;
    private boolean accepted;

    private BrushStroke(
            final StrokePoint[] points, final float breadth
    ) {
        this.points = points;
        this.breadth = breadth;
    }

    public Coord2D from() {
        return points[0].coord;
    }

    public int length() {
        return points.length;
    }

    public RectBounds affectedArea(
            final int canvasWidth, final int canvasHeight
    ) {
        final RectBounds.Builder rbb = new RectBounds.Builder();

        // TODO - based on rendering, r should be breadth / 2
        final int r = (int) Math.ceil(breadth);

        for (StrokePoint point : points) {
            rbb.updateLeft(point.roundedX - r)
                    .updateRight(point.roundedX + r)
                    .updateTop(point.roundedY - r)
                    .updateBottom(point.roundedY + r);
        }

        return rbb.constrainLeft(0).constrainTop(0)
                .constrainRight(canvasWidth).constrainBottom(canvasHeight)
                .build();
    }

    public void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }

    public boolean wasAccepted() {
        return accepted;
    }

    public static class Builder {
        public final List<StrokePoint> points;

        private float breadth;

        public Builder(final StrokePoint initial) {
            this.points = new LinkedList<>();
            points.add(initial);

            breadth = 2f;
        }

        public Builder addPoint(final StrokePoint point) {
            points.add(point);
            return this;
        }

        public Builder setBreadth(final float breadth) {
            this.breadth = breadth;
            return this;
        }

        public BrushStroke build() {
            return new BrushStroke(points.toArray(StrokePoint[]::new), breadth);
        }
    }
}
