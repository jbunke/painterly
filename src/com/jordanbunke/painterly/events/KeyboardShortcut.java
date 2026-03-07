package com.jordanbunke.painterly.events;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameKeyEvent;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.util.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.delta_time.events.GameKeyEvent.*;
import static com.jordanbunke.delta_time.events.Key.*;

public final class KeyboardShortcut {
    public final boolean ctrl, shift;
    public final GameKeyEvent keyStroke;

    public KeyboardShortcut(
            final boolean ctrl, final boolean shift, final Key key
    ) {
        this.ctrl = ctrl;
        this.shift = shift;

        keyStroke = newKeyStroke(key, Action.PRESS);
    }

    public static KeyboardShortcut single(final Key key) {
        return new KeyboardShortcut(false, false, key);
    }

    public static boolean areModKeysPressed(
            final boolean ctrl, final boolean shift,
            final InputEventLogger eventLogger
    ) {
        return ctrl == eventLogger.isPressed(CTRL_OR_COMMAND) &&
                shift == eventLogger.isPressed(SHIFT);
    }

    public GameEvent checkIfPressed(final InputEventLogger eventLogger) {
        if (!areModKeysPressed(ctrl, shift, eventLogger))
            return null;

        final List<GameEvent> eventList = eventLogger.getUnprocessedEvents();

        for (GameEvent event : eventList) {
            if (event.isProcessed())
                continue;

            if (event.equals(keyStroke))
                return event;
        }

        return null;
    }

    public String[] asStringArray() {
        final List<Key> keyList = new LinkedList<>();

        if (ctrl) keyList.add(CTRL_OR_COMMAND);
        if (shift) keyList.add(SHIFT);

        keyList.add(keyStroke.key);

        return keyList.stream()
                .map(KeyboardShortcut::formatKey)
                .toArray(String[]::new);
    }

    @Override
    public String toString() {
        return Arrays.stream(asStringArray())
                .reduce((a, b) -> a + " + " + b)
                .orElse(formatKey(keyStroke.key));
    }

    private static String formatKey(final Key key) {
        return StringUtils.capitalizeFirstLetter(key.simpleName());
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof KeyboardShortcut that)
            return this.ctrl == that.ctrl && this.shift == that.shift &&
                    this.keyStroke.equals(that.keyStroke);

        return false;
    }
}
