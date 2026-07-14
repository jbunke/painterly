package com.jordanbunke.painterly.theme;

public enum ThemeEnum {
    DEFAULT(DefaultTheme.get()),
    // TODO - CUSTOM()
    ;

    final Theme theme;

    ThemeEnum(final Theme theme) {
        this.theme = theme;
    }

    public String id() {
        return name().toLowerCase();
    }

    public static ThemeEnum fromID(final String id) {
        try {
            return ThemeEnum.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return DEFAULT;
        }
    }
}
