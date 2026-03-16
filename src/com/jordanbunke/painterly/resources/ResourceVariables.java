package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.EnumUtils;

import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ResourceVariables {
    RV_ACCEPTED_OR_ATTEMPTED(() -> {
        final Project p = ProjectManager.get().getProject();

        if (p != null)
            return p.isTickMode()
                    ? RC_MEASURING_ATTEMPTED : RC_MEASURING_ACCEPTED;

        return RC_UNKNOWN;
    }),
    ;

    final Supplier<ResourceCode> valueGetter;

    ResourceVariables(
            final Supplier<ResourceCode> valueGetter
    ) {
        this.valueGetter = valueGetter;
    }

    private final static String ENUM_PREFIX = "RV_", ID_PREFIX = "$";

    public String id() {
        return ID_PREFIX + EnumUtils.formattedNameNoPrefix(this, ENUM_PREFIX);
    }

    @Override
    public String toString() {
        return id();
    }

    public static String parse(final String unparsed) {
        String parsing = unparsed;

        if (unparsed.contains(ID_PREFIX)) {
            for (ResourceVariables rv : ResourceVariables.values()) {
                if (!parsing.contains(ID_PREFIX))
                    break;

                parsing = parsing.replace(rv.id(),
                        LanguageData.retrieveValue(rv.valueGetter.get()));
            }
        }

        return parsing;
    }
}
