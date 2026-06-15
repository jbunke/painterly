package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.menu.MenuAssembly;
import com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual.ISubMenuEntry;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.settings.RuntimeSettings;
import com.jordanbunke.painterly.tool.ToolManager;
import com.jordanbunke.painterly.util.Layout;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.jordanbunke.delta_time.events.Key.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.tool.ToolManager.ToolEnum.*;

public enum GlobalAction
        implements IAction</* TODO - evaluate whether more suitable type exists */ Runnable>, ISubMenuEntry {
    MAIN_MENU(RC_NAV_MAIN_MENU, null,
            () -> ProgramState.setMenu(MenuAssembly::mainMenu)),
    QUIT_PROGRAM(RC_NAV_QUIT_PROGRAM, null, Painterly::quitProgram),
    DIALOG_CLOSE(KeyboardShortcut.single(ESCAPE), DialogManager::close),
    DIALOG_OK(KeyboardShortcut.single(ENTER),
            () -> DialogManager.get().variableSet.ok()),
    TOGGLE_FULLSCREEN(RC_TOGGLE_FULLSCREEN, KeyboardShortcut.single(ESCAPE),
            Layout::toggleFullscreen),
    NEW_PROJECT(RC_NEW_PROJECT, new KeyboardShortcut(true, false, N),
            () -> DialogManager.set(DialogAssembly::newProject)),
    OPEN_PROJECT(RC_OPEN_PROJECT, new KeyboardShortcut(true, false, O),
            () -> {} /* TODO */),
    TOGGLE_DEBUG_PROFILER(RC_NA, new KeyboardShortcut(false, true, P),
            RuntimeSettings::toggleProfilerOn),
    SET_TOOL_HAND(RC_TOOL_HAND, KeyboardShortcut.single(H),
            () -> ToolManager.setCurrentTool(HAND)),
    SET_TOOL_DRAW_FOCUS_AREA(RC_TOOL_DRAW_FOCUS_AREA, KeyboardShortcut.single(X),
            () -> ToolManager.setCurrentTool(DRAW_FOCUS_AREA)),
    ;

    static {
        // Populate icon codes for actions with icons
        // TODO

        // Populate preconditions
        DIALOG_CLOSE.precondition = DialogManager::has;
        DIALOG_OK.precondition = () -> DialogManager.has() &&
                DialogManager.get().variableSet.validate();
        TOGGLE_FULLSCREEN.precondition = () -> !DialogManager.has();
        // TODO
    }

    private final KeyboardShortcut shortcut;
    private final Runnable behaviour;
    private final ResourceCode code;

    private ResourceCode iconCode;
    private Supplier<Boolean> precondition;

    GlobalAction(
            final ResourceCode code,
            final KeyboardShortcut shortcut,
            final Runnable behaviour
    ) {
        this.code = code;
        this.shortcut = shortcut;
        this.behaviour = behaviour;

        iconCode = ResourceCode.RC_NA;
        precondition = null;
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
}
