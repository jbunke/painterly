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
import com.jordanbunke.painterly.util.debug.FPSRenderer;

public final class Painterly implements ProgramContext {
    public final Program program;
    public GameWindow window;

    private static final Painterly INSTANCE;

    static {
        OnStartup.run();
        Key.setCtrlCommandMatch(true);
        Settings.read();
        ProgramInfo.readProgramFile();
        VersionHandler.startup();

        INSTANCE = new Painterly();
    }

    private Painterly() {
        window = makeWindow();

        final GameManager manager = new GameManager(0, this);
        program = new Program(window, manager, Constants.TICK_HZ, Constants.FPS);

        // config
        program.setCanvasSize(Layout.width(), Layout.height());
        program.setScheduleUpdates(false);
        program.getDebugger().muteChannel(GameDebugger.FRAME_RATE);
    }

    public static Painterly get() {
        return INSTANCE;
    }

    public static void main(final String[] args) {
        // triggers static initializer
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
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {
        FPSRenderer.debugRender(canvas, debugger);
    }

    private GameWindow makeWindow() {
        final GameWindow.Builder wb = new GameWindow.Builder();

        final String title = ProgramInfo.PROGRAM_NAME + " " +
                ProgramInfo.formatVersion();
        final boolean fullscreen = Layout.isFullscreen();

        wb.setTitle(title)
                .setIcon(/* TODO */ GameImage.dummy())
                .setExitOnClose(true)
                .setCanResize(false) // TODO - allow resizing
                .setFullscreen(fullscreen);

        if (!fullscreen) {
            wb.setMinWidth(Layout.MIN_WINDOWED_WIDTH)
                    .setMinHeight(Layout.MIN_WINDOWED_HEIGHT)
                    .setWidth(Layout.width(), false)
                    .setHeight(Layout.height(), false);
        }

        final GameWindow window = wb.build();
        window.hideCursor();

        return window;
    }

    public void remakeWindow() {
        window = makeWindow();
        program.setCanvasSize(Layout.width(), Layout.height());
        program.replaceWindow(window);

        window.focus();
    }

    public static void quitProgram() {
        Settings.write();
        System.exit(0);
    }
}
