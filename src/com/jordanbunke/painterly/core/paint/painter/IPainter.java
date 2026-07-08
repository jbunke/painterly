package com.jordanbunke.painterly.core.paint.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.BrushStroke;
import com.jordanbunke.painterly.core.paint.texture.ITexture;

import java.awt.*;

public interface IPainter {

    ITexture brushTexture(final BrushStroke stroke, final Color tintColor);

    double breadthMultiplier(final double progress, final BrushStroke stroke);

    GameImage realizeTexture(final double progress, final ITexture texture);

    /**
     * TODO - properly explain return type; boolean marks for whether angle is
     *        sampled from an edge via the Sobel operator image
     * */
    Pair<Boolean, Double> strokeAngle(final Project p, final Coord2D strokePos);

    int strokeLength(final Project p, final boolean angleFromEdge);

    double strokeBreadth(
            final Project p, final Coord2D strokePos,
            final int length, final boolean angleFromEdge
    );

    Color color(final Project p, final BrushStroke stroke);

    double nextAngle(
            final Project p, final double x, final double y,
            final double lastAngle, final double initialAngle,
            final int progress, final int length
    );
}
