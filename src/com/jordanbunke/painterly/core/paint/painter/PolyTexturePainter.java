package com.jordanbunke.painterly.core.paint.painter;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.RNG;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public abstract class PolyTexturePainter implements IPainter {
    private final List<GameImage> textureList;
    private final int size;

    PolyTexturePainter(final int size) {
        textureList = new ArrayList<>(size);
        this.size = size;

        while (textureList.size() < size)
            textureList.add(generateTexture());
    }

    abstract GameImage generateTexture();

    GameImage getRandomTexture() {
        return textureList.get(RNG.randomInRange(0, size));
    }
}
