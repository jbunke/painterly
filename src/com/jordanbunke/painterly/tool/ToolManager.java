package com.jordanbunke.painterly.tool;

import com.jordanbunke.painterly.events.actions.GlobalAction;

import java.util.function.Supplier;

import static com.jordanbunke.painterly.events.actions.GlobalAction.*;

public final class ToolManager {
    public enum ToolEnum {
        DRAW_FOCUS_AREA(DrawFocusArea::get, SET_TOOL_DRAW_FOCUS_AREA),
        MOVE_FOCUS_AREA(MoveFocusArea::get, SET_TOOL_MOVE_FOCUS_AREA),
        HAND(Hand::get, SET_TOOL_HAND),
        ZOOM(Zoom::get, SET_TOOL_ZOOM),
        ;

        private final Supplier<Tool> retriever;
        public final GlobalAction setter;

        ToolEnum(final Supplier<Tool> retriever, final GlobalAction setter) {
            this.retriever = retriever;
            this.setter = setter;
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

    public static GlobalAction getCurrentAction() {
        return currentTool.setter;
    }
}
