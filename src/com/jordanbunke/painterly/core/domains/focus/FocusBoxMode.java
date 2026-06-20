package com.jordanbunke.painterly.core.domains.focus;

import com.jordanbunke.painterly.events.actions.ProjectAction;

import static com.jordanbunke.painterly.events.actions.ProjectAction.*;
import static com.jordanbunke.painterly.util.StringUtils.*;

public enum FocusBoxMode {
    FREE(SET_FB_FREE),
    FORWARDS(SET_FB_FORWARDS),
    BACKWARDS(SET_FB_BACKWARDS),
    RANDOM(SET_FB_RANDOM),
    WORST(SET_FB_WORST),
    PRIORITIZE_WORST(SET_FB_PRIORITIZE_WORST);

    public final ProjectAction setter;

    FocusBoxMode(final ProjectAction setter) {
        this.setter = setter;
    }

    public String formattedName() {
        return capitalizeFirstLetter(nameFromID(name()).toLowerCase());
    }
}
