package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.menu.elements.complex.project_bar.ProjectBar;
import com.jordanbunke.painterly.util.ProjectUtils;

public final class SaveAs extends ProjectVariables {
    private static final SaveAs INSTANCE;

    static {
        INSTANCE = new SaveAs();
    }

    private SaveAs() {
        super();
    }

    public static SaveAs get() {
        return INSTANCE;
    }

    @Override
    void whenReady() {
        ProjectUtils.doIfHasProject(p -> {
            p.setName(name.get());
            p.setFolder(folder.get());
            p.saveManager.setAutosave(autosave.get());
            p.saveManager.setAutosaveFrequency(autosaveFrequency.get());
            p.saveManager.save();
            ProjectBar.regen();
        }).run();
    }
}
