package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.domains.interval.ProgressManager;
import com.jordanbunke.painterly.dialog.data.menus.*;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.Layout;
import com.jordanbunke.painterly.util.ProjectUtils;
import com.jordanbunke.painterly.util.debug.LogManager;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.resources.StringVariableMap.ID.*;
import static com.jordanbunke.painterly.util.debug.LogChannel.*;

public enum ResourceVariables {
    RV_ACCEPTED_OR_ATTEMPTED(p -> (p.strokeManager.isTickMode()
            ? RC_MEASURING_ATTEMPTED : RC_MEASURING_ACCEPTED).asValue(),
            RC_UNKNOWN.asValue()),
    RV_CHANNEL_STATUS_ALL(() -> (LogManager.isGlobalOff()
            ? RC_ENABLED : RC_DISABLED).asValue()),
    RV_CHANNEL_STATUS_FPS(() -> (LogManager.isChannelActive(FPS)
            ? RC_OFF : RC_ON).asValue()),
    RV_CHANNEL_STATUS_RS(() -> (LogManager.isChannelActive(RECENT_STROKES)
            ? RC_OFF : RC_ON).asValue()),
    RV_CURRENT_TOOL(() -> ResourceValue.ofString(
            LanguageData.retrieveUIText(ToolManager.getCurrentAction().getCode()))),
    RV_DIVS_X(p -> ResourceValue.ofString(
            String.valueOf(p.focusManager.getDivsX())),
            RC_UNKNOWABLE.asValue()),
    RV_DIVS_Y(p -> ResourceValue.ofString(
            String.valueOf(p.focusManager.getDivsY())),
            RC_UNKNOWABLE.asValue()),
    RV_FILE_PAINTING(() -> ResourceValue.ofString(
            Constants.PAINTING_FILENAME)),
    RV_FILE_SOURCE(() -> ResourceValue.ofString(
            Constants.SOURCE_FILENAME)),
    RV_FILE_SPEC(() -> ResourceValue.ofString(
            Constants.SPEC_FILENAME)),
    RV_FILENAME(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(FILENAME))),
    RV_FOCUS_BOX_MODE(p -> ResourceValue.ofString(
            p.focusManager.getFocusBoxMode().formattedName()),
            RC_UNKNOWABLE.asValue()),
    RV_FORMATTED_SIM(p -> ResourceValue.ofString(
            p.progressManager.formattedSimilarity()),
            RC_UNKNOWABLE.asValue()),
    RV_FULLSCREEN_ACTION(() -> (Layout.isFullscreen()
            ? RC_EXIT_FULLSCREEN : RC_FULLSCREEN).asValue()),
    RV_INTERVAL_COMPLETED(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(INTERVAL_COMPLETED))),
    RV_INTERVAL_PERC(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(INTERVAL_PERC))),
    RV_INTERVAL_PROGRESS(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getIntervalProgress())),
            RC_UNKNOWABLE.asValue()),
    RV_INTERVAL_TARGET(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getIntervalTarget())),
            RC_UNKNOWABLE.asValue()),
    RV_INTERVAL_TOTAL(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(INTERVAL_TOTAL))),
    RV_MAX_DIVS_ABOVE(() -> ResourceValue.ofString(
            FocusAreaAsFocusBox.get().raMaxDivsAbove())),
    RV_MAX_DIVS_BELOW(() -> ResourceValue.ofString(
            FocusAreaAsFocusBox.get().raMaxDivsBelow())),
    RV_MAX_DIVS_LEFT(() -> ResourceValue.ofString(
            FocusAreaAsFocusBox.get().raMaxDivsLeft())),
    RV_MAX_DIVS_RIGHT(() -> ResourceValue.ofString(
            FocusAreaAsFocusBox.get().raMaxDivsRight())),
    RV_PROGRAM_NAME(() -> ResourceValue.ofString(ProgramInfo.PROGRAM_NAME)),
    RV_PROJECT_NAME(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(PROJECT_NAME))),
    RV_PW_DIVISIONS(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(PW_DIVISIONS))),
    RV_PW_RANK(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(PW_RANK))),
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
    RV_PV_FOLDER(() ->
            ResourceValue.ofString(ProjectVariables.raFolder())),
    RV_SIM_ACTION_STATUS(p -> (p.isPainting()
            ? RC_SIM_PAUSE : RC_SIM_RESUME).asValue(),
            RC_SIM_RESUME.asValue()),
    RV_SIM_SCOPE(p -> (p.progressManager.isDisplay() == ProgressManager.FOCUS
            ? RC_SCOPE_FOCUS_AREA : RC_SCOPE_GLOBAL).asValue(),
            RC_UNKNOWABLE.asValue()),
    RV_STROKES_ATTEMPTED(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getStrokesAttempted())),
            RC_UNKNOWABLE.asValue()),
    RV_STROKES_COMPLETED(p -> ResourceValue.ofString(
            String.valueOf(p.strokeManager.getStrokesCompleted())),
            RC_UNKNOWABLE.asValue()),
    RV_WORST_PERC(() -> ResourceValue.ofString(
            StringVariableMap.retrieve(WORST_PERC))),
    ;

    final Supplier<ResourceValue> valueGetter;

    ResourceVariables(final Supplier<ResourceValue> valueGetter) {
        this.valueGetter = valueGetter;
    }

    ResourceVariables(
            final Function<Project, ResourceValue> f,
            final ResourceValue noActiveProjectCase
    ) {
        valueGetter = ProjectUtils.wrapGetter(f, noActiveProjectCase);
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
