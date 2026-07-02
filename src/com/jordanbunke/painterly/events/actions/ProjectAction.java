package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.core.domains.focus.FocusBoxMode;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.viewport.Viewport;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ProjectAction implements IAction<Project>, ISubMenuEntry {
    EXPORT(RC_EXPORT, false /* TODO */, false /* TODO */,
            new KeyboardShortcut(true, false, E),
            p -> p.saveManager.export()),
    SAVE(RC_SAVE, false /* TODO */, false /* TODO */,
            new KeyboardShortcut(true, false, S),
            p -> p.saveManager.save()),
    SAVE_AS(RC_SAVE_AS, new KeyboardShortcut(true, true, S), /* TODO */ p -> {}),
    TOGGLE_SIM(RC_TOGGLE_SIM, KeyboardShortcut.single(SPACE),
            Project::toggleSimulation),
    TOGGLE_SOURCE(RC_TOGGLE_SOURCE, KeyboardShortcut.single(ENTER),
            p -> p.canvas.toggleShowSource()),
    RESET_POSITIONING(RC_RESET_POS, new KeyboardShortcut(true, false, ENTER),
            p -> Viewport.get().getPositioning().reset()),
    RESET_FOCUS_AREA(RC_RESET_FOCUS_AREA, KeyboardShortcut.single(BACKSPACE),
            p -> p.focusManager.resetFocusArea()),
    FOCUS_BOX_AS_FOCUS_AREA(RC_FOCUS_BOX_AS_FOCUS_AREA, true, false /* TODO */,
            KeyboardShortcut.single(Q),
            p -> p.focusManager.focusBoxAsNewFocusArea()),
    CLEAR_FOCUS_BOXES(RC_CLEAR_FOCUS_BOXES, KeyboardShortcut.single(A),
            p -> p.focusManager.clearFocusBoxes()),
    DELETE_ACTIVE_BOUNDS(RC_DELETE_ACTIVE_BOUNDS, true, false /* TODO */,
            KeyboardShortcut.single(DELETE),
            p -> DialogManager.set(
                    () -> DialogAssembly.aysDeleteActiveBounds(p))),
    // similarity display setters
    SET_DISPLAY_FOCUS(RC_DISPLAY_FOCUS, false, false /* TODO */, null,
            p -> p.progressManager.setDisplayToFocus()),
    SET_DISPLAY_GLOBAL(RC_DISPLAY_GLOBAL, false, false /* TODO */, null,
            p -> p.progressManager.setDisplayToGlobal()),
    // tick mode setters
    TOGGLE_TICK_MODE(RC_TOGGLE_TICK_MODE, KeyboardShortcut.single(S),
            p -> p.strokeManager.toggleTickMode()),
    SET_TICK_MODE_ATTEMPTED(RC_TICK_MODE_ATTEMPTED, true, true, null,
            p -> p.strokeManager.setTickModeToAttempted()),
    SET_TICK_MODE_COMPLETED(RC_TICK_MODE_COMPLETED, true, true, null,
            p -> p.strokeManager.setTickModeToCompleted()),
    // focus box augmentation
    INC_DIVS_X(RC_NA, new KeyboardShortcut(false, true, RIGHT_ARROW),
            p -> p.focusManager.augmentDivsX(1)),
    DEC_DIVS_X(RC_NA, new KeyboardShortcut(false, true, LEFT_ARROW),
            p -> p.focusManager.augmentDivsX(-1)),
    INC_DIVS_Y(RC_NA, new KeyboardShortcut(false, true, UP_ARROW),
            p -> p.focusManager.augmentDivsY(1)),
    DEC_DIVS_Y(RC_NA, new KeyboardShortcut(false, true, DOWN_ARROW),
            p -> p.focusManager.augmentDivsY(-1)),
    // free focus box navigation
    INC_X(RC_NA, KeyboardShortcut.single(RIGHT_ARROW),
            p -> p.focusManager.augmentX(1)),
    DEC_X(RC_NA, KeyboardShortcut.single(LEFT_ARROW),
            p -> p.focusManager.augmentX(-1)),
    INC_Y(RC_NA, KeyboardShortcut.single(DOWN_ARROW),
            p -> p.focusManager.augmentY(1)),
    DEC_Y(RC_NA, KeyboardShortcut.single(UP_ARROW),
            p -> p.focusManager.augmentY(-1)),
    // focus box mode setters
    SET_FB_FREE(RC_FB_FREE, true, true,
            KeyboardShortcut.single(L),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FREE)),
    SET_FB_RANDOM(RC_FB_RANDOM, true, true,
            KeyboardShortcut.single(R),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.RANDOM)),
    SET_FB_FORWARDS(RC_FB_FORWARDS, true, true,
            KeyboardShortcut.single(F),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FORWARDS)),
    SET_FB_BACKWARDS(RC_FB_BACKWARDS, true, true,
            KeyboardShortcut.single(D),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.BACKWARDS)),
    SET_FB_WORST(RC_FB_WORST, true, true,
            KeyboardShortcut.single(W),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.WORST)),
    SET_FB_PRIORITIZE_WORST(RC_FB_PRIORITIZE_WORST, true, true,
            KeyboardShortcut.single(E),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.PRIORITIZE_WORST)),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        FOCUS_BOX_AS_FOCUS_AREA.precondition =
                p -> !p.focusManager.isEntireArea();
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Consumer<Project> behaviour;
    private final ResourceCode code, tooltipCode;

    private ResourceCode iconCode;
    private Predicate<Project> precondition;

    ProjectAction(
            final ResourceCode code,
            final ResourceCode tooltipCode, final ResourceCode iconCode,
            final KeyboardShortcut shortcut,
            final Consumer<Project> behaviour
    ) {
        this.code = code;
        this.shortcut = shortcut;
        this.behaviour = behaviour;

        this.tooltipCode = tooltipCode;
        this.iconCode = iconCode;
        precondition = null;
    }

    ProjectAction(
            final ResourceCode code,
            final boolean inheritTooltip, final boolean inheritIcon,
            final KeyboardShortcut shortcut,
            final Consumer<Project> behaviour
    ) {
        this(code, inheritTooltip ? code : RC_NA,
                inheritIcon ? code : RC_NA, shortcut, behaviour);
    }

    ProjectAction(
            final ResourceCode code,
            final KeyboardShortcut shortcut,
            final Consumer<Project> behaviour
    ) {
        this(code, false, false, shortcut, behaviour);
    }

    @Override
    public Project defaultFetch() {
        return ProjectManager.get().getProject();
    }

    @Override
    public int getWidthAllotment() {
        return IAction.super.getWidthAllotment();
    }

    @Override
    public KeyboardShortcut getShortcut() {
        return shortcut;
    }

    @Override
    public Predicate<Project> getPrecondition() {
        return precondition;
    }

    @Override
    public Consumer<Project> getBehaviour() {
        return behaviour;
    }

    @Override
    public ResourceCode getCode() {
        return code;
    }

    @Override
    public ResourceCode getTooltipCode() {
        return tooltipCode;
    }

    @Override
    public ResourceCode getIconCode() {
        return iconCode;
    }
}
