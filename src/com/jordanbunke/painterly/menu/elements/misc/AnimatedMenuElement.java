package com.jordanbunke.painterly.menu.elements.misc;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.image.ImageProcessing;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.Layout;

import java.util.Arrays;

public final class AnimatedMenuElement extends MenuElement {
    final GameImage[] frames;
    final boolean repeats;
    final int ticksPerFrame;

    int tickCount, index;

    private AnimatedMenuElement(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final GameImage[] frames,
            final boolean repeats, final int ticksPerFrame
    ) {
        super(position, dimensions, anchor, true);

        this.frames = frames;
        this.repeats = repeats;
        this.ticksPerFrame = ticksPerFrame;

        index = 0;
        tickCount = 0;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {}

    @Override
    public void update(final double deltaTime) {
        tickCount++;

        if (tickCount >= ticksPerFrame) {
            index++;
            tickCount = 0;

            if (index >= frames.length)
                index = repeats ? 0 : frames.length - 1;
        }
    }

    @Override
    public void render(final GameImage canvas) {
        draw(frames[index], canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static class Builder implements MenuElementBuilder<AnimatedMenuElement> {
        private final Coord2D position;
        private final GameImage[] frames;

        private final int fw, fh;

        private Bounds2D dimensions;
        private Anchor anchor;

        private boolean repeats;
        private int ticksPerFrame;

        public Builder(final Coord2D position, final GameImage... frames) {
            this.position = position;
            this.frames = frames;

            if (frames != null && frames.length >= 1) {
                fw = frames[0].getWidth();
                fh = frames[0].getHeight();
            } else {
                fw = 1;
                fh = 1;
            }

            dimensions = new Bounds2D(fw, fh);
            anchor = Anchor.CENTRAL;
            repeats = false;
            ticksPerFrame = 1;
        }

        public Builder fillScreenBox(
                final Layout.ScreenBox sb, final boolean ignoreIfSmaller
        ) {
            final int sbw = sb.width.get(), sbh = sb.height.get();
            final double scaleW = sbw / (double) fw, scaleH = sbh / (double) fh,
                    scaleFactor = Math.max(scaleW, scaleH);

            if (ignoreIfSmaller && scaleFactor <= 1.0)
                return this;

            final int w = (int) Math.ceil(scaleFactor * fw),
                    h = (int) Math.ceil(scaleFactor * fh);
            return setDimensions(new Bounds2D(w, h));
        }

        public Builder setDimensions(final Bounds2D dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public Builder setWidth(final int width) {
            final double scaleFactor = width / (double) fw;
            final int height = (int) Math.round(scaleFactor * fh);

            return setDimensions(new Bounds2D(width, height));
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setRepeats(final boolean repeats) {
            this.repeats = repeats;
            return this;
        }

        public Builder setTicksPerFrame(final int ticksPerFrame) {
            this.ticksPerFrame = ticksPerFrame;
            return this;
        }

        @Override
        public AnimatedMenuElement build() {
            final double scaleFactor = dimensions.width() / (double) fw;
            final GameImage[] frames = Arrays.stream(this.frames)
                    .map(img -> scaleFactor == 1.0
                            ? new GameImage(img)
                            : ImageProcessing.scale(img, dimensions.width(), dimensions.height()))
                    .toArray(GameImage[]::new);

            return new AnimatedMenuElement(position, dimensions,
                    anchor, frames, repeats, ticksPerFrame);
        }
    }
}
