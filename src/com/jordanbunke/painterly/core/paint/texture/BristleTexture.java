package com.jordanbunke.painterly.core.paint.texture;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;
import com.jordanbunke.painterly.algo.CircleMath;
import com.jordanbunke.painterly.core.paint.painter.TextureGenerator;

import java.util.stream.IntStream;

public final class BristleTexture implements ITexture {
    private final Bristle[] bristles;
    private final GameImage texture;

    public BristleTexture(final int numBristles) {
        bristles = IntStream.range(0, numBristles)
                .mapToObj(i -> new Bristle())
                .toArray(Bristle[]::new);

        texture = TextureGenerator.bristleTexture(bristles);
    }

    @Override
    public GameImage realize(final double pressure) {
        // TODO
        return texture;
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
