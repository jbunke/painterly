package com.jordanbunke.painterly.tool;

import java.util.function.Supplier;

public final class ToolManager {
    public enum ToolEnum {
        HAND(Hand::get), DRAW_FOCUS_AREA(DrawFocusArea::get);

        private final Supplier<Tool> retriever;

        ToolEnum(final Supplier<Tool> retriever) {
            this.retriever = retriever;
        }
    }

    private static ToolEnum currentTool;

    static {
        currentTool = ToolEnum.HAND;
    }

    public static Tool getCurrentTool() {
        return currentTool.retriever.get();
    }

    public static void setCurrentTool(final ToolEnum tool) {
        if (tool != currentTool)
            currentTool.retriever.get().deselect();

        currentTool = tool;
    }
}
