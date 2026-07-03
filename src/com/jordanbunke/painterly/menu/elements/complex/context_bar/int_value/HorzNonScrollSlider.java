package com.jordanbunke.painterly.menu.elements.complex.context_bar.int_value;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.ConcreteProperty;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.scroll.Slider;
import com.jordanbunke.painterly.theme.Theme;
import com.jordanbunke.painterly.theme.ThemeManager;
import com.jordanbunke.painterly.util.Cursor;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.menu.elements.Button.sim;
import static com.jordanbunke.painterly.util.Layout.*;

public final class HorzNonScrollSlider extends Slider {
    private GameImage base, highlight, sliding;

    private HorzNonScrollSlider(
            final Coord2D position, final int width,
            final int minValue, final int maxValue,
            final Supplier<Integer> getter, final Consumer<Integer> setter
    ) {
        super(position, new Bounds2D(width, SLIDER_HEIGHT),
                minValue, maxValue, new ConcreteProperty<>(getter, setter),
                SLIDER_BALL_DIM);
        updateAssets();
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    @Override
    protected GameImage drawSliding() {
        return sliding;
    }

    @Override
    protected GameImage drawHighlighted() {
        return highlight;
    }

    @Override
    protected GameImage drawBasic() {
        return base;
    }

    @Override
    protected int getCoordDimension(final Coord2D position) {
        return position.x;
    }

    @Override
    protected int getSizeDimension() {
        return getWidth();
    }

    @Override
    protected void updateAssets() {
        final double fractionX = getSliderFraction();
        final Theme theme = ThemeManager.get();

        base = theme.drawHorzSlider(getWidth(), getHeight(),
                fractionX, sim(false, false));
        highlight = theme.drawHorzSlider(getWidth(), getHeight(),
                fractionX, sim(false, true));
        sliding = theme.drawHorzSlider(getWidth(), getHeight(),
                fractionX, sim(true, false));
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isSliding())
            Cursor.force(Cursor.HORZ_SCROLL);
        else if (isHighlighted())
            Cursor.ping(Cursor.HORZ_SCROLL);
    }

    public static class Builder implements MenuElementBuilder<HorzNonScrollSlider> {
        private final Coord2D position;

        private int width;

        private int minValue, maxValue;
        private Supplier<Integer> getter;
        private Consumer<Integer> setter;

        public Builder(final Coord2D position) {
            this.position = position;

            this.width = SLIDER_DEF_WIDTH;

            minValue = 1;
            maxValue = 10;
            getter = () -> 1;
            setter = i -> {};
        }

        public Builder setWidth(final int width) {
            this.width = Math.max(2 * SLIDER_BALL_DIM, width);
            return this;
        }

        public Builder setMinValue(final int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder setMaxValue(final int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder setGetter(final Supplier<Integer> getter) {
            this.getter = getter;
            return this;
        }

        public Builder setSetter(final Consumer<Integer> setter) {
            this.setter = setter;
            return this;
        }

        @Override
        public HorzNonScrollSlider build() {
            return new HorzNonScrollSlider(position, width,
                    minValue, maxValue, getter, setter);
        }
    }
}
