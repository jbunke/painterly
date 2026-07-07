package com.jordanbunke.painterly.core;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.core.domains.debug.DebugData;
import com.jordanbunke.painterly.core.domains.focus.FocusManager;
import com.jordanbunke.painterly.core.domains.interval.ProgressManager;
import com.jordanbunke.painterly.core.domains.interval.StrokeManager;
import com.jordanbunke.painterly.core.domains.save.SaveManager;
import com.jordanbunke.painterly.core.paint.Canvas;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Constants;

import java.nio.file.Path;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_AUTOSAVE_ON_BY_DEFAULT;
import static com.jordanbunke.painterly.theme.Graphics.blankCanvas;

public final class Project {
    public final double scaleFactor;
    public final int width, height;

    private String name;
    private Path folder;

    private final GameImage sourceImage;

    public final StrokeManager strokeManager;
    public final ProgressManager progressManager;
    public final SaveManager saveManager;
    public final FocusManager focusManager;
    public final DebugData debugData;
    public final Canvas canvas;

    private boolean painting;

    private Project(
            final String name, final Path folder,
            final GameImage sourceImage, final GameImage paintingImage,
            final int width, final int height, final double scaleFactor,
            final int strokesCompleted, final int strokesAttempted,
            final boolean autosave, final int autosaveFrequency
    ) {
        this.name = name;
        this.folder = folder;
        this.sourceImage = sourceImage;

        this.scaleFactor = scaleFactor;
        this.width = width;
        this.height = height;

        canvas = new Canvas(this, paintingImage);
        debugData = new DebugData(this);
        strokeManager = new StrokeManager(this,
                strokesCompleted, strokesAttempted);
        focusManager = new FocusManager(this);
        progressManager = new ProgressManager(this);
        saveManager = new SaveManager(this,
                autosave, autosaveFrequency);

        painting = false;
    }

    public void toggleSimulation() {
        painting = !painting;
    }

    public void disable() {
        painting = false;
    }

    public void update() {
        if (painting)
            attemptStroke();
    }

    private void attemptStroke() {
        final boolean strokeAccepted = canvas.attemptStroke(),
                intervalCompleted = strokeManager.tallyStroke(strokeAccepted);

        if (intervalCompleted)
            focusManager.tryUpdateBox();
    }

    public GameImage getSourceImage() {
        return sourceImage;
    }

    public boolean isPainting() {
        return painting;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Path getFolder() {
        return folder;
    }

    public void setFolder(final Path folder) {
        this.folder = folder;
    }

    public static class Builder {
        private final String name;
        private final Path folder;
        private final GameImage sourceImage;

        private double scaleFactor;

        private GameImage paintingImage;
        private int width, height, strokesCompleted, strokesAttempted;

        // Autosave
        private boolean autosave;
        private int autosaveFrequency;

        public Builder(
                final String name, final Path folder,
                final GameImage sourceImage
        ) {
            this.name = name;
            this.folder = folder;
            this.sourceImage = sourceImage;

            scaleFactor = 1.0;
            width = 0;
            height = 0;
            strokesCompleted = 0;
            strokesAttempted = 0;

            paintingImage = null;

            autosave = Settings.get(SET_ID_AUTOSAVE_ON_BY_DEFAULT, Boolean.class);
            autosaveFrequency = Constants.DEF_AUTOSAVE_FREQUENCY;
        }

        public Builder setScaleFactor(
                final double scaleFactor, final boolean updateWidthAndHeight
        ) {
            this.scaleFactor = scaleFactor;

            return updateWidthAndHeight
                    ? setWidthAndHeightFromScaleFactor() : this;
        }

        public Builder setScaleFactor(final double scaleFactor) {
            return setScaleFactor(scaleFactor, false);
        }

        public Builder setWidthAndHeightFromScaleFactor() {
            width = (int)(sourceImage.getWidth() * scaleFactor);
            height = (int)(sourceImage.getHeight() * scaleFactor);
            return this;
        }

        public Builder setWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(int height) {
            this.height = height;
            return this;
        }

        public Builder setStrokesAttempted(final int strokesAttempted) {
            this.strokesAttempted = strokesAttempted;
            return this;
        }

        public Builder setStrokesCompleted(final int strokesCompleted) {
            this.strokesCompleted = strokesCompleted;
            return this;
        }

        public Builder setPaintingImage(final GameImage paintingImage) {
            this.paintingImage = paintingImage;
            return this;
        }

        public Builder setAutosave(final boolean autosave) {
            this.autosave = autosave;
            return this;
        }

        public Builder setAutosaveFrequency(final int autosaveFrequency) {
            this.autosaveFrequency = autosaveFrequency;
            return this;
        }

        public Project build() {
            if (width == 0 || height == 0)
                setWidthAndHeightFromScaleFactor();

            if (strokesCompleted > strokesAttempted) {
                setStrokesCompleted(0);
                setStrokesAttempted(0);
            }

            if (paintingImage == null)
                setPaintingImage(blankCanvas(width, height));

            return new Project(name, folder, sourceImage, paintingImage,
                    width, height, scaleFactor,
                    strokesCompleted, strokesAttempted,
                    autosave, autosaveFrequency);
        }
    }
}
