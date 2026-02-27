package com.jordanbunke.painterly.settings;

import java.awt.*;

public enum Palette {
    ALL(1),
    HIGHEST_SPEC(3),
    HIGH_SPEC(5),
    AVG_HIGH_SPEC(15),
    AVG_LOW_SPEC(17),
    LOWEST_SPEC(51);

    private final int factor;

    Palette(final int factor) {
        this.factor = factor;
    }

    public Color quantize(final Color c) {
        if (this == ALL)
            return c;

        return new Color(
                roundProp(c.getRed()), roundProp(c.getGreen()), roundProp(c.getBlue())
        );
    }

    private int roundProp(final int p) {
        return (int)(Math.round(p / (double)factor) * factor);
    }
}
