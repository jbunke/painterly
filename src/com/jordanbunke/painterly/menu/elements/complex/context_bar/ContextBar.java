package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.PlaceholderMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;

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

        // TODO - temp

        // tool
        // TODO - width, expansion, dynamic icon
        final ContextBarElement tool = ContextBarElement
                .init(RC_CB_CURRENT_TOOL, 0)
                .setTooltipCode(RC_CB_CURRENT_TOOL)
                .setRequiresProject(false)
                .setAlignment(Alignment.LEFT)
                .setExpansion(new PlaceholderMenuElement())
                .build();
        elements.add(tool);

        // stroke count
        // TODO - width
        final ContextBarElement strokeCount = ContextBarElement
                .init(RC_CB_STROKE_COUNT, tool.nextX())
                .setTooltipCode(RC_CB_STROKE_COUNT)
                .build();
        elements.add(strokeCount);

        // interval progress
        // TODO - expansion, width, icon
        final ContextBarElement intervalProgress = ContextBarElement
                .init(RC_CB_INTERVAL_PROGRESS, strokeCount.nextX())
                .setTooltipCode(RC_CB_INTERVAL_PROGRESS)
                .setExpansion(new PlaceholderMenuElement())
                .setWidthFromPercentage(0.05)
                .build();
        elements.add(intervalProgress);

        // interval target
        // TODO - expansion, width, icon
        final ContextBarElement intervalTarget = ContextBarElement
                .init(RC_CB_INTERVAL_TARGET, intervalProgress.nextX())
                .setTooltipCode(RC_CB_INTERVAL_TARGET)
                .setExpansion(new PlaceholderMenuElement())
                .setWidthFromPercentage(0.05)
                .build();
        elements.add(intervalTarget);

        // focus box mode
        // TODO - width, expansion, icon?
        final ContextBarElement focusBoxMode = ContextBarElement
                .init(RC_CB_FOCUS_BOX_MODE, intervalTarget.nextX())
                .setTooltipCode(RC_CB_FOCUS_BOX_MODE)
                .setAlignment(Alignment.LEFT)
                .setExpansion(new PlaceholderMenuElement())
                .build();
        elements.add(focusBoxMode);

        // X divisions
        // TODO - expansion, width, icon
        final ContextBarElement divsX = ContextBarElement
                .init(RC_CB_DIVS_X, focusBoxMode.nextX())
                .setTooltipCode(RC_CB_DIVS_X)
                .setExpansion(new PlaceholderMenuElement())
                .setWidthFromPercentage(0.05)
                .build();
        elements.add(divsX);

        // Y divisions
        // TODO - expansion, width, icon
        final ContextBarElement divsY = ContextBarElement
                .init(RC_CB_DIVS_Y, divsX.nextX())
                .setTooltipCode(RC_CB_DIVS_Y)
                .setExpansion(new PlaceholderMenuElement())
                .setWidthFromPercentage(0.05)
                .build();
        elements.add(divsY);

        // similarity
        // TODO - expansion, update tooltip, width, icon
        final ContextBarElement similarity = ContextBarElement
                .init(RC_CB_SIMILARITY, CONTEXT_BAR.atX(1d))
                .setTooltipCode(RC_CB_SIMILARITY)
                .setAlignment(Alignment.RIGHT)
                .setAnchor(Anchor.RIGHT_TOP)
                .build();
        elements.add(similarity);

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
