package com.jordanbunke.painterly.core.paint;

public class BoundsBuilder {
    private int left, right, top, bottom;

    public BoundsBuilder() {
        left = Integer.MAX_VALUE;
        right = Integer.MIN_VALUE;
        top = Integer.MAX_VALUE;
        bottom = Integer.MIN_VALUE;
    }

    public RectBounds build() {
        return new RectBounds(left, right, top, bottom);
    }

    public void updateLeft(final int leftCandidate) {
        left = Math.min(leftCandidate, left);
    }

    public void updateRight(final int rightCandidate) {
        right = Math.max(rightCandidate, right);
    }

    public void updateTop(final int topCandidate) {
        top = Math.min(topCandidate, top);
    }

    public void updateBottom(final int bottomCandidate) {
        bottom = Math.max(bottomCandidate, bottom);
    }
}
