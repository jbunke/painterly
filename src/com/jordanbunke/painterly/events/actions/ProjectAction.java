package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.core.domains.focus.FocusBoxMode;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.dialog.data.menus.DuplicateProject;
import com.jordanbunke.painterly.dialog.data.menus.EditProjectSettings;
import com.jordanbunke.painterly.dialog.data.menus.SaveAs;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ProjectAction implements IAction<Project>, ISubMenuEntry {
    EXPORT(new Builder(RC_EXPORT)
            .inheritTooltipCode()
            // TODO - .inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, E))
            .setBehaviour(p -> p.saveManager.export())),
    SAVE(new Builder(RC_SAVE)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, S))
            .setBehaviour(p -> p.saveManager.save())),
    SAVE_AS(new Builder(RC_SAVE_AS)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, true, S))
            .setBehaviour(p -> {
                SaveAs.get().softReset();
                DialogManager.set(DialogAssembly::saveAs);
            })),
    EDIT_PROJECT_SETTINGS(new Builder(RC_EDIT_PROJECT_SETTINGS)
            .inheritTooltipCode()
            // TODO - .inheritIconCode()
            .setShortcut(new KeyboardShortcut(false, true, S))
            .setBehaviour(p -> {
                EditProjectSettings.get().softReset();
                DialogManager.set(DialogAssembly::editProjectSettings);
            })),
    DUPLICATE_PROJECT(new Builder(RC_DUPLICATE_PROJECT)
            .inheritTooltipCode().inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, D))
            .setBehaviour(p -> {
                DuplicateProject.get().softReset();
                DialogManager.set(DialogAssembly::duplicateProject);
            })),
    TOGGLE_SIM(new Builder(RC_TOGGLE_SIM)
            .setIconCodeFunction(p -> p.isPainting() ? RC_SIM_PAUSE : RC_SIM_RESUME)
            .setShortcut(KeyboardShortcut.single(SPACE))
            .setBehaviour(Project::toggleSimulation)),
    TOGGLE_SOURCE(new Builder(RC_TOGGLE_SOURCE)
            .setShortcut(KeyboardShortcut.single(ENTER))
            .setBehaviour(p -> p.canvas.toggleShowSource())),
    RESET_POSITIONING(new Builder(RC_RESET_POS)
            .setShortcut(new KeyboardShortcut(true, false, ENTER))
            .setBehaviour(p -> p.positioning.reset())),
    FIT_TO_FOCUS_AREA(new Builder(RC_FIT_TO_FOCUS_AREA)
            .setShortcut(new KeyboardShortcut(true, true, ENTER))
            .setBehaviour(p -> p.positioning.fitToFocusArea())
            .setPrecondition(p -> !p.focusManager.isWholeCanvas())),
    RESET_FOCUS_AREA(new Builder(RC_RESET_FOCUS_AREA)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(BACKSPACE))
            .setBehaviour(p -> p.focusManager.resetFocusArea())
            .setPrecondition(p -> !p.focusManager.isWholeCanvas())),
    FOCUS_BOX_AS_FOCUS_AREA(new Builder(RC_FOCUS_BOX_AS_FOCUS_AREA)
            .inheritTooltipCode().inheritIconCode()
            .setShortcut(KeyboardShortcut.single(Q))
            .setBehaviour(p -> p.focusManager.focusBoxAsNewFocusArea())
            .setPrecondition(p -> !p.focusManager.isEntireArea())),
    FOCUS_AREA_AS_FOCUS_BOX(new Builder(RC_FOCUS_AREA_AS_FOCUS_BOX)
            .inheritTooltipCode().inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, Q))
            .setBehaviour(p -> {
                // TODO - dialog reset and set menu
            }).setPrecondition(p -> !p.focusManager.isWholeCanvas())),
    FOCUS_AREA_AS_FOCUS_BOX_MAXIMAL(new Builder(RC_FOCUS_AREA_AS_FOCUS_BOX_MAXIMAL)
            .inheritTooltipCode().inheritIconCode()
            .setShortcut(new KeyboardShortcut(false, true, Q))
            .setBehaviour(p -> p.focusManager.focusAreaAsNewFocusBoxMaximal())
            .setPrecondition(p -> !p.focusManager.isWholeCanvas())),
    CLEAR_FOCUS_BOXES(new Builder(RC_CLEAR_FOCUS_BOXES)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(A))
            .setBehaviour(p -> p.focusManager.clearFocusBoxes())
            .setPrecondition(p -> !p.focusManager.isEntireArea())),
    DELETE_ACTIVE_BOUNDS(new Builder(RC_DELETE_ACTIVE_BOUNDS)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(DELETE))
            .setBehaviour(p -> DialogManager.set(
                    () -> DialogAssembly.aysDeleteActiveBounds(p)))),
    // similarity display setters
    SET_DISPLAY_FOCUS(new Builder(RC_DISPLAY_FOCUS)
            .inheritIconCode()
            .setBehaviour(p -> p.progressManager.setDisplayToFocus())),
    SET_DISPLAY_GLOBAL(new Builder(RC_DISPLAY_GLOBAL)
            .inheritIconCode()
            .setBehaviour(p -> p.progressManager.setDisplayToGlobal())),
    // tick mode setters
    TOGGLE_TICK_MODE(new Builder(RC_TOGGLE_TICK_MODE)
            .setTooltipCode(RC_TOGGLE_TICK_MODE)
            .setIconCodeFunction(p -> p.strokeManager.isTickMode() == StrokeManager.ATTEMPTED
                    ? RC_TICK_MODE_ATTEMPTED : RC_TICK_MODE_COMPLETED)
            .setShortcut(KeyboardShortcut.single(S))
            .setBehaviour(p -> p.strokeManager.toggleTickMode())),
    SET_TICK_MODE_ATTEMPTED(new Builder(RC_TICK_MODE_ATTEMPTED)
            .inheritTooltipCode()
            .inheritIconCode()
            .setBehaviour(p -> p.strokeManager.setTickModeToAttempted())),
    SET_TICK_MODE_COMPLETED(new Builder(RC_TICK_MODE_COMPLETED)
            .inheritTooltipCode()
            .inheritIconCode()
            .setBehaviour(p -> p.strokeManager.setTickModeToCompleted())),
    // focus box augmentation
    INC_DIVS_X(new Builder(RC_NA)
            .setShortcut(new KeyboardShortcut(false, true, RIGHT_ARROW))
            .setBehaviour(p -> p.focusManager.augmentDivsX(1))),
    DEC_DIVS_X(new Builder(RC_NA)
            .setShortcut(new KeyboardShortcut(false, true, LEFT_ARROW))
            .setBehaviour(p -> p.focusManager.augmentDivsX(-1))),
    INC_DIVS_Y(new Builder(RC_NA)
            .setShortcut(new KeyboardShortcut(false, true, UP_ARROW))
            .setBehaviour(p -> p.focusManager.augmentDivsY(1))),
    DEC_DIVS_Y(new Builder(RC_NA)
            .setShortcut(new KeyboardShortcut(false, true, DOWN_ARROW))
            .setBehaviour(p -> p.focusManager.augmentDivsY(-1))),
    // free focus box navigation
    INC_X(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(RIGHT_ARROW))
            .setBehaviour(p -> p.focusManager.augmentX(1))),
    DEC_X(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(LEFT_ARROW))
            .setBehaviour(p -> p.focusManager.augmentX(-1))),
    INC_Y(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(DOWN_ARROW))
            .setBehaviour(p -> p.focusManager.augmentY(1))),
    DEC_Y(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(UP_ARROW))
            .setBehaviour(p -> p.focusManager.augmentY(-1))),
    // focus box mode setters
    SET_FB_FREE(new Builder(RC_FB_FREE)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(L))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FREE))),
    SET_FB_RANDOM(new Builder(RC_FB_RANDOM)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(R))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.RANDOM))),
    SET_FB_FORWARDS(new Builder(RC_FB_FORWARDS)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(F))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.FORWARDS))),
    SET_FB_BACKWARDS(new Builder(RC_FB_BACKWARDS)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(D))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.BACKWARDS))),
    SET_FB_WORST(new Builder(RC_FB_WORST)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(W))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.WORST))),
    SET_FB_PRIORITIZE_WORST(new Builder(RC_FB_PRIORITIZE_WORST)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(E))
            .setBehaviour(p -> p.focusManager.setFocusBoxMode(FocusBoxMode.PRIORITIZE_WORST))),
    ;

    private final KeyboardShortcut shortcut;
    private final Consumer<Project> behaviour;
    private final ResourceCode code, tooltipCode;
    private final Function<Project, ResourceCode> iconCodeFunction;
    private final Predicate<Project> precondition;

    ProjectAction(Builder builder) {
        this.code = builder.code;
        this.shortcut = builder.shortcut;
        this.behaviour = builder.behaviour;
        this.tooltipCode = builder.tooltipCode;
        this.iconCodeFunction = builder.iconCodeFunction;
        this.precondition = builder.precondition;
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
        try {
            return iconCodeFunction.apply(defaultFetch());
        } catch (NullPointerException npe) {
            return RC_NA;
        }
    }

    static class Builder {
        final ResourceCode code;

        KeyboardShortcut shortcut;
        ResourceCode tooltipCode;
        Function<Project, ResourceCode> iconCodeFunction;

        Consumer<Project> behaviour;
        Predicate<Project> precondition;

        Builder(final ResourceCode code) {
            this.code = code;

            shortcut = null;
            tooltipCode = RC_NA;
            iconCodeFunction = p -> RC_NA;

            behaviour = p -> {};
            precondition = null;
        }

        Builder setShortcut(final KeyboardShortcut shortcut) {
            this.shortcut = shortcut;
            return this;
        }

        Builder setTooltipCode(final ResourceCode tooltipCode) {
            this.tooltipCode = tooltipCode;
            return this;
        }

        Builder inheritTooltipCode() {
            return setTooltipCode(code);
        }

        Builder setIconCodeFunction(
                final Function<Project, ResourceCode> iconCodeFunction
        ) {
            this.iconCodeFunction = iconCodeFunction;
            return this;
        }

        Builder inheritIconCode() {
            return setIconCodeFunction(p -> code);
        }

        Builder setBehaviour(final Consumer<Project> behaviour) {
            this.behaviour = behaviour;
            return this;
        }

        Builder setPrecondition(final Predicate<Project> precondition) {
            this.precondition = precondition;
            return this;
        }
    }
}
