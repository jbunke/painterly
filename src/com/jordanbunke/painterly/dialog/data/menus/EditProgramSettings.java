package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.VariableUIAssembler;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.tool.DrawFocusArea;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.settings.Settings.SettingID.*;

public final class EditProgramSettings extends DialogVariableSet {
    private static final EditProgramSettings INSTANCE;

    private final DialogVariable<Boolean> autosaveOBD, drawDFAReticle;
    private final DialogVariable<Integer> defIntervalTarget;

    static {
        INSTANCE = new EditProgramSettings();
    }

    private EditProgramSettings() {
        autosaveOBD = new DialogVariable<>(
                () -> Settings.get(SET_ID_AUTOSAVE_ON_BY_DEFAULT, Boolean.class),
                Validator::always,
                VariableUIAssembler.assembleCheckbox(
                        RC_DIALOG_TX_AUTOSAVE_OBD));
        drawDFAReticle = new DialogVariable<>(
                () -> Settings.get(SET_ID_DRAW_DFA_RETICLE, Boolean.class),
                Validator::always,
                VariableUIAssembler.assembleCheckbox(
                        RC_DIALOG_TX_DRAW_DFA_OVERLAY));
        defIntervalTarget = new DialogVariable<>(
                () -> Settings.get(SET_ID_DEFAULT_INTERVAL_TARGET, Integer.class),
                Validator::validIntervalTarget,
                VariableUIAssembler.assembleDefIntervalTargetTextbox());
        // TODO - additional settings: language, theme
    }

    public static EditProgramSettings get() {
        return INSTANCE;
    }

    @Override
    public DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                autosaveOBD, drawDFAReticle, defIntervalTarget
        };
    }

    @Override
    void whenReady() {
        Settings.set(SET_ID_AUTOSAVE_ON_BY_DEFAULT, autosaveOBD.get());
        DrawFocusArea.get().updateDrawReticle(drawDFAReticle.get());
        Settings.set(SET_ID_DEFAULT_INTERVAL_TARGET, defIntervalTarget.get());
    }
}
