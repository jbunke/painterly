package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.util.EnumUtils;

public enum ResourceCode {
    RC_NA, // special resource code indicating N/A

    // Icons
    // TODO

    // Tooltips
    RC_INTERVAL_PROGRESS, // TODO - temporary

    // UI text
    RC_ABOUT, // also: tooltip
    RC_COPYRIGHT,
    RC_DIALOG_CANCEL,
    RC_DIALOG_CANNOT_BE_EMPTY,
    RC_DIALOG_CANNOT_BE_ONLY_WHITESPACE,
    RC_DIALOG_CANNOT_READ_INT,
    RC_DIALOG_CANNOT_VALIDATE_SCALE_FACTOR_WITHOUT_IMAGE,
    RC_DIALOG_CLOSE,
    RC_DIALOG_CONTAINS_INVALID_CHARACTER,
    RC_DIALOG_DEFAULT_OK,
    RC_DIALOG_MUST_BE_POSITIVE,
    RC_DIALOG_VARIABLE_CANNOT_BE_NULL,
    // Loading messages
    RC_LOAD_INIT_PROJECT,
    RC_LOAD_UPDATE_STATS,
    // Navigation
    RC_NAV_PROGRAM,
    RC_NAV_MAIN_MENU,
    RC_NAV_QUIT_PROGRAM,
    RC_NAV_PROJECT,
    RC_NAV_SAVE_AS,
    RC_NAV_VIEW,
    /* TODO */
    // End navigation
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
    RC_RESET_POS,
    RC_START,
    RC_UPLOAD,
    RC_TOGGLE_FULLSCREEN,
    RC_TOGGLE_SIM, // TODO - UI text
    RC_TOGGLE_SOURCE,

    // Values
    // TODO - variable output possibilities - make language-dependent _values json files
    RC_MEASURING_ACCEPTED,
    RC_MEASURING_ATTEMPTED,
    RC_EXIT_FULLSCREEN,
    RC_FULLSCREEN,
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
