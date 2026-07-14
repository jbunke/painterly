package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.VariableUIAssembler;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Constants;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static com.jordanbunke.painterly.resources.ResourceCode.RC_DIALOG_TX_AUTOSAVE;
import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_AUTOSAVE_ON_BY_DEFAULT;
import static com.jordanbunke.painterly.util.ProjectUtils.*;

public sealed abstract class ProjectVariables
        extends DialogVariableSet
        permits DuplicateProject, EditProjectSettings, SaveAs {
    public static final DialogVariable<String> name;
    public static final DialogVariable<Path> folder;
    public static final DialogVariable<Boolean> autosave;
    public static final DialogVariable<Integer> autosaveFrequency;

    static {
        name = new DialogVariable<>(wrapGetter(
                Project::getName, ""),
                Validator::validName,
                VariableUIAssembler.assembleProjectNameTextbox());
        folder = new DialogVariable<>(wrapGetter(
                Project::getFolder, null),
                path -> Validator.validFolder(path,
                        ResourceCode.RC_DIALOG_FB_PV_VALIDATED_FOLDER),
                VariableUIAssembler.assembleChooseFolderButton(
                        ProjectVariables::chooseFolder));
        autosave = new DialogVariable<>(wrapGetter(
                p -> p.saveManager.isAutosave(),
                Settings.get(SET_ID_AUTOSAVE_ON_BY_DEFAULT, Boolean.class)),
                Validator::always,
                VariableUIAssembler.assembleCheckbox(RC_DIALOG_TX_AUTOSAVE));
        autosaveFrequency = new DialogVariable<>(wrapGetter(
                p -> p.saveManager.getAutosaveFrequency(),
                Constants.DEF_AUTOSAVE_FREQUENCY),
                Validator::validAutosaveFrequency,
                VariableUIAssembler.assembleAutosaveFrequencyTextbox());
    }

    ProjectVariables() {}

    @Override
    public DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                name, folder, autosave, autosaveFrequency
        };
    }

    // logic

    private static void chooseFolder() {
        FileIO.setDialogToFoldersOnly();
        final Optional<File> opened = FileIO.openFileFromSystem();

        if (opened.isEmpty())
            return;

        folder.set(opened.get().toPath());
    }

    // resource variable accessors

    public static String raFolder() {
        return String.valueOf(folder.get());
    }
}
