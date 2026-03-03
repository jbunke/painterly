package com.jordanbunke.painterly.resources.lang;

import com.jordanbunke.painterly.util.StringUtils;

public enum Language {
    ENGLISH, GERMAN, BRAZILIAN_PORTUGUESE, SPANISH;

    public String code() {
        return switch (this) {
            case ENGLISH -> "en";
            case GERMAN -> "de";
            case BRAZILIAN_PORTUGUESE -> "pt-br";
            case SPANISH -> "es";
        };
    }

    public String formattedName() {
        return StringUtils.nameFromID(name());
    }

    public static Language fromCode(final String code) {
        for (Language l : Language.values())
            if (l.code().equals(code))
                return l;

        return null;
    }

    public static boolean validCode(final String code) {
        return fromCode(code) != null;
    }
}
