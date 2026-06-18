package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.PlaceholderMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;

import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.CONTEXT_BAR;

public final class ContextBar extends MenuElement {
    private static ContextBar INSTANCE;

    private final ContextBarElement[] elements;
    // TODO - fields

    static {
        INSTANCE = build();
    }

    private ContextBar(final ContextBarElement[] elements) {
        super(CONTEXT_BAR.pos(), new Bounds2D(1, 1),
                Anchor.LEFT_TOP, false);

        this.elements = elements;
    }

    public static ContextBar get() {
        return INSTANCE;
    }

    private static ContextBar build() {
        final List<ContextBarElement> elements = new LinkedList<>();

        // TODO - temp: example
        final ContextBarElement test = ContextBarElement
                .init(RC_CB_STROKE_COUNT, 0)
                .setTooltipCode(RC_CB_STROKE_COUNT)
                .setExpansion(new PlaceholderMenuElement())
                .build();

        elements.add(test);

        // TODO

        return new ContextBar(elements.toArray(ContextBarElement[]::new));
    }

    public static void regen() {
        INSTANCE = build();
    }

    public void collapseOthers(final ContextBarElement affected) {
        for (ContextBarElement element : elements) {
            if (!element.equals(affected))
                element.collapse();
        }
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        for (ContextBarElement element : elements)
            element.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        for (ContextBarElement element : elements)
            element.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        for (ContextBarElement element : elements)
            element.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}
}
