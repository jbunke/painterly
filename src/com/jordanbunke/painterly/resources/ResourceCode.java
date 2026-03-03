package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    RC_NO_TOOLTIP;

    private final static String prefix = "RC_";

    public String id() {
        return EnumUtils.formattedNameNoPrefix(this, prefix);
    }

    @Override
    public String toString() {
        return id();
    }
}
