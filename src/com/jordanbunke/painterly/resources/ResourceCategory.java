package com.jordanbunke.painterly.resources;

public enum ResourceCategory {
    TOOLTIP;
    // TODO - add categories

    public String suffix() {
        return "_" + name().toLowerCase() + "s";
    }
}
