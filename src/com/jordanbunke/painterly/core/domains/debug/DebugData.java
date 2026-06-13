package com.jordanbunke.painterly.core.domains.debug;

import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.paint.BrushStroke;
import com.jordanbunke.painterly.util.Constants;

import java.util.ArrayList;
import java.util.List;

public final class DebugData {
    private final Project project;

    private final List<BrushStroke> recentStrokes;

    public DebugData(final Project project) {
        this.project = project;

        recentStrokes = new ArrayList<>();
    }

    public void addStroke(final BrushStroke stroke) {
        recentStrokes.add(0, stroke);

        while (recentStrokes.size() > Constants.MAX_RECENT_STROKES_DEBUG)
            recentStrokes.remove(Constants.MAX_RECENT_STROKES_DEBUG);
    }

    public List<BrushStroke> getRecentStrokes() {
        return recentStrokes;
    }
}
