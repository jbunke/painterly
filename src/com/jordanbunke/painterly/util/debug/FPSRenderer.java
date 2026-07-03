package com.jordanbunke.painterly.util.debug;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.theme.Colors;

import static com.jordanbunke.painterly.util.Layout.*;

public final class FPSRenderer {
    private static int lastFps, lastW;
    private static SimpleLabel fpsLabel;

    static {
        lastFps = 0;
        lastW = width();
        fpsLabel = null;
    }

    public static void debugRender(final GameImage canvas, final GameDebugger debugger) {
        if (LogManager.isChannelActive(LogChannel.FPS))
            renderFps(canvas, debugger.getFPS());
    }

    private static void renderFps(final GameImage canvas, final int fps) {
        final int w = width();

        if (fps != lastFps || w != lastW || fpsLabel == null) {
            final int x = w - DEBUG_EDGE_MARGIN;

            fpsLabel = SimpleLabel.initLiteral(
                    String.valueOf(fps), new Coord2D(x, DEBUG_EDGE_MARGIN))
                    .setColor(Colors.debug())
                    .setAnchor(MenuElement.Anchor.RIGHT_TOP).build();

            lastFps = fps;
            lastW = w;
        }

        fpsLabel.render(canvas);
    }
}
