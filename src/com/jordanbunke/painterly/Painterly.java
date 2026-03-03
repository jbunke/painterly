package com.jordanbunke.painterly;

import com.jordanbunke.delta_time.OnStartup;
import com.jordanbunke.delta_time._core.GameManager;
import com.jordanbunke.delta_time._core.Program;
import com.jordanbunke.delta_time._core.ProgramContext;
import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.Key;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.window.GameWindow;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.settings.Settings;
import com.jordanbunke.painterly.settings.update.VersionHandler;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.Layout;

public final class Painterly implements ProgramContext {
    public final Program program;
    public GameWindow window;

    private Painterly() {
        window = makeWindow();

        final GameManager manager = new GameManager(0, this);
        program = new Program(window, manager, Constants.TICK_HZ, Constants.FPS);

        // config
        program.setCanvasSize(Layout.width(), Layout.height());
        program.setScheduleUpdates(false);
        program.getDebugger().muteChannel(GameDebugger.FRAME_RATE);
    }

    public static void main(final String[] args) {
        OnStartup.run();
        Key.setCtrlCommandMatch(true);
        Settings.read();
        ProgramInfo.readProgramFile();
        VersionHandler.startup();

        new Painterly();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        ProgramState.get().process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        ProgramState.get().update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        ProgramState.get().render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    private GameWindow makeWindow() {
        final GameWindow window = new GameWindow(
                ProgramInfo.PROGRAM_NAME + " " + ProgramInfo.formatVersion(),
                Layout.width(), Layout.height(), /* TODO */ GameImage.dummy(),
                true, /* TODO - assess */ true, Layout.isFullscreen()
        );
        window.hideCursor();

        return window;
    }

    // TODO - assess need
    public void remakeWindow() {
        window = makeWindow();
        program.setCanvasSize(Layout.width(), Layout.height());
        program.replaceWindow(window);

        // TODO - redraw components if necessary

        window.focus();
    }

    public static void quitProgram() {
        Settings.write();
        System.exit(0);
    }
}
