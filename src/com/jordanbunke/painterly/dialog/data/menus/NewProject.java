package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.nio.file.Path;

public final class NewProject extends AllVarsInDialog {
    private static final NewProject INSTANCE;

    public final DialogVariable<String> name;
    public final DialogVariable<Path> folder;
    public final DialogVariable<GameImage> ref;
    public final DialogVariable<Integer> scaleFactor;

    static {
        INSTANCE = new NewProject();
    }

    private NewProject() {
        // TODO
        name = new DialogVariable<>("", NewProject::validName);
        folder = new DialogVariable<>();
        ref = new DialogVariable<>();
        scaleFactor = new DialogVariable<>(10, NewProject::validScaleFactor);
    }

    public static NewProject get() {
        return INSTANCE;
    }

    @Override
    DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                name, folder, ref, scaleFactor
        };
    }

    // validators

    private static Pair<Boolean, ResourceCode> validName(
            final String name
    ) {
        // TODO
        return Validator.never();
    }

    private static Pair<Boolean, ResourceCode> validScaleFactor(
            final int scaleFactor
    ) {
        // TODO
        return Validator.never();
    }
}
