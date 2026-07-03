package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.painterly.core.domains.focus.FocusBoxMode;
import com.jordanbunke.painterly.core.domains.interval.ProgressManager;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.menu.elements.complex.context_bar.int_value.IntValueContainer;
import com.jordanbunke.painterly.menu.elements.complex.context_bar.multichoice.OptionsContainer;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.EnumUtils;

import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.painterly.events.actions.ProjectAction.*;
import static com.jordanbunke.painterly.menu.elements.complex.context_bar.ContextBarSection.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.CONTEXT_BAR;

public final class ContextBar extends MenuElement {
    private static ContextBar INSTANCE;

    private final ContextBarElement[] elements;

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

        // tool
        final int toolWidth = EnumUtils.stream(ToolManager.ToolEnum.class)
                .mapToInt(t -> t.setter.getWidthAllotment(false))
                .reduce(1, Math::max);
        final ContextBarElement.Builder toolBuilder = ContextBarElement
                .init(TOOL, RC_CB_CURRENT_TOOL, 0)
                .setTooltipCode(RC_CB_CURRENT_TOOL)
                .setWidth(toolWidth)
                .setRequiresProject(false)
                .setIconCodeGetter(() ->
                        ToolManager.getCurrentAction().getIconCode());
        final OptionsContainer toolOptions = OptionsContainer
                .init(toolBuilder.getPosition())
                .setAnchor(toolBuilder.complementaryReflected())
                .setActionsFromEnum(ToolManager.ToolEnum.class, t -> t.setter)
                .setPostExecution(() -> ContextBar.get().collapseSection(TOOL))
                .build();
        final ContextBarElement tool = toolBuilder
                .setExpansion(toolOptions)
                .build();
        elements.add(tool);

        // strokes completed
        final ContextBarElement strokesCompleted = ContextBarElement
                .init(STROKES_COMPLETED, RC_CB_STROKES_COMPLETED, tool.nextX())
                .setTooltipCode(RC_CB_STROKES_COMPLETED)
                .setStaticIconCode(RC_CB_STROKES_COMPLETED)
                .setWidth(CONTEXT_BAR_NUM_SECTION_LARGE_WIDTH)
                .build();
        elements.add(strokesCompleted);

        // strokes attempted
        final ContextBarElement strokesAttempted = ContextBarElement
                .init(STROKES_ATTEMPTED, RC_CB_STROKES_ATTEMPTED,
                        strokesCompleted.nextX())
                .setTooltipCode(RC_CB_STROKES_ATTEMPTED)
                .setStaticIconCode(RC_CB_STROKES_ATTEMPTED)
                .setWidth(CONTEXT_BAR_NUM_SECTION_LARGE_WIDTH)
                .build();
        elements.add(strokesAttempted);

        // interval progress
        final ContextBarElement.Builder intervalProgressBuilder =
                ContextBarElement.init(INTERVAL_PROGRESS,
                                RC_CB_INTERVAL_PROGRESS,
                                strokesAttempted.nextX())
                        .setTooltipCode(RC_CB_INTERVAL_PROGRESS)
                        .setIconCodeGetter(
                                p -> p.strokeManager.isTickMode() == StrokeManager.ATTEMPTED
                                        ? RC_TICK_MODE_ATTEMPTED : RC_TICK_MODE_COMPLETED,
                                RC_CB_INTERVAL_PROGRESS)
                        .setWidth(CONTEXT_BAR_NUM_SECTION_SMALL_WIDTH);
        final OptionsContainer intervalProgressOptions = OptionsContainer
                .init(intervalProgressBuilder.getPosition())
                .setAnchor(intervalProgressBuilder.complementaryReflected())
                .setActions(SET_TICK_MODE_ATTEMPTED, SET_TICK_MODE_COMPLETED)
                .setPostExecution(() -> ContextBar.get()
                        .collapseSection(INTERVAL_PROGRESS))
                .build();
        final ContextBarElement intervalProgress = intervalProgressBuilder
                .setExpansion(intervalProgressOptions)
                .build();
        elements.add(intervalProgress);

        // interval target
        final ContextBarElement.Builder intervalTargetBuilder =
                ContextBarElement.init(INTERVAL_TARGET, RC_CB_INTERVAL_TARGET,
                                intervalProgress.nextX())
                        .setTooltipCode(RC_CB_INTERVAL_TARGET)
                        .setStaticIconCode(RC_CB_INTERVAL_TARGET)
                        .setWidth(CONTEXT_BAR_NUM_SECTION_SMALL_WIDTH);
        final IntValueContainer intervalTargetValue = IntValueContainer
                .init(intervalTargetBuilder.getPosition())
                .setAnchor(intervalTargetBuilder.complementaryReflected())
                .setMinValue(Constants.MIN_INTERVAL_TARGET)
                .setMaxValue(Constants.MAX_INTERVAL_TARGET)
                .setProjectGetter(p -> p.strokeManager.getIntervalTarget())
                .setProjectSetter((p, i) ->
                        p.strokeManager.setIntervalTarget(i))
                .build();
        final ContextBarElement intervalTarget = intervalTargetBuilder
                .setExpansion(intervalTargetValue)
                .build();
        elements.add(intervalTarget);

