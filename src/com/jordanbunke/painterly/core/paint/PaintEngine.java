package com.jordanbunke.painterly.core.paint;

import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.color_proc.ColorAlgo;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.algo.CircleMath;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.painter.IPainter;
import com.jordanbunke.painterly.core.paint.painter.PainterManager;
import com.jordanbunke.painterly.core.paint.texture.ITexture;

import java.awt.*;
import java.awt.geom.AffineTransform;

public final class PaintEngine {
    public static BrushStroke draw(final Project p, final GameImage copy) {
        final BrushStroke stroke = computeStroke(p);
        final Color color = PainterManager.get().color(p, stroke);

        draw(copy, stroke, color);
        return stroke;
    }

    private static void draw(
            final GameImage canvas, final BrushStroke stroke, final Color color
    ) {
        final IPainter painter = PainterManager.get();

        final ITexture texture = painter.brushTexture(stroke, color);
        final int l = stroke.points.length;

        for (int i = 0; i < l; i++) {
            final StrokePoint point = stroke.points[i];
            final double progress = i / (double) l,
                    bm = painter.breadthMultiplier(progress, stroke),
                    breadth = stroke.breadth * bm,
                    scale = breadth / (double) texture.getHeight();

            final GameImage realTexture =
                    painter.realizeTexture(progress, texture);
            final int tw = realTexture.getWidth(),
                    th = realTexture.getHeight();

            final AffineTransform tx = new AffineTransform();

            tx.translate(point.x, point.y);

            if (texture.rotates())
                tx.rotate(point.angle);

            tx.scale(scale, scale);
            tx.translate(-tw / 2.0, -th / 2.0);

            canvas.draw(realTexture, tx);
        }
    }

    private static BrushStroke computeStroke(final Project p) {
        final IPainter painter = PainterManager.get();

        final Coord2D strokePos = p.focusManager.strokePosition();
        final Pair<Boolean, Double> sobelResult =
                painter.strokeAngle(p, strokePos);
        final double initialAngle = sobelResult.b();
        final boolean angleFromEdge = sobelResult.a();
        final int length = painter.strokeLength(p, angleFromEdge);
        final double breadth = painter.strokeBreadth(
                p, strokePos, length, angleFromEdge);

        return populateStrokePoints(p, strokePos,
                length, breadth, initialAngle, angleFromEdge);
    }

    private static BrushStroke populateStrokePoints(
            final Project p,
            final Coord2D strokePos, final int length,
            final double breadth, final double initialAngle,
            final boolean angleFromEdge
    ) {
        final StrokePoint initial = new StrokePoint(
                strokePos.x, strokePos.y, initialAngle);
        final BrushStroke.Builder strokeBuilder =
                new BrushStroke.Builder(initial, breadth);

        final IPainter painter = PainterManager.get();
        double angle = initial.angle, dxAngle = 0d,
                x = initial.x, y = initial.y;

        for (int i = 0; i < length; i++) {
            x += Math.cos(angle);
            y += Math.sin(angle);

            final double lastAngle = angle;
            angle = painter.nextAngle(p, x, y, angle, dxAngle,
                    initialAngle, angleFromEdge, i, length);
            dxAngle = CircleMath.angleDifference(angle, lastAngle);

            final StrokePoint point = new StrokePoint(x, y, angle);
            strokeBuilder.addPoint(point);
        }

        return strokeBuilder.build(p);
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
}
