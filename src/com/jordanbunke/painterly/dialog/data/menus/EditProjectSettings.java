package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.menu.elements.complex.project_bar.ProjectBar;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.ProjectUtils;

public final class EditProjectSettings extends ProjectVariables {
    private static final EditProjectSettings INSTANCE;

    static {
        INSTANCE = new EditProjectSettings();
    }

    private EditProjectSettings() {
        super(ResourceCode.RC_EPS_VALIDATED_FOLDER);
    }

    public static EditProjectSettings get() {
        return INSTANCE;
    }

    @Override
    void whenReady() {
        ProjectUtils.doIfHasProject(p -> {
            p.setName(name.get());
            p.setFolder(folder.get());
            p.saveManager.setAutosave(autosave.get());
            p.saveManager.setAutosaveFrequency(autosaveFrequency.get());
            ProjectBar.regen();
        }).run();
    }
}
