package com.jordanbunke.painterly.core.domains.focus;

import com.jordanbunke.painterly.core.Project;

public final class FocusManager {
    private final Project project;

    private FocusBoxMode focusBoxMode;
    private Scope scope;
    private IFocusArea focusArea;
    private int boxDivisionsX, boxDivisionsY;

    public FocusManager(final Project project) {
        this.project = project;
    }

    public void tryUpdateBox() {
        // TODO
    }
}
