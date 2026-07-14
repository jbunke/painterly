package com.jordanbunke.painterly.util;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ProjectUtils {
    public static <T> Supplier<T> wrapGetter(
            final Function<Project, T> f, final T noActiveProjectCase
    ) {
        return () -> {
            final Project p = ProjectManager.get().getProject();

            if (p != null)
                return f.apply(p);

            return noActiveProjectCase;
        };
    }

    public static <T> Consumer<T> wrapSetter(
            final BiConsumer<Project, T> f
    ) {
        return t -> {
            final Project p = ProjectManager.get().getProject();

            if (p != null)
                f.accept(p, t);
        };
    }

    public static Runnable doIfHasProject(
            final Consumer<Project> f
    ) {
        return () -> {
            final Project p = ProjectManager.get().getProject();

            if (p != null)
                f.accept(p);
        };
    }
}
