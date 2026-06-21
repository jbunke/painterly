package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.painterly.algo.CircleMath;
import com.jordanbunke.painterly.algo.Sobel;
import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.util.Constants;

import java.awt.*;

public final class PaintEngine {
    public static BrushStroke draw(final Project p, final GameImage copy) {
        final Coord2D strokePos = p.focusManager.strokePosition();
        final BrushStroke stroke = stroke(p, strokePos);
        final Color color = color(p, stroke);

        draw(copy, stroke, color);
        return stroke;
    }

    private static void draw(
            final GameImage canvas, final BrushStroke stroke, final Color color
    ) {
        // TODO - naive implementation
        canvas.drawLine(color, stroke.breadth,
                stroke.position.x, stroke.position.y,
                stroke.endPosition.x, stroke.endPosition.y);
    }

    private static BrushStroke stroke(
            final Project p, final Coord2D strokePos
    ) {
        // TODO - naive implementation
        final BrushStroke.Builder strokeBuilder = BrushStroke.init(strokePos);

        // TODO

        strokeAngle(p, strokeBuilder);
        strokeBreadth(p, strokeBuilder);
        strokeLength(p, strokeBuilder);

        return strokeBuilder.build();
    }

    private static void strokeAngle(
            final Project p, final BrushStroke.Builder strokeBuilder
    ) {
        final Coord2D pos = sourcePosition(p, strokeBuilder.position);
        final double intensity = Sobel.edgeIntensity(pos.x, pos.y, p),
                edgeDirection = Sobel.edgeDirection(pos.x, pos.y, p),
                sampleProb = intensity * Constants.MAX_ANGLE_SAMPLE_PROB;

        double angle;

        if (RNG.prob(sampleProb)) {
            // sample angle from edge direction at initial point
            final double variance = RNG.randomInRange(
                    -Constants.MAX_ANGLE_VARIANCE,
                    Constants.MAX_ANGLE_VARIANCE);
            angle = CircleMath.augmentAngle(edgeDirection, variance);

            strokeBuilder.setAlongEdge(true);
        } else {
            // random direction
            angle = RNG.randomInRange(0, CircleMath.CIRCLE);
        }

        strokeBuilder.setAngle(angle);
    }

    /**
     * Stroke length is influenced by similarity, canvas size, art style
     * */
    private static void strokeLength(
            final Project p, final BrushStroke.Builder strokeBuilder
    ) {
        // TODO

        final double diagonal = diagonal(p),
                similarity = p.progressManager.getGlobalSimilarity(),
                simComp = Math.max(1 - Math.pow(similarity, 1.5),
                        Constants.MIN_STROKE_LENGTH_MULTIPLIER),
                sizeComp = diagonal *
                        Constants.MAX_STROKE_LENGTH_SIZE_RATIO,
                rndComp = RNG.randomInRange(0.2, 1d),
                length = simComp * sizeComp * rndComp;

        strokeBuilder.setLength((int) length);
    }

    /**
     * Stroke breadth is influenced by similarity, Sobel, canvas size, art style
     * */
    private static void strokeBreadth(
            final Project p, final BrushStroke.Builder strokeBuilder
    ) {
        final Coord2D pos = sourcePosition(p, strokeBuilder.position);
        final double intensity = Sobel.edgeIntensity(pos.x, pos.y, p),
                diagonal = diagonal(p),
                similarity = p.progressManager.getGlobalSimilarity(),
                simComp = 1 - Math.pow(similarity, 4d),
                sizeComp = Math.pow(diagonal, 0.67),
                rndComp = RNG.randomInRange(0.8, 1d);

        double breadth = MathPlus.bounded(Constants.MIN_STROKE_BREADTH,
                simComp * sizeComp * rndComp, sizeComp);

        if (intensity > Constants.LINE_SOBEL_THRESHOLD &&
                RNG.prob(Constants.LINE_BREADTH_PROB))
            breadth *= Constants.LINE_BREADTH_MULTIPLIER;

        strokeBuilder.setBreadth((float) breadth);
    }

    private static Color color(
            final Project p, final BrushStroke stroke
    ) {
        // TODO - naive implementation; don't hate it though

        final Coord2D sourcePos = sourcePosition(p, stroke.position);

        final int sampleX = MathPlus.bounded(0,
                smudge(sourcePos.x, stroke.length),
                (p.width / p.scaleFactor) - 1),
                sampleY = MathPlus.bounded(0,
                        smudge(sourcePos.y, stroke.length),
                        (p.height / p.scaleFactor) - 1);

        return p.getSourceImage().getColorAt(sampleX, sampleY);
    }

    private static Coord2D sourcePosition(
            final Project p, final Coord2D position
    ) {
        return new Coord2D(position.x / p.scaleFactor,
                position.y / p.scaleFactor);
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
                cumSim += colorSimilarity(source.getColorAt(x, y),
                        painting.getColorAt(x, y));

        return cumSim / (double) pixels;
    }

    private static double colorSimilarity(final Color a, final Color b) {
        return 1d - ColorAlgo.diffRGB(a, b);
    }

    private static double diagonal(final Project p) {
        final RectBounds bounds = p.focusManager.getFocusArea();
        return Math.sqrt(Math.pow(bounds.width(), 2) +
                Math.pow(bounds.height(), 2));
    }

    private static int smudge(final int num, final int max) {
        return (num - ((max + 1) / 2)) + RNG.randomInRange(0, max);
    }
}