        // focus box mode
        final int focusBoxModeWidth = EnumUtils.stream(FocusBoxMode.class)
                .mapToInt(f -> f.setter.getWidthAllotment(false))
                .reduce(1, Math::max);
        final ContextBarElement.Builder focusBoxModeBuilder =
                ContextBarElement.init(FOCUS_BOX_MODE, RC_CB_FOCUS_BOX_MODE,
                                intervalTarget.nextX())
                        .setTooltipCode(RC_CB_FOCUS_BOX_MODE)
                        .setWidth(focusBoxModeWidth)
                        .setIconCodeGetter(p -> p.focusManager
                                .getFocusBoxMode().setter.getIconCode(),
                                RC_FB_FREE);
        final OptionsContainer focusBoxModeOptions = OptionsContainer
                .init(focusBoxModeBuilder.getPosition())
                .setAnchor(focusBoxModeBuilder.complementaryReflected())
                .setActionsFromEnum(FocusBoxMode.class, t -> t.setter)
                .setPostExecution(() -> ContextBar.get()
                        .collapseSection(FOCUS_BOX_MODE))
                .build();
        final ContextBarElement focusBoxMode = focusBoxModeBuilder
                .setExpansion(focusBoxModeOptions)
                .build();
        elements.add(focusBoxMode);

        // X divisions
        final ContextBarElement.Builder divsXBuilder = ContextBarElement
                .init(DIVS_X, RC_CB_DIVS_X, focusBoxMode.nextX())
                .setTooltipCode(RC_CB_DIVS_X)
                .setStaticIconCode(RC_CB_DIVS_X)
                .setWidth(CONTEXT_BAR_NUM_SECTION_SMALL_WIDTH);
        final IntValueContainer divsXValue = IntValueContainer
                .init(divsXBuilder.getPosition())
                .setAnchor(divsXBuilder.complementaryReflected())
                .setMinValue(1)
                .setMaxValue(Constants.MAX_BOX_DIVS)
                .setProjectGetter(p -> p.focusManager.getDivsX())
                .setProjectSetter((p, i) ->
                        p.focusManager.setDivsX(i))
                .build();
        final ContextBarElement divsX = divsXBuilder
                .setExpansion(divsXValue)
                .build();
        elements.add(divsX);

        // Y divisions
        final ContextBarElement.Builder divsYBuilder = ContextBarElement
                .init(DIVS_Y, RC_CB_DIVS_Y, divsX.nextX())
                .setTooltipCode(RC_CB_DIVS_Y)
                .setStaticIconCode(RC_CB_DIVS_Y)
                .setWidth(CONTEXT_BAR_NUM_SECTION_SMALL_WIDTH);
        final IntValueContainer divsYValue = IntValueContainer
                .init(divsYBuilder.getPosition())
                .setAnchor(divsYBuilder.complementaryReflected())
                .setMinValue(1)
                .setMaxValue(Constants.MAX_BOX_DIVS)
                .setProjectGetter(p -> p.focusManager.getDivsY())
                .setProjectSetter((p, i) ->
                        p.focusManager.setDivsY(i))
                .build();
        final ContextBarElement divsY = divsYBuilder
                .setExpansion(divsYValue)
                .build();
        elements.add(divsY);

        // similarity
        final ContextBarElement.Builder similarityBuilder = ContextBarElement
                .init(SIMILARITY, RC_CB_SIMILARITY, CONTEXT_BAR.atX(1d))
                .setTooltipCode(RC_CB_SIMILARITY)
                .setIconCodeGetter(
                        p -> p.progressManager.isDisplay() == ProgressManager.FOCUS
                                ? RC_DISPLAY_FOCUS : RC_DISPLAY_GLOBAL,
                        RC_DISPLAY_GLOBAL)
                .setWidth(CONTEXT_BAR_NUM_SECTION_LARGE_WIDTH)
                .setAlignment(Alignment.RIGHT)
                .setAnchor(Anchor.RIGHT_TOP);
        final OptionsContainer similarityOptions = OptionsContainer
                .init(similarityBuilder.getPosition())
                .setAnchor(similarityBuilder.complementaryReflected())
                .setActions(SET_DISPLAY_FOCUS, SET_DISPLAY_GLOBAL)
                .setPostExecution(() -> ContextBar.get()
                        .collapseSection(SIMILARITY))
                .build();
        final ContextBarElement similarity = similarityBuilder
                .setExpansion(similarityOptions)
                .build();
        elements.add(similarity);

        return new ContextBar(elements.toArray(ContextBarElement[]::new));
    }

    public static void regen() {
        INSTANCE = build();
    }

    public void collapseSection(final ContextBarSection section) {
        for (ContextBarElement element : elements)
            if (element.section == section)
                element.collapse();
    }

    public void collapseOthers(final ContextBarElement affected) {
        for (ContextBarElement element : elements)
            if (!element.equals(affected))
                element.collapse();
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
