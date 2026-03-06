package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    RC_NA, // special resource code indicating N/A

    // Icons
    // TODO

    // Tooltips
    RC_TEMP, // TODO - temporary

    // UI text
    RC_ABOUT, // also: tooltip
    RC_COPYRIGHT,
    RC_PROGRAM_SETTINGS, // also: tooltip
    RC_QUIT,
    RC_START,
    ;

    private final static String prefix = "RC_";

    public String id() {
        return EnumUtils.formattedNameNoPrefix(this, prefix);
    }

    @Override
    public String toString() {
        return id();
    }
}
