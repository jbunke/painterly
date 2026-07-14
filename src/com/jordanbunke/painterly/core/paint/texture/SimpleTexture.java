package com.jordanbunke.painterly.core.paint.texture;

import com.jordanbunke.delta_time.image.GameImage;

public final class SimpleTexture implements ITexture {
    private final GameImage texture;
    private final boolean rotates;

    public SimpleTexture(final GameImage texture, final boolean rotates) {
        this.texture = texture;
        this.rotates = rotates;
    }

    @Override
    public GameImage realize(final double progress) {
        return texture;
    }

    @Override
    public boolean rotates() {
        return rotates;
    }

    @Override
    public int getHeight() {
        return texture.getHeight();
    }
}
