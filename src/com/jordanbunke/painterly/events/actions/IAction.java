package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.events.KeyboardShortcut;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface IAction<T> {
    KeyboardShortcut getShortcut();
    Predicate<T> getPrecondition();
    Consumer<T> getBehaviour();

    default boolean tryForMatchingKeyStroke(
            final InputEventLogger eventLogger,
            final T t
    ) {
        final KeyboardShortcut shortcut = getShortcut();

        if (shortcut != null) {
            final GameEvent event = shortcut.checkIfPressed(eventLogger);

            if (event == null)
                return false;

            return tryExecute(t);
        }

        return false;
    }

    default boolean tryExecute(final T t) {
        final Predicate<T> precondition = getPrecondition();

        if (precondition == null || precondition.test(t)) {
            getBehaviour().accept(t);
            return true;
        }

        return false;
    }
}
