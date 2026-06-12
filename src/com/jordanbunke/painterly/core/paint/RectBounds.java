package com.jordanbunke.painterly.core.paint;

public record RectBounds(int left, int right, int top, int bottom) {

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
