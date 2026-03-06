package com.jordanbunke.painterly.menu.dialog;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;

import java.util.function.Supplier;

public final class PopUpDialog extends MenuElementContainer {
    public PopUpDialog(final Coord2D position, final Bounds2D dimensions) {
        super(position, dimensions, Anchor.CENTRAL, true);
    }

    // TODO

    public Builder init(
            final String title
    ) {
        return new Builder(title);
    }

    @Override
    public MenuElement[] getMenuElements() {
        // TODO
        return new MenuElement[0];
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        // TODO
        return false;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO
    }

    @Override
    public void update(final double deltaTime) {
        // TODO
    }

    @Override
    public void render(final GameImage canvas) {
        // TODO
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static class Builder implements MenuElementBuilder<PopUpDialog> {
        private final String title;

        private Supplier<Boolean> precondition;

        Builder(
                final String title
        ) {
            this.title = title;

            precondition = () -> true;
        }

        @Override
        public PopUpDialog build() {
            // TODO
            return null;
        }
    }
}
