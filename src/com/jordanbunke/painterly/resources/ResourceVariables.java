package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.util.EnumUtils;

import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ResourceVariables {
    RV_ACCEPTED_OR_ATTEMPTED(() -> {
        final Project p = ProjectManager.get().getProject();

        if (p != null)
            return (p.getStrokeManager().isTickMode()
                    ? RC_MEASURING_ATTEMPTED : RC_MEASURING_ACCEPTED).asValue();

        return RC_UNKNOWN.asValue();
    }),
    RV_PROGRAM_NAME(() -> ResourceValue.ofString(ProgramInfo.PROGRAM_NAME)),
    RV_NPD_FOLDER(() ->
            ResourceValue.ofString(NewProject.get().prospectiveFolder())),
    RV_NPD_REF_NAME(() ->
            ResourceValue.ofString(NewProject.get().prospectiveRefName())),
    RV_NPD_REF_W(() ->
            ResourceValue.ofString(NewProject.get().prospectiveRefWidth())),
    RV_NPD_REF_H(() ->
            ResourceValue.ofString(NewProject.get().prospectiveRefHeight())),
    RV_NPD_W(() ->
            ResourceValue.ofString(NewProject.get().prospectiveWidth())),
    RV_NPD_H(() ->
            ResourceValue.ofString(NewProject.get().prospectiveHeight())),
    ;

    final Supplier<ResourceValue> valueGetter;

    ResourceVariables(final Supplier<ResourceValue> valueGetter) {
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
                        rv.valueGetter.get().retrieve());
            }
        }

        return parsing;
    }
}
