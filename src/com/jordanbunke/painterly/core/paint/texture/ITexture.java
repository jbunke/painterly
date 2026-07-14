package com.jordanbunke.painterly.core.paint.texture;

import com.jordanbunke.delta_time.image.GameImage;

public interface ITexture {
    GameImage realize(final double progress);

    boolean rotates();

    int getHeight();
}
