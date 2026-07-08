package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.ProjectUtils;

public final class DuplicateProject extends ProjectVariables {
    private static final DuplicateProject INSTANCE;

    static {
        INSTANCE = new DuplicateProject();
    }

    private DuplicateProject() {
        super(ResourceCode.RC_DP_VALIDATED_FOLDER);
    }

    public static DuplicateProject get() {
        return INSTANCE;
    }

    @Override
    void whenReady() {
        ProjectUtils.doIfHasProject(p -> {
            final Project project = new Project.Builder(name.get(),
                    folder.get(), p.getSourceImage())
                    .setScaleFactor(p.scaleFactor)
                    .setWidth(p.width).setHeight(p.height)
                    .setAutosave(autosave.get())
                    .setAutosaveFrequency(autosaveFrequency.get())
                    .build();
            ProjectManager.get().addProject(project, true);
        }).run();
    }
}
