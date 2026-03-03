package com.jordanbunke.painterly.util;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public final class StringUtils {
    public static String nameFromID(final String id) {
        return Arrays.stream(id.split("[_\\-]"))
                .map(StringUtils::capitalizeFirstLetter)
                .reduce((a, b) -> a + " " + b)
                .orElse(capitalizeFirstLetter(id));
    }

    private static String capitalizeFirstLetter(final String word) {
        return String.valueOf(word.charAt(0)).toUpperCase() +
                word.substring(1).toLowerCase();
    }

    public static String ifTransform(
            final String s, final Predicate<String> condition,
            final Function<String, String> transformation
    ) {
        return condition.test(s) ? transformation.apply(s) : s;
    }
}
