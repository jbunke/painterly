package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;

// TODO - all temp
public final class BrushStroke {
    public final Coord2D position, endPosition;
    public final double angle;
    public final float breadth;
    public final int length;

    // metadata
    public final boolean alongEdge;
    private boolean accepted;

    // TODO

    private BrushStroke(
            final Coord2D position,
            final double angle, final boolean alongEdge,
            final float breadth, final int length
    ) {
        this.position = position;
        this.angle = angle;
        this.alongEdge = alongEdge;
        this.breadth = breadth;
        this.length = length;

        endPosition = position.displace(
                (int) (Math.cos(angle) * length),
                (int) (Math.sin(angle) * length));
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    /** TODO */
    public RectBounds affectedArea(
            final int canvasWidth, final int canvasHeight
    ) {
        final int r = (int) Math.ceil(breadth),
                x1 = position.x, x2 = endPosition.x,
                y1 = position.y, y2 = endPosition.y;

        return new RectBounds.Builder()
                .updateLeft(Math.min(x1, x2) - r)
                .updateRight(Math.max(x1, x2) + r)
                .updateTop(Math.min(y1, y2) - r)
                .updateBottom(Math.max(y1, y2) + r)
                .constrainLeft(0).constrainTop(0)
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
        public final Coord2D position;

        private double angle;
        private boolean alongEdge;
        private float breadth;
        private int length;

        public Builder(final Coord2D position) {
            this.position = position;

            // TODO
            angle = 0d;
            alongEdge = false;
            breadth = 2f;
            length = 10;
        }

        // TODO - temp setters

        public Builder setAngle(final double angle) {
            this.angle = angle;
            return this;
        }

        public Builder setAlongEdge(final boolean alongEdge) {
            this.alongEdge = alongEdge;
            return this;
        }

        public Builder setBreadth(final float breadth) {
            this.breadth = breadth;
            return this;
        }

        public Builder setLength(final int length) {
            this.length = length;
            return this;
        }

        public BrushStroke build() {
            // TODO
            return new BrushStroke(position,
                    angle, alongEdge, breadth, length);
        }
    }
}
