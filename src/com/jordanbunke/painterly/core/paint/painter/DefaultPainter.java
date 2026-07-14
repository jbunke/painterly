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
import com.jordanbunke.painterly.core.paint.texture.BristleTexture;
import com.jordanbunke.painterly.core.paint.texture.ITexture;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Function;

public final class DefaultPainter implements IPainter {
    private static final DefaultPainter INSTANCE;

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
            SHORT_LTB_LENGTH_THRESHOLD = 20.0,
            SHORT_MCDA_LENGTH_THRESHOLD = 25.0,
            LONG_MCDA_LENGTH_THRESHOLD = 200.0,
            SHORT_MCDA_DELTA_ANGLE = CircleMath.fractionOfCircle(1 / 32.),
            LONG_MCDA_DELTA_ANGLE = CircleMath.fractionOfCircle(0.25),
            LINE_CONTINUATION_THRESHOLD = 0.5,
            REVERSE_DELTA_ANGLE_PROB = 0.01,
            BREADTH_TAPER_OFF_PROB = 0.2,
            BREADTH_TAPER_OFF_MAX_THRESHOLD = MIN_BREADTH * 4,
            LATEST_TAPER_ONSET = 0.5,
            MAX_TAPER_FINAL_RATIO = 0.8,
            MIN_PRESSURE = 0.5, MAX_PRESSURE = 1.0,
            INCREASE_PRESSURE_PROB = 0.5,
            LARGE_BRUSH_BREADTH_THRESHOLD = MAX_BREADTH / 4.0,
            SMALL_BRUSH_BREADTH_THRESHOLD = MIN_BREADTH * 3.0;

    private static final int
            SMALL_BRUSH_BRISTLES = 5000,
            MEDIUM_BRUSH_BRISTLES = 2500,
            LARGE_BRUSH_BRISTLES = 1000;

    private final BristleTexture smallBrush, mediumBrush, largeBrush;

    private boolean taperOff;
    private double taperOnset, taperFinalRatio;
    private Function<Double, Double> pressureFunction;

    static {
        INSTANCE = new DefaultPainter();
    }

    private DefaultPainter() {
        smallBrush = new BristleTexture(SMALL_BRUSH_BRISTLES);
        mediumBrush = new BristleTexture(MEDIUM_BRUSH_BRISTLES);
        largeBrush = new BristleTexture(LARGE_BRUSH_BRISTLES);

        pressureFunction = p -> p;
    }

    public static DefaultPainter get() {
        return INSTANCE;
    }

    @Override
    public ITexture brushTexture(final BrushStroke stroke, final Color tintColor) {
        // variable updates for new stroke

        // breadth tapering
        updateBreadthTapering(stroke);

        // pressure
        updatePressureFunction(stroke);

        // choose texture
        final BristleTexture brush = chooseBrush(stroke);
        brush.tint(tintColor);
        return brush;
    }

    @Override
    public double breadthMultiplier(final double progress, final BrushStroke stroke) {
        if (taperOff)
            return MathPlus.lerp(progress, taperOnset, 1.0,
                    1.0, taperFinalRatio, true);

        return 1.0;
    }

