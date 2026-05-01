package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    RC_NA, // special resource code indicating N/A

    // Icons
    // TODO

    // Tooltips
    RC_INTERVAL_PROGRESS, // TODO - temporary

    // UI text
    RC_ABOUT, // also: tooltip
    RC_COPYRIGHT,
    RC_NEW_PROJECT, // also: tooltip
    RC_NO_PROJECTS_OPEN,
    RC_OPEN_PROJECT, // also: tooltip
    RC_PROGRAM_SETTINGS, // also: tooltip
    RC_QUIT,
    RC_START,

    // Values
    // TODO - variable output possibilities - make language-dependent _values json files
    RC_MEASURING_ACCEPTED,
    RC_MEASURING_ATTEMPTED,
    RC_UNKNOWN,
    ;

    private final static String prefix = "RC_";

    public String id() {
        return EnumUtils.formattedNameNoPrefix(this, prefix);
    }

    public ResourceValue asValue() {
        return ResourceValue.ofRC(this);
    }

    @Override
    public String toString() {
        return id();
    }
}
