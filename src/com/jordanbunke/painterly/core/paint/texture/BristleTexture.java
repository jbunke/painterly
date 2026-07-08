package com.jordanbunke.painterly.core.paint.texture;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.algo.CircleMath;
import com.jordanbunke.painterly.core.paint.painter.TextureGenerator;

import java.awt.*;
import java.util.stream.IntStream;

import static com.jordanbunke.painterly.algo.Recoloring.*;

public final class BristleTexture implements ITexture {
    private final Bristle[] bristles;
    private final GameImage texture;

    private GameImage tinted;

    public BristleTexture(final int numBristles) {
        bristles = IntStream.range(0, numBristles)
                .mapToObj(i -> new Bristle())
                .toArray(Bristle[]::new);

        texture = TextureGenerator.bristleTexture(bristles);
    }

    public void tint(final Color tint) {
        tinted = tintGreyscaleTexture(texture, tint, TextureGenerator.CH);
    }

    @Override
    public GameImage realize(final double pressure) {
        return pixelWiseTransformation(tinted,
                c -> subtractOpacity(c, 1 - pressure));
    }

    @Override
    public boolean rotates() {
        return true;
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }

    public static class Bristle {
        public static final double FULL_PRESSURE_THRESHOLD = 0.5,
                OFF_THRESHOLD = 0.1;

        public final double x, y, length;

        Bristle() {
            final double angle = CircleMath.randomAngle(),
                    r = RNG.randomInRange(0.0, 1.0);

            x = normalizeDim(Math.cos(angle) * r);
            y = normalizeDim(Math.sin(angle) * r);
            length = RNG.randomInRange(0.0, 1.0);
        }

        private static double normalizeDim(final double dim) {
            return (dim + 1.0) / 2.0;
        }
    }
}
