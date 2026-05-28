package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import static com.jordanbunke.painterly.util.Layout.ScreenBox.CONTEXT_BAR;

public final class ContextBar extends MenuElement {
    private static ContextBar INSTANCE;

    // TODO - fields

    static {
        INSTANCE = build();
    }

    public ContextBar() {
        super(new Coord2D(CONTEXT_BAR.x.get(), CONTEXT_BAR.y.get()),
                new Bounds2D(1, 1), Anchor.LEFT_TOP, false);
    }

    public static ContextBar get() {
        return INSTANCE;
    }

    private static ContextBar build() {
        // TODO - potential builder architecture; see MenuBar

        return new ContextBar();
    }

    public static void regen() {
        INSTANCE = build();
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
}
