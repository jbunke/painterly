package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.jordanbunke.delta_time.events.Key.*;

public enum GlobalAction
        implements IAction</* TODO - evaluate whether more suitable type exists */ Runnable> {
    CLOSE_DIALOG(KeyboardShortcut.single(ESCAPE), /* TODO */ () -> {}),
    TOGGLE_FULLSCREEN(KeyboardShortcut.single(ESCAPE), /* TODO */ () -> {}),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Runnable behaviour;

    private ResourceCode iconCode;
    private Supplier<Boolean> precondition;

    GlobalAction(
            final KeyboardShortcut shortcut,
            final Runnable behaviour
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
    public Predicate<Runnable> getPrecondition() {
        return precondition == null
                ? null : t -> precondition.get();
    }

    @Override
    public Consumer<Runnable> getBehaviour() {
        return t -> behaviour.run();
    }
}
