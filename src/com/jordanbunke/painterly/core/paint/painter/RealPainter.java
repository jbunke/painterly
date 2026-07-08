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

import java.awt.*;
import java.util.Arrays;

import static com.jordanbunke.painterly.algo.Recoloring.tintGreyscaleTexture;

public final class RealPainter implements IPainter {
    private static final RealPainter INSTANCE;

    private static final double MAX_ANGLE_DEVIATION =
            CircleMath.fractionOfCircle(1 / 32.),
            AVERAGE_DIM_STROKE_LENGTH_MULTIPLIER = 0.35,
            MIN_LENGTH = 4.0,
            MIN_BREADTH = 4.0, MAX_BREADTH = 100.0,
            SAMPLED_FROM_EDGE_BREADTH_MULTIPLIER = 0.3,
            THIN_LINE_PROB = 0.3,
            THIN_LINE_EDGE_INTENSITY_THRESHOLD = 0.5,
            FIRST_PIXEL_SAMPLE_PROB = 0.3,
            LONG_LTB_RATIO = 8.0,
            SHORT_LTB_RATIO = 4.0,
            LONG_LTB_LENGTH_THRESHOLD = 40.0,
            SHORT_LTB_LENGTH_THRESHOLD = 20.0;

    // TODO - temp
    private final GameImage example;

    static {
        INSTANCE = new RealPainter();
    }

    private RealPainter() {
        // TODO
        example = TextureGenerator.flatTexture();
    }

    public static RealPainter get() {
        return INSTANCE;
    }

    @Override
    public ITexture brushTexture(final BrushStroke stroke, final Color tintColor) {
        // TODO
        final GameImage image =
                tintGreyscaleTexture(example, tintColor, 128);
        return new SimpleTexture(image, true);
    }

    @Override
    public double breadthMultiplier(final double progress, final BrushStroke stroke) {
        // TODO
        return 1;
    }

    @Override
    public GameImage realizeTexture(final double progress, final ITexture texture) {
        // TODO
        return texture.realize(progress);
    }

    @Override
    public Pair<Boolean, Double> strokeAngle(final Project p, final Coord2D strokePos) {
        final Coord2D sourcePos = strokePos.scale(1 / p.scaleFactor);

        final double intensity = Sobel.edgeIntensity(sourcePos, p),
                edgeDirection = Sobel.edgeDirection(sourcePos, p);

        // TODO - use probability multiplier constant
        //  or separate probability conjunction
        if (RNG.prob(intensity)) {
            final double deviation = RNG.deviate(MAX_ANGLE_DEVIATION),
                    angle = CircleMath.augmentAngle(edgeDirection, deviation);

            return new Pair<>(true, angle);
        }

        return new Pair<>(false, CircleMath.randomAngle());
    }

    @Override
    public int strokeLength(final Project p, final boolean angleFromEdge) {
        final int area = p.focusManager.getFocusArea().area();
        final double dimAverage = Math.sqrt(area),
                similarity = p.progressManager.getFocusSimilarity(),
                simMultiplier = Math.pow(1.0 - similarity, 0.5),
                randomMultiplier = RNG.factor(1.5);
        double length = dimAverage * AVERAGE_DIM_STROKE_LENGTH_MULTIPLIER *
                simMultiplier * randomMultiplier;

        length = Math.max(length, MIN_LENGTH);

        return (int) Math.round(length);
    }

    @Override
    public double strokeBreadth(
            final Project p, final Coord2D strokePos,
            final int length, final boolean angleFromEdge
    ) {
        final double naiveBreadth = length / idealLTBRatio(length),
                randomMultiplier = RNG.factor(2.0);

        double breadth = naiveBreadth * randomMultiplier;

        if (angleFromEdge && RNG.prob(THIN_LINE_PROB)) {
            final Coord2D sourcePos = strokePos.scale(1 / p.scaleFactor);
            final double intensity = Sobel.edgeIntensity(sourcePos, p);

            if (intensity > THIN_LINE_EDGE_INTENSITY_THRESHOLD)
                breadth *= SAMPLED_FROM_EDGE_BREADTH_MULTIPLIER;
        }

        breadth = MathPlus.bounded(MIN_BREADTH, breadth, MAX_BREADTH);

        return breadth;
    }

    @Override
    public Color color(final Project p, final BrushStroke stroke) {
        final GameImage sourceImage = p.getSourceImage();

        if (RNG.prob(FIRST_PIXEL_SAMPLE_PROB)) {
            final Coord2D sourcePos = stroke.from()
                    .scale(1 / p.scaleFactor);
            return sourceImage.getColorAt(sourcePos.x, sourcePos.y);
        }

        final int w = sourceImage.getWidth(), h = sourceImage.getHeight();
        final Coord2D[] sourcePosArray = Arrays.stream(stroke.points)
                .map(pt -> pt.coord.scale(1 / p.scaleFactor))
                .filter(c -> c.x >= 0 && c.y >= 0 && c.x < w && c.y < h)
                .toArray(Coord2D[]::new);
        final int i = RNG.randomInRange(0, sourcePosArray.length);

        return sourceImage.getColorAt(
                sourcePosArray[i].x, sourcePosArray[i].y);
    }

    @Override
    public double nextAngle(
            final Project p, final double x, final double y,
            final double lastAngle, final double initialAngle,
            final int progress, final int length
    ) {
        // TODO
        return lastAngle;

//        final Coord2D pos = new Coord2D(
//                (int) Math.round(x), (int) Math.round(y));
//
//        if (pos.x < 0 || pos.y < 0 || pos.x >= p.width || pos.y >= p.height)
//            return CircleMath.randomAngle();
//
//        return strokeAngle(p, pos).b();
    }

    private static double idealLTBRatio(final int length) {
        final double tRange = LONG_LTB_LENGTH_THRESHOLD - SHORT_LTB_LENGTH_THRESHOLD,
                t = (length - SHORT_LTB_LENGTH_THRESHOLD) / tRange,
                vRange = LONG_LTB_RATIO - SHORT_LTB_RATIO,
                v = SHORT_LTB_RATIO + (t * vRange);

        return MathPlus.bounded(SHORT_LTB_RATIO, v, LONG_LTB_RATIO);
    }
}
