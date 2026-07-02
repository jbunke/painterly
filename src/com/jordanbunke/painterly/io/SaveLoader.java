package com.jordanbunke.painterly.io;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.FileIO;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.io.ZipEntryData;
import com.jordanbunke.delta_time.io.ZipWriter;
import com.jordanbunke.delta_time.scripting.util.PathHelper;
import com.jordanbunke.delta_time.utility.Version;
import com.jordanbunke.json.JSONBuilder;
import com.jordanbunke.json.JSONPair;
import com.jordanbunke.json.JSONReader;
import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Constants;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class SaveLoader {
    enum ID {
        VERSION, NAME, FOLDER,
        WIDTH, HEIGHT, SCALE_FACTOR,
        STROKES_COMPLETED, STROKES_ATTEMPTED,
        ;
    }

    private static final Set<Path> tempDirsForDeletion;

    static {
        tempDirsForDeletion = new HashSet<>();
        addShutdownHook();
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (final Path tempDir : tempDirsForDeletion)
                FileIO.deleteDirRecursive(tempDir);
        }));
    }

    public static void saveProject(final Project project) {
        final String specContents = buildProjectSpecFile(project);
        final GameImage source = project.getSourceImage(),
                painting = project.canvas.getPainting();

        final ZipEntryData<String> specFile = new ZipEntryData<>(
                specContents, Constants.SPEC_FILENAME, ZipWriter::forString);
        final ZipEntryData<GameImage> sourceFile = new ZipEntryData<>(source,
                Constants.SOURCE_FILENAME, ZipWriter::forImage),
                paintingFile = new ZipEntryData<>(painting,
                        Constants.PAINTING_FILENAME, ZipWriter::forImage);

        final Path zipPath = project.getFolder()
                .resolve(project.getName() + ".zip");

        try {
            FileIO.writeZip(zipPath, specFile, sourceFile, paintingFile);
        } catch (IOException ignored) {
            // TODO
        }
    }

    private static String buildProjectSpecFile(final Project project) {
        final JSONBuilder jb = new JSONBuilder();

        for (ID id : ID.values()) {
            final String key = id.name().toLowerCase();
            final Object value = switch (id) {
                case VERSION -> String.valueOf(ProgramInfo.getVersion());
                case NAME -> project.getName();
                case FOLDER -> String.valueOf(project.getFolder().toAbsolutePath());
                case WIDTH -> project.width;
                case HEIGHT -> project.height;
                case SCALE_FACTOR -> project.scaleFactor;
                case STROKES_COMPLETED -> project.strokeManager.getStrokesCompleted();
                case STROKES_ATTEMPTED -> project.strokeManager.getStrokesAttempted();
            };
            jb.add(new JSONPair(key, value));
        }

        return jb.write();
    }

    public static void openProject() {
        FileIO.setDialogToFilesOnly();

        final String description =
                LanguageData.retrieveUIText(RC_PROJECT_ZIP_DESCRIPTION);

        FileIO.openFileFromSystem(
                new String[] { description },
                new String[][] { { "zip" } }
        ).ifPresent(SaveLoader::loadProjectFromFile);
        ProgramState.scheduleKeyInputReset();
    }

    private static void loadProjectFromFile(final File archive) {
        final List<ResourceCode> errorList = new LinkedList<>();

        ProgramState.load(() -> {
            try {
                loadProject(new FileInputStream(archive), errorList);

                if (!errorList.isEmpty()) {
                    DialogManager.set(() -> DialogAssembly.errorMessages(
                            RC_ERR_SC_OPEN_PROJECT,
                            errorList.toArray(ResourceCode[]::new)));
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }}, RC_LOAD_LOAD_PROJECT);
    }

    private static void loadProject(
            final InputStream archiveIn, final List<ResourceCode> errorList
    ) {
        final Path dir = FileIO.extractZipToTempDir(archiveIn);
        tempDirsForDeletion.add(dir);

        final Path specPath = dir.resolve(Constants.SPEC_FILENAME),
                sourcePath = dir.resolve(Constants.SOURCE_FILENAME),
                paintingPath = dir.resolve(Constants.PAINTING_FILENAME);

        final GameImage source = GameImageIO.readImage(sourcePath),
                painting = GameImageIO.readImage(paintingPath);

        if (source == null)
            errorList.add(RC_ERR_COULD_NOT_READ_SOURCE);
        if (painting == null)
            errorList.add(RC_ERR_COULD_NOT_READ_PAINTING);

        if (source == null || painting == null)
            return;

        // initialize attributes
        String name = null;
        Path folder = null;
        Version version = ProgramInfo.getVersion();
        int width = painting.getWidth(), height = painting.getHeight(),
                strokesCompleted = 0, strokesAttempted = 0;
        double scaleFactor = width / (double) source.getWidth();

        if (specPath.toFile().isFile()) {
            final String content = FileIO.readFile(specPath);
            final JSONPair[] pairs = JSONReader.readObject(content);

            if (pairs == null) {
                errorList.add(RC_ERR_COULD_NOT_PARSE_SPEC_AS_JSON);
                return;
            }

            // read attributes
            for (final JSONPair pair : pairs) {
                final ID k;
                final String v = String.valueOf(pair.value());

                try {
                    k = ID.valueOf(pair.key().toUpperCase());
                } catch (Exception e) {
                    continue;
                }

                switch (k) {
                    case VERSION -> version = Version.parse(v);
                    case NAME -> name = v;
                    case FOLDER -> folder =
                            Path.of(PathHelper.formatPathString(v));
                    case WIDTH ->
                            width = parseInt(pair.value(), width);
                    case HEIGHT ->
                            height = parseInt(pair.value(), height);
                    case SCALE_FACTOR -> scaleFactor =
                            parseDouble(pair.value(), scaleFactor);
                    case STROKES_COMPLETED ->
                            strokesCompleted = parseInt(
                                    pair.value(), strokesCompleted);
                    case STROKES_ATTEMPTED ->
                            strokesAttempted = parseInt(
                                    pair.value(), strokesAttempted);
                }
            }

            if (name == null)
                errorList.add(RC_ERR_COULD_NOT_READ_NAME_FROM_SPEC);
            if (folder == null)
                errorList.add(RC_ERR_COULD_NOT_READ_FOLDER_FROM_SPEC);

            if (name == null || folder == null)
                return;

            // build project
            final Project project = new Project(name, folder,
                    source, painting, width, height, scaleFactor,
                    strokesCompleted, strokesAttempted);
            ProjectManager.get().addProject(project, true);
        } else {
            errorList.add(RC_ERR_NO_SPEC_PATH);
        }
    }

    private static double parseDouble(final Object value, final double failCase) {
        return parse(value, Double.class, failCase);
    }

    private static int parseInt(final Object value, final int failCase) {
        return parse(value, Integer.class, failCase);
    }

    private static <T> T parse(
            final Object value, final Class<T> type, final T failCase
    ) {
        return type.isInstance(value) ? type.cast(value) : failCase;
    }
}
