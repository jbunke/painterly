package com.jordanbunke.painterly.resources;

public enum ResourceCategory {
    TOOLTIP, UI_TEXT, VALUE;
    // TODO - add categories

    private static final String[] NO_PLURAL_IF_ENDS_IN;

    static {
        NO_PLURAL_IF_ENDS_IN = new String[] {
                "text"
        };
    }

    public String formattedName() {
        return switch (this) {
            case UI_TEXT -> "UI text";
            // TODO - additional non-trivial (language-dependent) resource categories
            default -> name().toLowerCase();
        };
    }

    public String filename() {
        final String filename = name().toLowerCase();

        for (String ending : NO_PLURAL_IF_ENDS_IN)
            if (filename.endsWith(ending))
                return filename;

        return filename + "s";
    }
}
