package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Constants;

import java.nio.file.Path;
import java.util.Set;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

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
        name = new DialogVariable<>("", this::validName);
        folder = new DialogVariable<>();
        ref = new DialogVariable<>();
        scaleFactor = new DialogVariable<>(10, this::validScaleFactor);
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

    private Pair<Boolean, ResourceCode> validName(
            final String name
    ) {
        if (name == null || name.isEmpty())
            return new Pair<>(false, RC_DIALOG_CANNOT_BE_EMPTY);
        else if (name.trim().isEmpty())
            return new Pair<>(false, RC_DIALOG_CANNOT_BE_ONLY_WHITESPACE);
        else if (nameContainsIllegalChar(name))
            return new Pair<>(false, RC_DIALOG_CONTAINS_INVALID_CHARACTER);

        return new Pair<>(true, RC_NA);
    }

    private Pair<Boolean, ResourceCode> validScaleFactor(
            final Integer scaleFactor
    ) {
        if (scaleFactor == null)
            return new Pair<>(false, RC_DIALOG_CANNOT_READ_INT);
        else if (scaleFactor < 1)
            return new Pair<>(false, RC_DIALOG_MUST_BE_POSITIVE);
        else if (!ref.passing())
            return new Pair<>(false,
                    RC_DIALOG_CANNOT_VALIDATE_SCALE_FACTOR_WITHOUT_IMAGE);
        else {
            final GameImage refImage = ref.get();
            final long pixels = (long) refImage.getWidth() * refImage.getHeight();

            if (pixels > Constants.MAX_CANVAS_PIXELS)
                return new Pair<>(false, RC_NA /* TODO */);

            return new Pair<>(true, RC_NA /* TODO */);
        }
    }

    // helper

    // TODO - if reused, move to utility class
    private static boolean nameContainsIllegalChar(final String name) {
        final Set<Character> ILLEGAL_CHAR_SET = Set.of(
                '/', '\\', ':', '*', '?', '"', '<', '>', '|', '{', '}');

        return ILLEGAL_CHAR_SET.stream()
                .map(c -> name.indexOf(c) >= 0)
                .reduce((a, b) -> a || b).orElse(false);
    }
}
