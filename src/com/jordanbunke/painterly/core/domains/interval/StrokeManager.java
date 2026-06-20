package com.jordanbunke.painterly.core.domains.interval;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Constants;

import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_DEFAULT_INTERVAL_TARGET;

public final class StrokeManager {
    private static final boolean ATTEMPTED = true, COMPLETED = false;

    private final Project project;

    private int strokesAttempted, strokesCompleted,
            intervalTarget, intervalProgress;

    /**
     * {@code true} is attempted strokes; {@code false} is completed strokes
     * */
    private boolean tickMode;

    public StrokeManager(final Project project) {
        this.project = project;

        strokesAttempted = 0;
        strokesCompleted = 0;
        intervalTarget = Settings.get(SET_ID_DEFAULT_INTERVAL_TARGET, Integer.class);
        intervalProgress = 0;
    }

    public boolean tallyStroke(final boolean strokeAccepted) {
        strokesAttempted++;

        if (strokeAccepted) {
            strokesCompleted++;

            // TODO - use variable
            if (strokesCompleted % Constants.STAT_UPDATE_STROKE_INTERVAL == 0)
                project.updateSimilarity();
        }

        if (tickMode || strokeAccepted)
            intervalProgress++;

        if (intervalProgress >= intervalTarget) {
            intervalProgress = 0;
            return true;
        }

        return false;
    }

    public boolean isTickMode() {
        return tickMode;
    }

    public void toggleTickMode() {
        tickMode = !tickMode;
    }

    public void setTickModeToAttempted() {
        tickMode = ATTEMPTED;
    }

    public void setTickModeToCompleted() {
        tickMode = COMPLETED;
    }

    public int getStrokesAttempted() {
        return strokesAttempted;
    }

    public int getStrokesCompleted() {
        return strokesCompleted;
    }

    public int getIntervalProgress() {
        return intervalProgress;
    }

    public int getIntervalTarget() {
        return intervalTarget;
    }
}
