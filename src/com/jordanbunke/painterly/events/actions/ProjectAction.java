package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ProjectAction implements IAction<Project>, ISubMenuEntry {
    // SET_FB_MODE_ITERATE(, KeyboardShortcut.single(S), /* TODO */ p -> {}),
    SAVE_AS(RC_NAV_SAVE_AS, new KeyboardShortcut(true, true, S), /* TODO */ p -> {}),
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
