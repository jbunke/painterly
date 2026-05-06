package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.jordanbunke.delta_time.events.Key.*;

public enum GlobalAction
        implements IAction</* TODO - evaluate whether more suitable type exists */ Runnable> {
    DIALOG_CLOSE(KeyboardShortcut.single(ESCAPE), DialogManager::close),
    DIALOG_OK(KeyboardShortcut.single(ENTER),
            () -> DialogManager.get().variableSet.ok()),
    TOGGLE_FULLSCREEN(KeyboardShortcut.single(ESCAPE), /* TODO */ () -> {}),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        DIALOG_CLOSE.precondition = DialogManager::has;
        DIALOG_OK.precondition = () -> DialogManager.has() &&
                DialogManager.get().variableSet.validate();
        TOGGLE_FULLSCREEN.precondition = () -> !DialogManager.has();
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

    public boolean tryForMatchingKeyStroke(final InputEventLogger eventLogger) {
        return IAction.super.tryForMatchingKeyStroke(eventLogger, null);
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
