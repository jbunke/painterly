package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.resources.ResourceCode;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

import java.util.Objects;

@FunctionalInterface
public interface Validator<T> {
    Pair<Boolean, ResourceCode> check(T value);

    static Pair<Boolean, ResourceCode> nonNull(Object o) {
        final boolean pass = Objects.nonNull(o);
        return new Pair<>(pass, pass ? RC_NA : RC_DIALOG_VARIABLE_CANNOT_BE_NULL);
    }

    @SuppressWarnings("unused")
    static Pair<Boolean, ResourceCode> never() {
        return new Pair<>(false, RC_NA);
    }

    static Double nullableParseDouble(final String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    static Integer nullableParseInt(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
