package com.jordanbunke.painterly.resources;

public enum ResourceCategory {
    TOOLTIP, UI_TEXT, VALUE;
    // TODO - add categories

    private static final String[] SAME_PLURAL_ENDINGS;

    static {
        SAME_PLURAL_ENDINGS = new String[] {
                "text" // TODO
        };
    }

    public String formattedName() {
        return switch (this) {
            case UI_TEXT -> "UI text";
            // TODO - additional non-trivial (language-dependent) resource categories
            default -> name().toLowerCase();
        };
    }

    public String suffix() {
        final String suffix = "_" + name().toLowerCase();

        for (String ending : SAME_PLURAL_ENDINGS)
            if (suffix.endsWith(ending))
                return suffix;

        return suffix + "s";
    }
}
