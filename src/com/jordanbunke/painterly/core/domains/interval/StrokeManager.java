package com.jordanbunke.painterly.core.domains.interval;

import com.jordanbunke.delta_time.utility.math.MathPlus;
import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.resources.StringVariableMap;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.debug.LogManager;
import com.jordanbunke.painterly.util.debug.LogMessage;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.resources.StringVariableMap.ID.*;
import static com.jordanbunke.painterly.settings.Settings.SettingID.SET_ID_DEFAULT_INTERVAL_TARGET;
import static com.jordanbunke.painterly.util.debug.LogChannel.*;

public final class StrokeManager {
    public static final boolean ATTEMPTED = true, COMPLETED = false;

    private final Project project;

    private int strokesAttempted, strokesCompleted,
            intervalTarget, intervalProgress, completedInInterval;

    /**
     * {@code true} is attempted strokes; {@code false} is completed strokes
     * */
    private boolean tickMode;

    public StrokeManager(
            final Project project,
            final int strokesCompleted, final int strokesAttempted
    ) {
        this.project = project;

        this.strokesAttempted = strokesAttempted;
        this.strokesCompleted = strokesCompleted;
        intervalTarget = Settings.get(SET_ID_DEFAULT_INTERVAL_TARGET, Integer.class);
        intervalProgress = 0;
        completedInInterval = 0;
    }

    public boolean tallyStroke(final boolean strokeAccepted) {
        strokesAttempted++;

        if (strokeAccepted) {
            strokesCompleted++;
            completedInInterval++;

            if (ProgramInfo.isFullRelease())
                project.saveManager.checkAutosave(strokesCompleted);
        }

        // expressed as equality with constant for sake of readability
        if (tickMode == ATTEMPTED || strokeAccepted)
            intervalProgress++;

        // end of interval check
        if (intervalProgress >= intervalTarget) {
            if (tickMode == ATTEMPTED) {
                StringVariableMap.post(INTERVAL_COMPLETED,
                        String.valueOf(completedInInterval));
                StringVariableMap.post(INTERVAL_TOTAL,
                        String.valueOf(intervalTarget));
                final double ratio = completedInInterval / (double) intervalTarget;
                StringVariableMap.post(INTERVAL_PERC,
                        BigDecimal.valueOf(ratio * 100)
                                .setScale(2, RoundingMode.HALF_UP).toString());
                LogManager.log(new LogMessage(INTERVAL_STATS, RC_LOG_INTERVAL_RATIO));
            }

            intervalProgress = 0;

            // TODO - change so that similarity updates are separated from
            //        intervals
            if (completedInInterval > 0)
                project.progressManager.update();

            completedInInterval = 0;

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

    public void setIntervalTarget(final int intervalTarget) {
        this.intervalTarget = MathPlus.bounded(Constants.MIN_INTERVAL_TARGET,
                intervalTarget, Constants.MAX_INTERVAL_TARGET);
    }
}
