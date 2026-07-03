package com.jordanbunke.painterly.menu.elements.icon_button;

import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractCheckbox;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.funke.core.ConcreteProperty;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.theme.Graphics;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.util.Layout.ICON_DIM;

public final class Checkbox extends AbstractCheckbox {
    private Checkbox(
            final Coord2D position, final Anchor anchor,
            final Supplier<Boolean> getter, final Consumer<Boolean> setter
    ) {
        super(position, new Bounds2D(ICON_DIM, ICON_DIM), anchor,
                new ConcreteProperty<>(getter, setter), Graphics::drawCheckbox);
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    public static class Builder implements MenuElementBuilder<Checkbox> {
        private final Coord2D position;

        private Anchor anchor;
        private Supplier<Boolean> getter;
        private Consumer<Boolean> setter;

        public Builder(final Coord2D position) {
            this.position = position;

            anchor = Anchor.LEFT_TOP;
            getter = () -> false;
            setter = b -> {};
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setGetter(final Supplier<Boolean> getter) {
            this.getter = getter;
            return this;
        }

        public Builder setSetter(final Consumer<Boolean> setter) {
            this.setter = setter;
            return this;
        }

        @Override
        public Checkbox build() {
            return new Checkbox(position, anchor, getter, setter);
        }
    }
}
