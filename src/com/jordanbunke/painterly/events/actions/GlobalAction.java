package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.io.SaveLoader;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.util.Layout;
import com.jordanbunke.painterly.util.debug.LogChannel;
import com.jordanbunke.painterly.util.debug.LogManager;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.tool.ToolManager.ToolEnum.*;

public enum GlobalAction
        implements IAction<Runnable>, ISubMenuEntry {
    MAIN_MENU(RC_NAV_MAIN_MENU, null,
            () -> ProgramState.setMenu(MenuAssembly::mainMenu)),
    QUIT_PROGRAM(RC_NAV_QUIT_PROGRAM, null,
            () -> DialogManager.set(DialogAssembly::aysQuit)),
    DIALOG_CLOSE(KeyboardShortcut.single(ESCAPE), DialogManager::close),
    DIALOG_OK(KeyboardShortcut.single(ENTER), () -> DialogManager.get().ok()),
    TOGGLE_FULLSCREEN(RC_TOGGLE_FULLSCREEN, KeyboardShortcut.single(ESCAPE),
            Layout::toggleFullscreen),
    NEW_PROJECT(RC_NEW_PROJECT, true, true,
            new KeyboardShortcut(true, false, N), () -> {
        NewProject.get().softReset();
        DialogManager.set(DialogAssembly::newProject);
    }),
    OPEN_PROJECT(RC_OPEN_PROJECT, true, true,
            new KeyboardShortcut(true, false, O),
            SaveLoader::openProject),
    TOGGLE_RECENT_STROKE_VISUALIZATION(RC_NA /* TODO */,
            new KeyboardShortcut(false, true, P),
            () -> LogManager.toggleChannelStatus(LogChannel.RECENT_STROKE_ATTEMPTS)),
    TOGGLE_FPS_INDICATOR(RC_NA /* TODO */,
            new KeyboardShortcut(false, true, F),
            () -> LogManager.toggleChannelStatus(LogChannel.FPS)),
    SET_TOOL_DRAW_FOCUS_AREA(RC_TOOL_DRAW_FOCUS_AREA, true, true,
            KeyboardShortcut.single(X),
            () -> ToolManager.setCurrentTool(DRAW_FOCUS_AREA)),
    SET_TOOL_FOCUS_BOX_SELECT(RC_TOOL_FOCUS_BOX_SELECT, true, true,
            KeyboardShortcut.single(B),
            () -> ToolManager.setCurrentTool(FOCUS_BOX_SELECT)),
    SET_TOOL_HAND(RC_TOOL_HAND, true, true,
            KeyboardShortcut.single(H),
            () -> ToolManager.setCurrentTool(HAND)),
    SET_TOOL_MOVE_FOCUS_AREA(RC_TOOL_MOVE_FOCUS_AREA, true, true,
            KeyboardShortcut.single(M),
            () -> ToolManager.setCurrentTool(MOVE_FOCUS_AREA)),
    SET_TOOL_ZOOM(RC_TOOL_ZOOM, true, true,
            KeyboardShortcut.single(Z),
            () -> ToolManager.setCurrentTool(ZOOM)),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        DIALOG_CLOSE.precondition = DialogManager::has;
        DIALOG_OK.precondition = () -> DialogManager.has() &&
                DialogManager.get().validate();
        TOGGLE_FULLSCREEN.precondition = () -> !DialogManager.has();
        NEW_PROJECT.precondition = () -> ProjectManager.get().canAddProject();
        OPEN_PROJECT.precondition = () -> ProjectManager.get().canAddProject();
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Runnable behaviour;
    private final ResourceCode code, tooltipCode;

    private ResourceCode iconCode;
    private Supplier<Boolean> precondition;

    GlobalAction(
            final ResourceCode code,
            final ResourceCode tooltipCode, final ResourceCode iconCode,
            final KeyboardShortcut shortcut,
            final Runnable behaviour
    ) {
        this.code = code;
        this.shortcut = shortcut;
        this.behaviour = behaviour;

        this.tooltipCode = tooltipCode;
        this.iconCode = iconCode;
        precondition = null;
    }

    GlobalAction(
            final ResourceCode code,
            final boolean inheritTooltip, final boolean inheritIcon,
            final KeyboardShortcut shortcut,
            final Runnable behaviour
    ) {
        this(code, inheritTooltip ? code : RC_NA,
                inheritIcon ? code : RC_NA, shortcut, behaviour);
    }

    GlobalAction(
            final ResourceCode code,
            final KeyboardShortcut shortcut,
            final Runnable behaviour
    ) {
        this(code, false, false, shortcut, behaviour);
    }

    GlobalAction(
            final KeyboardShortcut shortcut,
            final Runnable behaviour
    ) {
        this(RC_NA, shortcut, behaviour);
    }

    public boolean tryForMatchingKeyStroke(final InputEventLogger eventLogger) {
        return IAction.super.tryForMatchingKeyStroke(eventLogger, null);
    }

    @Override
    public Runnable defaultFetch() {
        return null;
    }

    @Override
    public boolean requiresNonNull() {
        return false;
    }

    @Override
    public int getWidthAllotment() {
        return IAction.super.getWidthAllotment();
    }

    @Override
    public KeyboardShortcut getShortcut() {
        return shortcut;
    }

    @Override
    public Predicate<Runnable> getPrecondition() {
        return precondition == null
                ? null : t -> precondition.get();
    }

    @Override
    public Consumer<Runnable> getBehaviour() {
        return t -> behaviour.run();
    }

    @Override
    public ResourceCode getCode() {
        return code;
    }

    @Override
    public ResourceCode getTooltipCode() {
        return tooltipCode;
    }

    @Override
    public ResourceCode getIconCode() {
        return iconCode;
    }
}
