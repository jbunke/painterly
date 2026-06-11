package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.core.Project;

import java.awt.*;

public final class PaintEngine {
    public static RectBounds draw(final Project p, final GameImage copy) {
        final Coord2D strokePos = strokePosition(p);
        final BrushStroke stroke = stroke(p, strokePos);
        final Color color = color(p, stroke);

        return draw(copy, stroke, color);
    }

    private static RectBounds draw(
            final GameImage canvas, final BrushStroke stroke, final Color color
    ) {
        // TODO - naive implementation

        canvas.drawLine(color, stroke.breadth,
                stroke.position.x, stroke.position.y,
                stroke.position.x + stroke.length, stroke.position.y);

        final int left = Math.max(0, stroke.position.x - (int)(stroke.breadth + 1)),
                right = Math.min(canvas.getWidth(), stroke.position.x +
                        stroke.length + (int)(stroke.breadth + 1)),
                top = Math.max(0, stroke.position.y - (int)(stroke.breadth + 1)),
                bottom = Math.min(canvas.getHeight(), stroke.position.y +
                        (int)(stroke.breadth + 1));

        return new RectBounds(left, right, top, bottom);
    }

    private static Coord2D strokePosition(final Project p) {
        // TODO - naive implementation
        // TODO - get focus bounds from FocusManager

        return new Coord2D(RNG.randomInRange(0, p.width),
                RNG.randomInRange(0, p.height));
    }

    private static BrushStroke stroke(
            final Project p, final Coord2D strokePos
    ) {
        // TODO - naive implementation

        return new BrushStroke(strokePos,
                (float) Math.pow(RNG.randomInRange(1d, 5d),
                        RNG.randomInRange(1d, 2d)),
                (int) Math.pow(RNG.randomInRange(4d, 16d),
                        RNG.randomInRange(1d, 1.5d)));
    }

    private static Color color(
            final Project p, final BrushStroke stroke
    ) {
        // TODO - naive implementation; don't hate it though

        final int sourceX = stroke.position.x / p.scaleFactor,
                sourceY = stroke.position.y / p.scaleFactor;

        final int sampleX = MathPlus.bounded(0,
                smudge(sourceX, stroke.length),
                (p.width / p.scaleFactor) - 1),
                sampleY = MathPlus.bounded(0,
                        smudge(sourceY, stroke.length),
                        (p.height / p.scaleFactor) - 1);

        return p.getSourceImage().getColorAt(sampleX, sampleY);
    }

    public static double similarity(
            final GameImage source, final GameImage painting,
            final RectBounds bounds
    ) {
        final int pixels = (bounds.right() - bounds.left()) *
                (bounds.bottom() - bounds.top());
        double cumSim = 0.;

        for (int x = bounds.left(); x < bounds.right(); x++)
            for (int y = bounds.top(); y < bounds.bottom(); y++)
                cumSim += ColorAlgo.diffRGB(source.getColorAt(x, y),
                        painting.getColorAt(x, y));

        return cumSim / (double) pixels;
    }

    private static double diagonal(final Project p) {
        return Math.sqrt(Math.pow(p.width, 2) + Math.pow(p.height, 2));
    }

    private static int smudge(final int num, final int max) {
        return (num - ((max + 1) / 2)) + RNG.randomInRange(0, max);
    }
}
