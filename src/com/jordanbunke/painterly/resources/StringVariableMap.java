package com.jordanbunke.painterly.resources;

import java.util.HashMap;
import java.util.Map;

public final class StringVariableMap {
    public enum ID {
        PROJECT_NAME
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
