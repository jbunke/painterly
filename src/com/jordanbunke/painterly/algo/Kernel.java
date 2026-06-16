package com.jordanbunke.painterly.algo;

public final class Kernel {
    public final int width, height;
    private final int[] values;

    public Kernel(final int width, final int height, final int... values) {
        this.width = width;
        this.height = height;
        this.values = new int[width * height];

        for (int i = 0; i < this.values.length && i < values.length; i++)
            this.values[i] = values[i];
    }

    public int at(final int x, final int y) {
        if (x < 0 || x >= width)
            throw new IndexOutOfBoundsException();
        if (y < 0 || y >= height)
            throw new IndexOutOfBoundsException();

        return values[(y * width) + x];
    }
}
