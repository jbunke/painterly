package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.core.domains.focus.FocusBoxMode;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.viewport.Viewport;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ProjectAction implements IAction<Project>, ISubMenuEntry {
    SAVE_AS(RC_NAV_SAVE_AS, new KeyboardShortcut(true, true, S), /* TODO */ p -> {}),
    TOGGLE_SIM(RC_TOGGLE_SIM, KeyboardShortcut.single(SPACE), Project::toggleSim),
    TOGGLE_SOURCE(RC_TOGGLE_SOURCE, KeyboardShortcut.single(ENTER),
            p -> p.canvas.toggleShowSource()),
    RESET_POSITIONING(RC_RESET_POS, new KeyboardShortcut(true, false, ENTER),
            p -> Viewport.get().getPositioning().reset()),
    RESET_FOCUS_AREA(RC_RESET_FOCUS_AREA, KeyboardShortcut.single(BACKSPACE),
            p -> p.focusManager.resetFocusArea()),
    CLEAR_FOCUS_BOXES(RC_CLEAR_FOCUS_BOXES, KeyboardShortcut.single(A),
            p -> p.focusManager.clearFocusBoxes()),
    TOGGLE_TICK_MODE(RC_TOGGLE_TICK_MODE, KeyboardShortcut.single(S),
            p -> p.strokeManager.toggleTickMode()),
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
    SET_FB_FREE(RC_FB_FREE, KeyboardShortcut.single(L),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FREE)),
    SET_FB_RANDOM(RC_FB_RANDOM, KeyboardShortcut.single(R),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.RANDOM)),
    SET_FB_FORWARDS(RC_FB_FORWARDS, KeyboardShortcut.single(F),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FORWARDS)),
    SET_FB_BACKWARDS(RC_FB_BACKWARDS, KeyboardShortcut.single(D),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.BACKWARDS)),
    SET_FB_WORST(RC_FB_WORST, KeyboardShortcut.single(W),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.WORST)),
    SET_FB_PRIORITIZE_WORST(RC_FB_PRIORITIZE_WORST, KeyboardShortcut.single(E),
            p -> p.focusManager.setFocusBoxMode(FocusBoxMode.PRIORITIZE_WORST)),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Consumer<Project> behaviour;
    private final ResourceCode code;

    private ResourceCode iconCode;
    private Predicate<Project> precondition;

    ProjectAction(
            final ResourceCode code,
            final KeyboardShortcut shortcut,
            final Consumer<Project> behaviour
    ) {
        this.code = code;
        this.shortcut = shortcut;
        this.behaviour = behaviour;

        iconCode = ResourceCode.RC_NA;
        precondition = null;
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
}
