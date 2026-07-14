package com.jordanbunke.painterly.resources;

import java.util.Arrays;

public enum ResourceCategory {
    TOOLTIP, UI_TEXT, VALUE,
    CHANGELOG(FileType.MARKDOWN),
    ROADMAP(FileType.MARKDOWN),
    LICENSE(FileType.MARKDOWN);
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
        final String filename = name();

        return switch (this) {
            case LICENSE -> filename;
            case TOOLTIP, VALUE -> filename.toLowerCase() + "s";
            default -> filename.toLowerCase();
        } + fileType;
    }

    public static ResourceCategory[] jsonCategories() {
        return Arrays.stream(values())
                .filter(c -> c.fileType == FileType.JSON)
                .toArray(ResourceCategory[]::new);
    }

    enum FileType {
        JSON, MARKDOWN;

        @Override
        public String toString() {
            if (this == MARKDOWN)
                return ".md";

            return "." + name().toLowerCase();
        }
    }
}
