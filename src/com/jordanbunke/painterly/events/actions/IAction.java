package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.jordanbunke.painterly.util.Graphics.drawKeyboardShortcut;
import static com.jordanbunke.painterly.util.Graphics.standardTextWidth;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.MENU_BAR_PADDING_X;

public interface IAction<T> {
    KeyboardShortcut getShortcut();
    Predicate<T> getPrecondition();
    Consumer<T> getBehaviour();
    ResourceCode getCode();
    ResourceCode getTooltipCode();
    ResourceCode getIconCode();
    T defaultFetch();

    default boolean requiresNonNull() {
        return true;
    }

    default boolean isPassing() {
        final T fetched = defaultFetch();
        final Predicate<T> precondition = getPrecondition();

        if (fetched == null && requiresNonNull())
            return false;

        return precondition == null || precondition.test(fetched);
    }

    default boolean execute() {
        final T fetched = defaultFetch();

        if (!(requiresNonNull() && fetched == null))
            return tryExecute(fetched);

        return false;
    }

    default boolean tryForMatchingKeyStroke(
            final InputEventLogger eventLogger,
            final T t
    ) {
        final KeyboardShortcut shortcut = getShortcut();

        if (shortcut != null) {
            final GameEvent event = shortcut.checkIfPressed(eventLogger);

            if (event == null)
                return false;

            final boolean success = tryExecute(t);
            if (success)
                event.markAsProcessed();

            return success;
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

    default int getWidthAllotment() {
        final ResourceCode code = getCode();
        final KeyboardShortcut shortcut = getShortcut();

        int width = 0;

        final String text = LanguageData.retrieveUIText(code);

        // initial padding
        width += MENU_BAR_PADDING_X;

        // icon allotment; does not depend on whether there is a valid icon code
        width += ICON_DIM;

        // between icon and text
        width += MENU_BAR_PADDING_X;

        // text
        width += standardTextWidth(text);

        if (shortcut != null) {
            // text to shortcut divider
            width += MENU_BAR_DIVIDER_WIDTH;

            // shortcut
            width += drawKeyboardShortcut(shortcut).getWidth();
        }

        // final padding
        width += MENU_BAR_PADDING_X;

        return width;
    }
}
