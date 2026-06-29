package com.jordanbunke.painterly.menu.elements.complex.context_bar.int_value;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.icon_button.IconButton;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.ProjectUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;

public final class IntValueContainer extends MenuElement {
    private final HorzNonScrollSlider slider;
    private final IconButton decrement, increment;

    private final GameImage bg;

    private IntValueContainer(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor,
            final int minValue, final int maxValue,
            final Supplier<Integer> getter, final Consumer<Integer> setter
    ) {
        super(position, dimensions, anchor, true);

        final Coord2D pos = getRenderPosition();
        final int y = (TEXT_BUTTON_DEF_HEIGHT - SLIDER_HEIGHT) / 2;

        slider = HorzNonScrollSlider
                .init(pos.displace(CONTEXT_BAR_PADDING_X, y))
                .setMinValue(minValue).setMaxValue(maxValue)
                .setGetter(getter).setSetter(setter)
                .build();
        decrement = IconButton
                .init(RC_DECREMENT, follow(slider, CONTEXT_BAR_PADDING_X),
                        () -> {
                    final int v = getter.get();

                    if (v > minValue)
                        setter.accept(v - 1);
                }).setTooltipCode(RC_NA).build();
        increment = IconButton
                .init(RC_INCREMENT, follow(decrement, CONTEXT_BAR_PADDING_X),
                        () -> {
                    final int v = getter.get();

                    if (v < maxValue)
                        setter.accept(v + 1);
                }).setTooltipCode(RC_NA).build();

        bg = drawContextBarExpansionBackground(getWidth(), getHeight());
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        slider.process(eventLogger);
        decrement.process(eventLogger);
        increment.process(eventLogger);

        if (mouseIsWithinBounds(eventLogger.getAdjustedMousePosition()))
            Cursor.ping(Cursor.MAIN);
    }

    @Override
    public void update(final double deltaTime) {
        slider.update(deltaTime);
        decrement.update(deltaTime);
        increment.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        draw(bg, canvas);

        slider.render(canvas);
        decrement.render(canvas);
        increment.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static class Builder implements MenuElementBuilder<IntValueContainer> {
        private final Coord2D position;

        private Anchor anchor;

        private int minValue, maxValue;
        private Supplier<Integer> getter;
        private Consumer<Integer> setter;

        public Builder(final Coord2D position) {
            this.position = position;

            anchor = Anchor.LEFT_TOP;

            minValue = 1;
            maxValue = 10;
            getter = () -> 1;
            setter = i -> {};
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
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

        public Builder setProjectGetter(
                final Function<Project, Integer> getter
        ) {
            return setGetter(ProjectUtils.wrapGetter(getter, 0));
        }

        public Builder setProjectSetter(
                final BiConsumer<Project, Integer> setter
        ) {
            return setSetter(ProjectUtils.wrapSetter(setter));
        }

        @Override
        public IntValueContainer build() {
            final int width = (4 * CONTEXT_BAR_PADDING_X) +
                    (2 * ICON_DIM) + SLIDER_DEF_WIDTH;

            return new IntValueContainer(position,
                    new Bounds2D(width, TEXT_BUTTON_DEF_HEIGHT), anchor,
                    minValue, maxValue, getter, setter);
        }
    }
}
