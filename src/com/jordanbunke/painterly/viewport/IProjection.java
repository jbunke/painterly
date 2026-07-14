package com.jordanbunke.painterly.viewport;

@FunctionalInterface
public interface IProjection {
    void project(final int x, final int y, final int width, final int height);
}
