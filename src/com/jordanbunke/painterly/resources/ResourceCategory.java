package com.jordanbunke.painterly.resources;

import java.util.Arrays;

public enum ResourceCategory {
    TOOLTIP, UI_TEXT, VALUE,
    CHANGELOG(FileType.TXT), ROADMAP(FileType.TXT);
    // TODO - add categories

    private final FileType fileType;

    ResourceCategory(final FileType fileType) {
        this.fileType = fileType;
    }

    ResourceCategory() {
        this(FileType.JSON);
    }

    public String formattedName() {
        return this == UI_TEXT ? "UI text" : name().toLowerCase();
    }

    public String filename() {
        final String filename = name().toLowerCase();

        return switch (this) {
            case TOOLTIP, VALUE -> filename + "s";
            default -> filename;
        } + fileType;
    }

    public static ResourceCategory[] jsonCategories() {
        return Arrays.stream(values())
                .filter(c -> c.fileType == FileType.JSON)
                .toArray(ResourceCategory[]::new);
    }

    enum FileType {
        JSON, TXT;

        @Override
        public String toString() {
            return "." + name().toLowerCase();
        }
    }
}