    @Override
    public GameImage realizeTexture(final double progress, final ITexture texture) {
        final double pressure = pressureFunction.apply(progress);
        return texture.realize(pressure);
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
    public double nextAngle(
            final Project p, final double x, final double y,
            final double lastAngle, final double lastDeltaAngle,
            final double initialAngle, final boolean angleFromEdge,
            final int progress, final int length
    ) {
        // preprocessing
        final int remaining = length - progress;
        final double maxCumDeltaAngle = maxCumDeltaAngle(length),
                drift = CircleMath.angleDifference(lastAngle, initialAngle),
                allowance = maxCumDeltaAngle - Math.abs(drift),
                perPoint = allowance / (double) remaining;

        // determine whether following Sobel edge is viable
        final Coord2D strokePos = new Coord2D(
                (int) Math.round(x), (int) Math.round(y));

        if (angleFromEdge && strokePos.x >= 0 && strokePos.y >= 0 &&
                strokePos.x < p.width && strokePos.y < p.height) {
            final Coord2D sourcePos = strokePos.scale(1 / p.scaleFactor);

            final double intensity = Sobel.edgeIntensity(sourcePos, p),
                    edgeDirection = Sobel.edgeDirection(sourcePos, p),
                    deviation = RNG.deviate(MAX_ANGLE_DEVIATION),
                    candidate = CircleMath.augmentAngle(edgeDirection, deviation),
                    diffMagnitude = Math.abs(
                            CircleMath.angleDifference(candidate, lastAngle));

            if (intensity >= LINE_CONTINUATION_THRESHOLD &&
                    diffMagnitude < perPoint)
                return candidate;
        }

        // standard path propagation
        double deltaAngle;

        if (progress == 0) {
            // start of stroke
            final double randomMultiplier = RNG.factor(1.5),
                    maxDeltaAngle = perPoint * randomMultiplier;

            deltaAngle = RNG.deviate(maxDeltaAngle);
        } else {
            // continues from previous point
            final double randomMultiplier = RNG.factor(1.1);

            deltaAngle = lastDeltaAngle * randomMultiplier;

            if (RNG.prob(REVERSE_DELTA_ANGLE_PROB))
                deltaAngle *= -1.0;
        }

        return CircleMath.augmentAngle(lastAngle, deltaAngle);
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

    // VARIABLE UPDATERS

    private void updateBreadthTapering(final BrushStroke stroke) {
        taperOff = stroke.breadth <= BREADTH_TAPER_OFF_MAX_THRESHOLD &&
                RNG.prob(BREADTH_TAPER_OFF_PROB);
        if (taperOff) {
            taperOnset = RNG.randomInRange(0.0, LATEST_TAPER_ONSET);
            taperFinalRatio = RNG.randomInRange(0.0, MAX_TAPER_FINAL_RATIO);
        }
    }

    private void updatePressureFunction(final BrushStroke stroke) {
        final int length = stroke.length();

        if (taperOff)
            pressureFunction = this::decreasePressure;
        else if (length >= LONG_LTB_LENGTH_THRESHOLD &&
                RNG.prob(INCREASE_PRESSURE_PROB))
            pressureFunction = this::increasePressure;
        else {
            final double pressure =
                    RNG.randomInRange(MIN_PRESSURE, MAX_PRESSURE);
            pressureFunction = p -> pressure;
        }
    }

    private BristleTexture chooseBrush(final BrushStroke stroke) {
        final double breadth = stroke.breadth;

        if (breadth >= LARGE_BRUSH_BREADTH_THRESHOLD)
            return largeBrush;
        else if (breadth <= SMALL_BRUSH_BREADTH_THRESHOLD)
            return smallBrush;

        return mediumBrush;
    }

    // PRESSURE FUNCTIONS

    private double increasePressure(final double progress) {
        return MathPlus.lerp(progress, 0, 1,
                MIN_PRESSURE, MAX_PRESSURE, true);
    }

    private double decreasePressure(final double progress) {
        return MathPlus.lerp(progress, 0, 1,
                MAX_PRESSURE, MIN_PRESSURE, true);
    }

    // HELPER

    private static double idealLTBRatio(final int length) {
        return MathPlus.lerp(length,
                SHORT_LTB_LENGTH_THRESHOLD, LONG_LTB_LENGTH_THRESHOLD,
                SHORT_LTB_RATIO, LONG_LTB_RATIO, true);
    }

    private static double maxCumDeltaAngle(final int length) {
        return MathPlus.lerp(length,
                SHORT_MCDA_LENGTH_THRESHOLD, LONG_MCDA_LENGTH_THRESHOLD,
                SHORT_MCDA_DELTA_ANGLE, LONG_MCDA_DELTA_ANGLE, true);
    }
}
