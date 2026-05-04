package com.jordanbunke.painterly.core;

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
        if (index >= 0 && index < projects.size()) {
            this.index = index;
            return true;
        }

        return false;
    }

    // TODO - close project, shift remaining, update index if necessary

    public void addProject(final Project project, final boolean setActive) {
        projects.add(project);

        if (setActive)
            index = projects.size() - 1;
    }
}
