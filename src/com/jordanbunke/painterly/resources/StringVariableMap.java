package com.jordanbunke.painterly.resources;

import java.util.HashMap;
import java.util.Map;

public final class StringVariableMap {
    public enum ID {
        FILENAME, PROJECT_NAME, PW_RANK, PW_DIVISIONS,
        INTERVAL_COMPLETED, INTERVAL_TOTAL, INTERVAL_PERC,
        WORST_PERC
        ;
    }

    private static final Map<ID, String> map;

    static {
        map = new HashMap<>();
    }

    public static String retrieve(final ID id) {
        return map.getOrDefault(id, "");
    }

    public static void post(final ID id, final String value) {
        map.put(id, value);
    }
}
