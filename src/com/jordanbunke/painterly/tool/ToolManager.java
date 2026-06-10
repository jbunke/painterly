package com.jordanbunke.painterly.tool;

import java.util.function.Supplier;

public final class ToolManager {
    private enum ToolEnum {
        HAND(Hand::get);

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
}
