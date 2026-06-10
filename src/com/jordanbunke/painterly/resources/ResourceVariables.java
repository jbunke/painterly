package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.Layout;

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
    RV_FULLSCREEN_ACTION(() -> (Layout.isFullscreen()
            ? RC_EXIT_FULLSCREEN : RC_FULLSCREEN).asValue()),
    RV_TOGGLE_SRC_ACTION(() -> {
        final Project p = ProjectManager.get().getProject();

        if (p != null)
            return (p.canvas.isShowSource()
                    ? RC_SHOW_CANVAS : RC_SHOW_SOURCE).asValue();

        return RC_SHOW_SOURCE.asValue();
    }),
    RV_PROGRAM_NAME(() -> ResourceValue.ofString(ProgramInfo.PROGRAM_NAME)),
    RV_NPD_FOLDER(() ->
            ResourceValue.ofString(NewProject.get().raFolder())),
    RV_NPD_SRC_NAME(() ->
            ResourceValue.ofString(NewProject.get().raSourceImageName())),
    RV_NPD_SRC_W(() ->
            ResourceValue.ofString(NewProject.get().raSourceImageWidth())),
    RV_NPD_SRC_H(() ->
            ResourceValue.ofString(NewProject.get().raSourceImageHeight())),
    RV_NPD_W(() ->
            ResourceValue.ofString(NewProject.get().raWidth())),
    RV_NPD_H(() ->
            ResourceValue.ofString(NewProject.get().raHeight())),
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
                if (!parsing.contains(rv.id()))
                    continue;

                parsing = parsing.replace(rv.id(),
                        rv.valueGetter.get().retrieve());
            }
        }

        return parsing;
    }
}
