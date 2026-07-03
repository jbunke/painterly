package com.jordanbunke.painterly.core.domains.save;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.io.SaveLoader;
import com.jordanbunke.painterly.resources.StringVariableMap;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.debug.LogManager;
import com.jordanbunke.painterly.util.debug.LogMessage;

import java.nio.file.Path;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.resources.StringVariableMap.ID.*;
import static com.jordanbunke.painterly.util.debug.LogChannel.*;

public final class SaveManager {
    private static final String PNG_SUFFIX = ".png";

    private final Project project;

    private boolean autosave;
    private int autosaveFrequency;

    public SaveManager(
            final Project project,
            final boolean autosave, final int autosaveFrequency
    ) {
        this.project = project;

        this.autosave = autosave;
        setAutosaveFrequency(autosaveFrequency);
    }

    public void save() {
        save(false);
    }

    public void checkAutosave(final int strokesCompleted) {
        if (autosave && strokesCompleted % autosaveFrequency == 0)
            autosave();
    }

    private void autosave() {
        save(true);
    }

    private void save(final boolean autosaving) {
        SaveLoader.saveProject(project);

        StringVariableMap.post(PROJECT_NAME, project.getName());
        LogManager.log(new LogMessage(SAVE_EXPORT,
                autosaving ? RC_LOG_AUTOSAVED : RC_LOG_SAVED));
    }

    public void export() {
        final GameImage painting = new GameImage(project.canvas.getPainting());
        final int strokes = project.strokeManager.getStrokesCompleted();
        final String filename = project.getName() + "_" + strokes + PNG_SUFFIX;
        final Path folder = project.getFolder();

        GameImageIO.writeImage(folder.resolve(filename), painting);

        StringVariableMap.post(PROJECT_NAME, project.getName());
        StringVariableMap.post(FILENAME, filename);
        LogManager.log(new LogMessage(SAVE_EXPORT, RC_LOG_EXPORTED));
    }

    public void setAutosaveFrequency(final int autosaveFrequency) {
        this.autosaveFrequency = MathPlus.bounded(
                Constants.MIN_AUTOSAVE_FREQUENCY,
                autosaveFrequency, Constants.MAX_AUTOSAVE_FREQUENCY);
    }

    public void setAutosave(final boolean autosave) {
        this.autosave = autosave;
    }

    public int getAutosaveFrequency() {
        return autosaveFrequency;
    }

    public boolean isAutosave() {
        return autosave;
    }
}
