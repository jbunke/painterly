package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    RC_NA, // special resource code indicating N/A

    // Icons
    RC_CHECKED_FALSE,
    RC_CHECKED_TRUE,
    RC_FEEDBACK_INVALID,
    RC_FEEDBACK_VALID,
    RC_DECREMENT,
    RC_INCREMENT,

    // Tooltips

    // UI text
    RC_ABOUT, // also: tooltip

    // Are you sure
    RC_AYS_DELETE_ACTIVE_BOUNDS_MESSAGE,
    RC_AYS_DELETE_ACTIVE_BOUNDS_TITLE,
    RC_AYS_QUIT_MESSAGE,
    RC_AYS_QUIT_TITLE,

    // Context bar
    RC_CB_CURRENT_TOOL,
    RC_CB_DIVS_X,
    RC_CB_DIVS_Y,
    RC_CB_FOCUS_BOX_MODE,
    RC_CB_INTERVAL_TARGET,
    RC_CB_INTERVAL_PROGRESS,
    RC_CB_SIMILARITY,
    RC_CB_STROKE_COUNT,

    RC_CLEAR_FOCUS_BOXES,
    RC_COPYRIGHT,
    RC_DELETE_ACTIVE_BOUNDS,
    RC_DIALOG_CANCEL,
    RC_DIALOG_CANNOT_BE_EMPTY,
    RC_DIALOG_CANNOT_BE_ONLY_WHITESPACE,
    RC_DIALOG_CANNOT_READ_INT,
    RC_DIALOG_CANNOT_VALIDATE_SCALE_FACTOR_WITHOUT_IMAGE,
    RC_DIALOG_CLOSE,
    RC_DIALOG_CONTAINS_INVALID_CHARACTER,
    RC_DIALOG_DEFAULT_OK,
    RC_DIALOG_MUST_BE_GR_EQ_1,
    RC_DIALOG_VARIABLE_CANNOT_BE_NULL,
    RC_DISPLAY_FOCUS,
    RC_DISPLAY_GLOBAL,

    // Focus box modes
    RC_FB_BACKWARDS,
    RC_FB_FREE,
    RC_FB_FORWARDS,
    RC_FB_PRIORITIZE_WORST,
    RC_FB_RANDOM,
    RC_FB_WORST,

    RC_FOCUS_BOX_AS_FOCUS_AREA, // also: tooltip?

    // Loading messages
    RC_LOAD_INIT_PROJECT,
    RC_LOAD_UPDATE_STATS,

    // Navigation
    RC_NAV_EDIT,
    RC_NAV_FOCUS_AREA,
    RC_NAV_MAIN_MENU,
    RC_NAV_PROGRAM,
    RC_NAV_PROJECT,
    RC_NAV_QUIT_PROGRAM,
    RC_NAV_SAVE_AS,
    RC_NAV_VIEW,
    /* TODO */

    RC_NEW_PROJECT, // also: tooltip,
    RC_NO_PROJECTS_OPEN,
    RC_NPD_CHOOSE_FOLDER,
    RC_NPD_FOLDER,
    RC_NPD_PROJECT_NAME,
    RC_NPD_SOURCE_IMAGE,
    RC_NPD_SCALE_FACTOR,
    RC_NPD_VALIDATED_FOLDER,
    RC_NPD_VALIDATED_SRC_IMAGE,
    RC_NPD_VALIDATED_SCALE_FACTOR,
    RC_OFD_ACCEPTED_RASTER_TYPES,
    RC_OPEN_PROJECT, // also: tooltip,
    RC_PROGRAM_SETTINGS, // also: tooltip
    RC_QUIT,
    RC_RESET_FOCUS_AREA, // TODO - also: tooltip?
    RC_RESET_POS,
    RC_START,
    RC_UPLOAD,
    RC_TICK_MODE_ATTEMPTED, // TODO - also: tooltip
    RC_TICK_MODE_COMPLETED, // TODO - also: tooltip
    RC_TOGGLE_FULLSCREEN,
    RC_TOGGLE_SIM, // TODO - UI text
    RC_TOGGLE_TICK_MODE, // TODO - also: tooltip
    RC_TOGGLE_SOURCE,

    // Tools
    RC_TOOL_DRAW_FOCUS_AREA,
    RC_TOOL_HAND,
    RC_TOOL_MOVE_FOCUS_AREA,
    RC_TOOL_ZOOM,

    // Values
    // TODO - variable output possibilities - make language-dependent _values json files
    RC_MEASURING_ACCEPTED,
    RC_MEASURING_ATTEMPTED,
    RC_EXIT_FULLSCREEN,
    RC_FULLSCREEN,
    RC_SCOPE_FOCUS_AREA,
    RC_SCOPE_GLOBAL,
    RC_UNKNOWABLE,
    RC_UNKNOWN,
    ;

    private final static String prefix = "RC_";

    public String id() {
        return EnumUtils.formattedNameNoPrefix(this, prefix);
    }

    public ResourceValue asValue() {
        return ResourceValue.ofRC(this);
    }

    @Override
    public String toString() {
        return id();
    }
}
