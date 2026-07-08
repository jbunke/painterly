package com.jordanbunke.painterly.core.paint.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.algo.CircleMath;
import com.jordanbunke.painterly.algo.Sobel;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.BrushStroke;
import com.jordanbunke.painterly.core.paint.texture.ITexture;
import com.jordanbunke.painterly.core.paint.texture.SimpleTexture;
import com.jordanbunke.painterly.util.Constants;

import java.awt.*;

import static com.jordanbunke.painterly.algo.Recoloring.*;

public final class DefaultPainter extends PolyTexturePainter {
    private static final DefaultPainter INSTANCE;

    private static final int NUM_TEXTURES = 40;

    private static final double MAX_LIFETIME_DELTA =
            CircleMath.fractionOfCircle(0.2);

    private double maxD2xAngle, maxDeltaAngle, dxAngle;

    static {
        INSTANCE = new DefaultPainter();
    }

    private DefaultPainter() {
        super(NUM_TEXTURES);
    }

    public static DefaultPainter get() {
        return INSTANCE;
    }

    @Override
    public ITexture brushTexture(
            final BrushStroke stroke, final Color tintColor
    ) {
        final GameImage image =
                tintGreyscaleTexture(getRandomTexture(), tintColor, 128);
        return new SimpleTexture(image, true);
    }

    @Override
    public double breadthMultiplier(
            final double progress, final BrushStroke stroke
    ) {
        // TODO - temp
        // return 1.0 - (progress / 4.0);
        return 1.0;
    }

    @Override
    public GameImage realizeTexture(
            final double progress, final ITexture texture
    ) {
        // TODO - temp
        final double remaining = Math.pow(1.0 - progress, 0.5);
        return pixelWiseTransformation(texture.realize(progress), c -> {
            final int r = c.getRed(), g = c.getGreen(), b = c.getBlue(),
                    a = (int) Math.round(remaining * c.getAlpha());
            return new Color(r, g, b, a);
        });
    }

    @Override
    public Pair<Boolean, Double> strokeAngle(
            final Project p, final Coord2D strokePos
    ) {
        final Coord2D pos = strokePos.scale(1 / p.scaleFactor);
        final double intensity = Sobel.edgeIntensity(pos, p),
                edgeDirection = Sobel.edgeDirection(pos, p),
                sampleProb = intensity * Constants.MAX_ANGLE_SAMPLE_PROB;

        if (RNG.prob(sampleProb)) {
            // sample angle from edge direction at initial point
            final double variance = RNG.randomInRange(
                    -Constants.MAX_ANGLE_VARIANCE,
                    Constants.MAX_ANGLE_VARIANCE);

            // TODO - strokeBuilder.setAlongEdge(true);
            return new Pair<>(true,
                    CircleMath.augmentAngle(edgeDirection, variance));
        } else {
            // random direction
            return new Pair<>(false, CircleMath.randomAngle());
        }
    }

    @Override
    public int strokeLength(final Project p, final boolean angleFromEdge) {
        // TODO - revise

        final double diagonal = p.focusManager.getFocusArea().diagonal(),
                similarity = p.progressManager.getGlobalSimilarity(),
                simComp = Math.max(1 - Math.pow(similarity, 1.5),
                        Constants.MIN_STROKE_LENGTH_MULTIPLIER),
                sizeComp = diagonal *
                        Constants.MAX_STROKE_LENGTH_SIZE_RATIO,
                rndComp = RNG.randomInRange(0.2, 1d),
                length = simComp * sizeComp * rndComp;

        return (int) Math.round(length);
    }

    @Override
    public double strokeBreadth(
            final Project p, final Coord2D strokePos,
            final int length, final boolean angleFromEdge
    ) {
        // TODO - revise

        final Coord2D pos = strokePos.scale(1 / p.scaleFactor);
        final double intensity = Sobel.edgeIntensity(pos, p),
                diagonal = p.focusManager.getFocusArea().diagonal(),
                similarity = p.progressManager.getGlobalSimilarity(),
                simComp = 1 - Math.pow(similarity, 4d),
                sizeComp = Math.pow(diagonal, 0.67),
                rndComp = RNG.randomInRange(0.8, 1d);

        double breadth = MathPlus.bounded(Constants.MIN_STROKE_BREADTH,
                simComp * sizeComp * rndComp, sizeComp);

        if (intensity > Constants.LINE_SOBEL_THRESHOLD &&
                RNG.prob(Constants.LINE_BREADTH_PROB))
            breadth *= Constants.LINE_BREADTH_MULTIPLIER;

        return breadth;
    }

    @Override
    public Color color(final Project p, final BrushStroke stroke) {
        // TODO - naive implementation; don't hate it though

        final Coord2D sourcePos = stroke.from()
                .scale(1 / p.scaleFactor);

        final int sampleX = MathPlus.bounded(0,
                smudge(sourcePos.x, stroke.length()),
                (int)(p.width / p.scaleFactor) - 1),
                sampleY = MathPlus.bounded(0,
                        smudge(sourcePos.y, stroke.length()),
                        (int)(p.height / p.scaleFactor) - 1);

        return p.getSourceImage().getColorAt(sampleX, sampleY);
    }

    @Override
    public double nextAngle(
            final Project p, final double x, final double y,
            final double lastAngle, final double initialAngle,
            final int progress, final int length
    ) {
        // TODO - temp implementation

        if (progress == 0)
            updateAngleVariables(length);

        dxAngle += RNG.randomInRange(-maxD2xAngle, maxD2xAngle);

        final double presumptive = CircleMath.augmentAngle(lastAngle, dxAngle);
        final Coord2D pos = new Coord2D(
                (int) Math.round(x), (int) Math.round(y));

        if (pos.x < 0 || pos.y < 0 || pos.x >= p.width || pos.y >= p.height)
            return presumptive;

        final Pair<Boolean, Double> sobelResult =
                PainterManager.get().strokeAngle(p, pos);
        final double diffMagnitude = Math.abs(
                CircleMath.angleDifference(sobelResult.b(), lastAngle));

        if (sobelResult.a() && diffMagnitude < maxDeltaAngle)
            return sobelResult.b();

        return presumptive;
    }

    private void updateAngleVariables(final int length) {
        final double lifetimeDelta = RNG.randomInRange(
                -MAX_LIFETIME_DELTA, MAX_LIFETIME_DELTA);
        maxD2xAngle = lifetimeDelta / Math.pow(length, 2);
        maxDeltaAngle = Math.abs(lifetimeDelta / (length / (double) 3));
        dxAngle = lifetimeDelta / (double) length;
    }

    // TODO - move
    private static int smudge(final int num, final int max) {
        return (num - ((max + 1) / 2)) + RNG.randomInRange(0, max);
    }

    @Override
    GameImage generateTexture() {
        // TODO - temp
        return RNG.prob(0.8,
                TextureGenerator.flatTexture(),
                TextureGenerator.gapTexture());
    }
}
