package com.jordanbunke.painterly.core.paint.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;

import java.awt.*;

public final class TextureGenerator {
    private static final int STANDARD_TEXTURE_W = 50,
            STANDARD_TEXTURE_H = 100;
    private static final Color GREY = new Color(0x808080);
    private static final int CH = 0x80;

    public static GameImage flatTexture() {
        final int w = STANDARD_TEXTURE_W, h = STANDARD_TEXTURE_H;
        final GameImage texture = new GameImage(w, h);
        texture.fillRectangle(GREY, w / 2, 0, w / 2, h);
        return texture.submit();
    }

    public static GameImage gapTexture() {
        final int w = STANDARD_TEXTURE_W, h = STANDARD_TEXTURE_H;
        final GameImage texture = new GameImage(w, h);

        final int x = w / 2, sectionW = w / 2;

        int y = 0;
        boolean gap = false;

        while (y < h) {
            final int sectionH = RNG.randomInRange(1, 7);

            if (!gap) {
                final Color c = new Color(CH, CH, CH,
                        RNG.randomInRange(0x80, 0x100));
                texture.fillRectangle(c, x, y, sectionW, sectionH);
            }

            gap = !gap && RNG.prob(0.3);
            y += sectionH;
        }
        return texture.submit();
    }
}
