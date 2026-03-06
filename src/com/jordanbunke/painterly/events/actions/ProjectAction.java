package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.delta_time.events.Key.*;

public enum ProjectAction implements IAction<Project> {
    SET_FB_MODE_ITERATE(KeyboardShortcut.single(S), /* TODO */ p -> {}),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Consumer<Project> behaviour;

    private ResourceCode iconCode;
    private Predicate<Project> precondition;

    ProjectAction(
            final KeyboardShortcut shortcut,
            final Consumer<Project> behaviour
    ) {
        this.shortcut = shortcut;
        this.behaviour = behaviour;

        iconCode = ResourceCode.RC_NA;
        precondition = null;
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
}
