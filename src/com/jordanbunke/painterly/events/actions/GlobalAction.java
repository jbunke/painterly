package com.jordanbunke.painterly.events.actions;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.menus.EditProgramSettings;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.dialog.data.menus.UpdateChannelStatus;
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
    EDIT_PROGRAM_SETTINGS(new Builder(RC_EDIT_PROGRAM_SETTINGS)
            .setBehaviour(() -> {
                EditProgramSettings.get().softReset();
                DialogManager.set(DialogAssembly::editProgramSettings);
            })),
    MAIN_MENU(new Builder(RC_NAV_MAIN_MENU)
            .setBehaviour(() -> ProgramState.setMenu(MenuAssembly::mainMenu))),
    QUIT_PROGRAM(new Builder(RC_NAV_QUIT_PROGRAM)
            .setBehaviour(() -> DialogManager.set(DialogAssembly::aysQuit))),
    DIALOG_CLOSE(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(ESCAPE))
            .setBehaviour(DialogManager::close)
            .setPrecondition(DialogManager::has)),
    DIALOG_OK(new Builder(RC_NA)
            .setShortcut(KeyboardShortcut.single(ENTER))
            .setBehaviour(() -> DialogManager.get().ok())
            .setPrecondition(() -> DialogManager.has() &&
                    DialogManager.get().validate())),
    TOGGLE_FULLSCREEN(new Builder(RC_TOGGLE_FULLSCREEN)
            .setShortcut(KeyboardShortcut.single(ESCAPE))
            .setBehaviour(Layout::toggleFullscreen)
            .setPrecondition(() -> !DialogManager.has())),
    NEW_PROJECT(new Builder(RC_NEW_PROJECT)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, N))
            .setBehaviour(() -> {
                NewProject.get().softReset();
                DialogManager.set(DialogAssembly::newProject);
            })
            .setPrecondition(() -> ProjectManager.get().canAddProject())),
    OPEN_PROJECT(new Builder(RC_OPEN_PROJECT)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(new KeyboardShortcut(true, false, O))
            .setBehaviour(SaveLoader::openProject)
            .setPrecondition(() -> ProjectManager.get().canAddProject())),
    // Message log and debug toggles
    UPDATE_CHANNEL_STATUS(new Builder(RC_CHANNEL_UPDATE_STATUS)
            .inheritTooltipCode()
            .setShortcut(new KeyboardShortcut(false, true, D))
            .setBehaviour(() -> {
                UpdateChannelStatus.get().softReset();
                DialogManager.set(DialogAssembly::updateChannelStatus);
            })),
    TOGGLE_LOG_GLOBAL_OFF(new Builder(RC_CHANNEL_TOGGLE_ALL)
            .setShortcut(new KeyboardShortcut(false, true, L))
            .setBehaviour(LogManager::toggleGlobalOff)),
    TOGGLE_RECENT_STROKES_VIS(new Builder(RC_CHANNEL_TOGGLE_RECENT_STROKES)
            .setShortcut(new KeyboardShortcut(false, true, P))
            .setBehaviour(() -> LogManager.toggleChannelStatus(
                    LogChannel.RECENT_STROKES))),
    TOGGLE_FPS_INDICATOR(new Builder(RC_CHANNEL_TOGGLE_FPS)
            .setShortcut(new KeyboardShortcut(false, true, F))
            .setBehaviour(() -> LogManager.toggleChannelStatus(LogChannel.FPS))),
    // Tool setters
    SET_TOOL_DRAW_FOCUS_AREA(new Builder(RC_TOOL_DRAW_FOCUS_AREA)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(X))
            .setBehaviour(() -> ToolManager.setCurrentTool(DRAW_FOCUS_AREA))),
    SET_TOOL_FOCUS_BOX_SELECT(new Builder(RC_TOOL_FOCUS_BOX_SELECT)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(B))
            .setBehaviour(() -> ToolManager.setCurrentTool(FOCUS_BOX_SELECT))),
    SET_TOOL_HAND(new Builder(RC_TOOL_HAND)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(H))
            .setBehaviour(() -> ToolManager.setCurrentTool(HAND))),
    SET_TOOL_MOVE_FOCUS_AREA(new Builder(RC_TOOL_MOVE_FOCUS_AREA)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(M))
            .setBehaviour(() -> ToolManager.setCurrentTool(MOVE_FOCUS_AREA))),
    SET_TOOL_ZOOM(new Builder(RC_TOOL_ZOOM)
            .inheritTooltipCode()
            .inheritIconCode()
            .setShortcut(KeyboardShortcut.single(Z))
            .setBehaviour(() -> ToolManager.setCurrentTool(ZOOM))),
    ;

    private final KeyboardShortcut shortcut;
    private final Runnable behaviour;
    private final ResourceCode code, tooltipCode;
    private final Supplier<ResourceCode> iconCodeGetter;
    private final Supplier<Boolean> precondition;

    GlobalAction(Builder builder) {
        this.code = builder.code;
        this.shortcut = builder.shortcut;
        this.behaviour = builder.behaviour;
        this.tooltipCode = builder.tooltipCode;
        this.iconCodeGetter = builder.iconCodeGetter;
        this.precondition = builder.precondition;
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
        return iconCodeGetter.get();
    }

    static class Builder {
        final ResourceCode code;

        KeyboardShortcut shortcut;
        ResourceCode tooltipCode;
        Supplier<ResourceCode> iconCodeGetter;

        Runnable behaviour;
        Supplier<Boolean> precondition;

        Builder(final ResourceCode code) {
            this.code = code;

            shortcut = null;
            tooltipCode = RC_NA;
            iconCodeGetter = () -> RC_NA;

            behaviour = () -> {};
            precondition = null;
        }

        Builder setShortcut(final KeyboardShortcut shortcut) {
            this.shortcut = shortcut;
            return this;
        }

        Builder setTooltipCode(final ResourceCode tooltipCode) {
            this.tooltipCode = tooltipCode;
            return this;
        }

        Builder inheritTooltipCode() {
            return setTooltipCode(code);
        }

        Builder setIconCodeGetter(
                final Supplier<ResourceCode> iconCodeGetter
        ) {
            this.iconCodeGetter = iconCodeGetter;
            return this;
        }

        Builder inheritIconCode() {
            return setIconCodeGetter(() -> code);
        }

        Builder setBehaviour(final Runnable behaviour) {
            this.behaviour = behaviour;
            return this;
        }

        Builder setPrecondition(final Supplier<Boolean> precondition) {
            this.precondition = precondition;
            return this;
        }
    }
}
