package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Constants;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class NewProject extends DialogVariableSet {
    private static final NewProject INSTANCE;

    public final DialogVariable<String> name;
    public final DialogVariable<Path> folder;
    public final DialogVariable<GameImage> sourceImage;
    public final DialogVariable<Integer> scaleFactor;

    private String refImageFilename;

    static {
        INSTANCE = new NewProject();
    }

    private NewProject() {
        name = new DialogVariable<>("", this::validName);
        folder = new DialogVariable<>(null, this::validFolder);
        sourceImage = new DialogVariable<>(null, this::validSourceImage);
        scaleFactor = new DialogVariable<>(10, this::validScaleFactor);
    }

    public static NewProject get() {
        return INSTANCE;
    }

    @Override
    DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                name, folder, sourceImage, scaleFactor
        };
    }

    @Override
    void whenReady() {
        final Project project = new Project(name.get(),
                folder.get(), sourceImage.get(), scaleFactor.get());
        ProjectManager.get().addProject(project, true);
    }

    // logic

    public void chooseFolder() {
        FileIO.setDialogToFoldersOnly();
        final Optional<File> opened = FileIO.openFileFromSystem();

        if (opened.isEmpty())
            return;

        folder.set(opened.get().toPath());
    }

    public void uploadSourceImage() {
        FileIO.setDialogToFilesOnly();

        final String fileTypeDescription =
                LanguageData.retrieveUIText(RC_OFD_ACCEPTED_RASTER_TYPES);
        FileIO.openFileFromSystem(
                new String[] { fileTypeDescription },
                new String[][] { Constants.RASTER_FORMATS }
        ).ifPresent(this::processSourceImageFile);
    }

    private void processSourceImageFile(final File file) {
        final GameImage image = GameImageIO.readImage(file.toPath());
        sourceImage.set(image);
        refImageFilename = file.getName();
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

    private Pair<Boolean, ResourceCode> validFolder(
            final Path folder
    ) {
        if (folder == null)
            return new Pair<>(false, RC_DIALOG_VARIABLE_CANNOT_BE_NULL);

        return new Pair<>(true, RC_NPD_VALIDATED_FOLDER);
    }

    private Pair<Boolean, ResourceCode> validSourceImage(
            final GameImage sourceImage
    ) {
        if (sourceImage == null)
            return new Pair<>(false, RC_DIALOG_VARIABLE_CANNOT_BE_NULL);

        return new Pair<>(true, RC_NPD_VALIDATED_SRC_IMAGE);
    }

    private Pair<Boolean, ResourceCode> validScaleFactor(
            final Integer scaleFactor
    ) {
        if (scaleFactor == null)
            return new Pair<>(false, RC_DIALOG_CANNOT_READ_INT);
        else if (scaleFactor < 1)
            return new Pair<>(false, RC_DIALOG_MUST_BE_POSITIVE);
        else if (!sourceImage.passing())
            return new Pair<>(false,
                    RC_DIALOG_CANNOT_VALIDATE_SCALE_FACTOR_WITHOUT_IMAGE);
        else {
            final GameImage refImage = sourceImage.get();
            final long pixels = (long) refImage.getWidth() *
                    refImage.getHeight() * scaleFactor * scaleFactor;

            if (pixels > Constants.MAX_CANVAS_PIXELS)
                return new Pair<>(false, RC_NA /* TODO */);

            return new Pair<>(true, RC_NPD_VALIDATED_SCALE_FACTOR);
        }
    }

    // resource variable accessors

    public String raFolder() {
        return String.valueOf(folder.get());
    }

    public String raSourceImageName() {
        return refImageFilename;
    }

    public String raSourceImageWidth() {
        return String.valueOf(sourceImage.get().getWidth());
    }

    public String raSourceImageHeight() {
        return String.valueOf(sourceImage.get().getHeight());
    }

    public String raWidth() {
        return String.valueOf(sourceImage.get().getWidth() * scaleFactor.get());
    }

    public String raHeight() {
        return String.valueOf(sourceImage.get().getHeight() * scaleFactor.get());
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
