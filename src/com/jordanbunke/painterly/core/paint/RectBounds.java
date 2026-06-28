package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Coord2D;

public record RectBounds(int left, int right, int top, int bottom) {

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public Coord2D topLeft() {
        return new Coord2D(left, top);
    }

    public Coord2D topRight() {
        return new Coord2D(right, top);
    }

    public Coord2D bottomLeft() {
        return new Coord2D(left, bottom);
    }

    public Coord2D bottomRight() {
        return new Coord2D(right, bottom);
    }

    public RectBounds displace(final int deltaX, final int deltaY) {
        final int left = left() + deltaX, right = right() + deltaX,
                top = top() + deltaY, bottom = bottom() + deltaY;

        return new RectBounds(left, right, top, bottom);
    }

    public boolean hasArea() {
        return left < right && top < bottom;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof RectBounds that)
            return left == that.left && right == that.right &&
                    top == that.top && bottom == that.bottom;

        return false;
    }

    @Override
    public int hashCode() {
        return left + right + top + bottom;
    }

    public static class Builder {
        private int left, right, top, bottom;

        public Builder() {
            left = Integer.MAX_VALUE;
            right = Integer.MIN_VALUE;
            top = Integer.MAX_VALUE;
            bottom = Integer.MIN_VALUE;
        }

        public RectBounds build() {
            return new RectBounds(left, right, top, bottom);
        }

        public Builder from(final RectBounds ref) {
            left = ref.left;
            right = ref.right;
            top = ref.top;
            bottom = ref.bottom;
            return this;
        }

        public Builder setBottom(final int bottom) {
            this.bottom = bottom;
            return this;
        }

        public Builder setLeft(final int left) {
            this.left = left;
            return this;
        }

        public Builder setRight(final int right) {
            this.right = right;
            return this;
        }

        public Builder setTop(final int top) {
            this.top = top;
            return this;
        }

        public Builder updateLeft(final int leftCandidate) {
            left = Math.min(leftCandidate, left);
            return this;
        }

        public Builder updateRight(final int rightCandidate) {
            right = Math.max(rightCandidate, right);
            return this;
        }

        public Builder updateTop(final int topCandidate) {
            top = Math.min(topCandidate, top);
            return this;
        }

        public Builder updateBottom(final int bottomCandidate) {
            bottom = Math.max(bottomCandidate, bottom);
            return this;
        }

        public Builder constrainLeft(final int leftLimit) {
            left = Math.max(left, leftLimit);
            return this;
        }

        public Builder constrainRight(final int rightLimit) {
            right = Math.min(right, rightLimit);
            return this;
        }

        public Builder constrainTop(final int topLimit) {
            top = Math.max(top, topLimit);
            return this;
        }

        public Builder constrainBottom(final int bottomLimit) {
            bottom = Math.min(bottom, bottomLimit);
            return this;
        }
    }
}
