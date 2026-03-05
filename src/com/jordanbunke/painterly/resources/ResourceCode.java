package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    // Icons
    // TODO

    // Tooltips
    RC_NO_TOOLTIP,
    RC_TEMP, // TODO - temporary

    // UI text
    RC_ABOUT, // also: tooltip
    RC_COPYRIGHT,
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
