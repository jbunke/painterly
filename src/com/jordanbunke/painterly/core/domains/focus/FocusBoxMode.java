package com.jordanbunke.painterly.core.domains.focus;

import static com.jordanbunke.painterly.util.StringUtils.*;

public enum FocusBoxMode {
    FREE, FORWARDS, BACKWARDS, RANDOM, WORST, PRIORITIZE_WORST;

    public String formattedName() {
        return capitalizeFirstLetter(nameFromID(name()).toLowerCase());
    }

    // TODO
}
