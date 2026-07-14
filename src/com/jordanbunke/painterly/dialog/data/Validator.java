package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Constants;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

@FunctionalInterface
public interface Validator<T> {
    Pair<Boolean, ResourceCode> check(T value);

    static Pair<Boolean, ResourceCode> nonNull(final Object o) {
        final boolean pass = Objects.nonNull(o);
        return new Pair<>(pass, pass ? RC_NA : RC_DIALOG_FB_VARIABLE_CANNOT_BE_NULL);
    }

    @SuppressWarnings("unused")
    static Pair<Boolean, ResourceCode> never() {
        return new Pair<>(false, RC_NA);
    }

    static Pair<Boolean, ResourceCode> always(final Object o) {
        return new Pair<>(true, RC_NA);
    }

    static Pair<Boolean, ResourceCode> validName(
            final String name
    ) {
        if (name == null || name.isEmpty())
            return new Pair<>(false, RC_DIALOG_FB_CANNOT_BE_EMPTY);
        else if (name.trim().isEmpty())
            return new Pair<>(false, RC_DIALOG_FB_CANNOT_BE_ONLY_WHITESPACE);
        else if (nameContainsIllegalChar(name))
            return new Pair<>(false, RC_DIALOG_FB_CONTAINS_INVALID_CHARACTER);

        return new Pair<>(true, RC_NA);
    }

    private static boolean nameContainsIllegalChar(final String name) {
        final Set<Character> ILLEGAL_CHAR_SET = Set.of(
                '/', '\\', ':', '*', '?', '"', '<', '>', '|', '{', '}');

        return ILLEGAL_CHAR_SET.stream()
                .map(c -> name.indexOf(c) >= 0)
                .reduce((a, b) -> a || b).orElse(false);
    }

    static Pair<Boolean, ResourceCode> validFolder(
            final Path folder, final ResourceCode folderResolutionCode
    ) {
        if (folder == null)
            return new Pair<>(false, RC_DIALOG_FB_VARIABLE_CANNOT_BE_NULL);

        return new Pair<>(true, folderResolutionCode);
    }

    static Pair<Boolean, ResourceCode> validAutosaveFrequency(
            final Integer autosaveFrequency
    ) {
        return validBoundedInt(autosaveFrequency,
                Constants.MIN_AUTOSAVE_FREQUENCY,
                Constants.MAX_AUTOSAVE_FREQUENCY,
                RC_NA /* TODO */, RC_NA /* TODO */);
    }

    static Pair<Boolean, ResourceCode> validIntervalTarget(
            final Integer intervalTarget
    ) {
        return validBoundedInt(intervalTarget,
                Constants.MIN_INTERVAL_TARGET,
                Constants.MAX_INTERVAL_TARGET,
                RC_NA /* TODO */, RC_NA /* TODO */);
    }

    private static Pair<Boolean, ResourceCode> validBoundedInt(
            final Integer value,
            final int lowerBound, final int upperBound,
            final ResourceCode fbTooLow, final ResourceCode fbTooHigh
    ) {
        if (value == null)
            return new Pair<>(false, RC_DIALOG_FB_CANNOT_READ_INT);
        else if (value < lowerBound)
            return new Pair<>(false, fbTooLow);
        else if (value > upperBound)
            return new Pair<>(false, fbTooHigh);

        return new Pair<>(true, RC_NA);
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
