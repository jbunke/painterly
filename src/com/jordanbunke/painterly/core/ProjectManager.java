package com.jordanbunke.painterly.core;

import com.jordanbunke.painterly.menu.elements.complex.project_bar.ProjectBar;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.util.Constants;

import java.util.LinkedList;
import java.util.List;

public final class ProjectManager {
    private static final int NONE = -1;

    private static final ProjectManager INSTANCE;

    private final List<Project> projects;

    private int index;

    static {
        INSTANCE = new ProjectManager();
    }

    private ProjectManager() {
        projects = new LinkedList<>();
        index = NONE;
    }

    public static ProjectManager get() {
        return INSTANCE;
    }

    public Project getProject() {
        return hasProject() ? projects.get(index) : null;
    }

    public boolean hasProject() {
        return index != NONE && index < projects.size();
    }

    // TODO - resort projects due to drag

    public boolean setActiveProject(final int index) {
        if (isValidIndex(index) && this.index != index) {
            this.index = index;
            disableAll();
            ToolManager.getCurrentTool().deselect();
            return true;
        }

        return false;
    }

    public void closeProject(final int index) {
        if (!isValidIndex(index))
            return;

        projects.remove(index);

        if (index < this.index)
            this.index--;

        if (this.index >= projects.size())
            this.index = projects.size() - 1;

        ProjectBar.regen();
    }

    public void addProject(final Project project, final boolean setActive) {
        if (!canAddProject())
            return;

        projects.add(project);

        if (setActive)
            index = projects.size() - 1;

        ProjectBar.regen();
    }

    public int getIndex() {
        return index;
    }

    public int getNumberOfProjects() {
        return projects.size();
    }

    public Project getProjectAt(final int index) {
        return projects.get(index);
    }

    public boolean isValidIndex(final int index) {
        return index >= 0 && index < projects.size();
    }

    public boolean canAddProject() {
        return projects.size() < Constants.MAX_PROJECTS_ALLOWED;
    }

    private void disableAll() {
        for (Project project : projects)
            project.disable();
    }
}
