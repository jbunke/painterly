package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;

import java.util.LinkedList;
import java.util.List;

public final class BrushStroke {
    public final StrokePoint[] points;
    public final double breadth;
    public final RectBounds affectedArea;

    // metadata
    private boolean accepted;

    private BrushStroke(
            final StrokePoint[] points, final double breadth,
            final RectBounds affectedArea
    ) {
        this.points = points;
        this.breadth = breadth;
        this.affectedArea = affectedArea;
    }

    public Coord2D from() {
        return points[0].coord;
    }

    public int length() {
        return points.length;
    }

    public void setAccepted(final boolean accepted) {
        this.accepted = accepted;
    }

    public boolean wasAccepted() {
        return accepted;
    }

    public static class Builder {
        private final List<StrokePoint> points;
        private final double breadth;
        private final RectBounds.Builder rbb;

        public Builder(final StrokePoint initial, final double breadth) {
            this.points = new LinkedList<>();
            points.add(initial);

            this.breadth = breadth;

            rbb = new RectBounds.Builder();
        }

        private void updateBounds(final StrokePoint point) {
            final int r = (int) Math.ceil(breadth);

            rbb.updateLeft(point.roundedX - r)
                    .updateRight(point.roundedX + r)
                    .updateTop(point.roundedY - r)
                    .updateBottom(point.roundedY + r);
        }

        public Builder addPoint(final StrokePoint point) {
            points.add(point);
            updateBounds(point);
            return this;
        }

        public BrushStroke build(final Project p) {
            final RectBounds affectedArea = rbb
                    .constrainLeft(0).constrainTop(0)
                    .constrainRight(p.width).constrainBottom(p.height)
                    .build();

            return new BrushStroke(points.toArray(StrokePoint[]::new),
                    breadth, affectedArea);
        }
    }
}
