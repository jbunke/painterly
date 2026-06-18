package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.Layout;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public enum ResourceVariables {
    RV_ACCEPTED_OR_ATTEMPTED(p -> (p.strokeManager.isTickMode()
            ? RC_MEASURING_ATTEMPTED : RC_MEASURING_ACCEPTED).asValue(),
            RC_UNKNOWN.asValue()),
    RV_FULLSCREEN_ACTION(() -> (Layout.isFullscreen()
            ? RC_EXIT_FULLSCREEN : RC_FULLSCREEN).asValue()),
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
    RV_STROKES_ATTEMPTED(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getStrokesAttempted())),
            RC_UNKNOWABLE.asValue()),
    RV_STROKES_COMPLETED(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getStrokesCompleted())),
            RC_UNKNOWABLE.asValue()),
    ;

    final Supplier<ResourceValue> valueGetter;

    ResourceVariables(final Supplier<ResourceValue> valueGetter) {
        this.valueGetter = valueGetter;
    }

    ResourceVariables(
            final Function<Project, ResourceValue> f,
            final ResourceValue noActiveProjectCase
    ) {
        valueGetter = () -> {
            final Project p = ProjectManager.get().getProject();

            if (p != null)
                return f.apply(p);

            return noActiveProjectCase;
        };
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
