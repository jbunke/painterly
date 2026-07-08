package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Constants;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_AUTOSAVE_ON_BY_DEFAULT;

public final class NewProject extends DialogVariableSet {
    private static final NewProject INSTANCE;

    public final DialogVariable<String> name;
    public final DialogVariable<Path> folder;
    public final DialogVariable<GameImage> sourceImage;
    public final DialogVariable<Double> scaleFactor;
    public final DialogVariable<Boolean> autosave;
    public final DialogVariable<Integer> autosaveFrequency;

    private String refImageFilename;

    static {
        INSTANCE = new NewProject();
    }

    private NewProject() {
        name = new DialogVariable<>(() -> "", Validator::validName);
        folder = new DialogVariable<>(() -> null, false,
                path -> Validator.validFolder(path,
                        RC_DIALOG_FB_NPD_VALIDATED_FOLDER));
        sourceImage = new DialogVariable<>(() -> null, this::validSourceImage);
        scaleFactor = new DialogVariable<>(() -> 1d, this::validScaleFactor);
        autosave = new DialogVariable<>(
                () -> Settings.get(SET_ID_AUTOSAVE_ON_BY_DEFAULT, Boolean.class),
                Validator::always);
        autosaveFrequency = new DialogVariable<>(
                () -> Constants.DEF_AUTOSAVE_FREQUENCY,
                Validator::validAutosaveFrequency);
    }

    public static NewProject get() {
        return INSTANCE;
    }

    @Override
    DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                name, folder, sourceImage, scaleFactor,
                autosave, autosaveFrequency
        };
    }

    @Override
    protected void executionApparatus(final Runnable whenReady) {
        ProgramState.load(whenReady, RC_LOAD_INIT_PROJECT);
    }

    @Override
    void whenReady() {
        final Project project = new Project.Builder(name.get(),
                folder.get(), sourceImage.get())
                .setScaleFactor(scaleFactor.get(), true)
                .setAutosave(autosave.get())
                .setAutosaveFrequency(autosaveFrequency.get())
                .build();
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

    private Pair<Boolean, ResourceCode> validSourceImage(
            final GameImage sourceImage
    ) {
        if (sourceImage == null)
            return new Pair<>(false, RC_DIALOG_FB_VARIABLE_CANNOT_BE_NULL);

        return new Pair<>(true, RC_DIALOG_FB_NPD_VALIDATED_SRC_IMAGE);
    }

    private Pair<Boolean, ResourceCode> validScaleFactor(
            final Double scaleFactor
    ) {
        if (scaleFactor == null)
            return new Pair<>(false, RC_DIALOG_FB_CANNOT_READ_DOUBLE);
        else if (scaleFactor < 1d)
            return new Pair<>(false, RC_DIALOG_FB_MUST_BE_GR_EQ_1);
        else if (!sourceImage.passing())
            return new Pair<>(false,
                    RC_DIALOG_FB_CANNOT_VALIDATE_SCALE_FACTOR_WITHOUT_IMAGE);
        else {
            final GameImage refImage = sourceImage.get();
            final int w = (int)(refImage.getWidth() * scaleFactor),
                    h = (int)(refImage.getHeight() * scaleFactor);
            final long pixels = (long) w * h;

            if (pixels > Constants.MAX_CANVAS_PIXELS)
                return new Pair<>(false, RC_NA /* TODO */);

            return new Pair<>(true, RC_DIALOG_FB_NPD_VALIDATED_SCALE_FACTOR);
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
        return String.valueOf((int)(sourceImage.get().getWidth() * scaleFactor.get()));
    }

    public String raHeight() {
        return String.valueOf((int)(sourceImage.get().getHeight() * scaleFactor.get()));
    }
}
