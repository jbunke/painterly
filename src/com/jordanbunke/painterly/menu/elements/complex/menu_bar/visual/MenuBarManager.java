package com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual;

import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.io.InputEventLogger;

import java.util.List;

public final class MenuBarManager {
    // TODO
    // highlighted button, if any
    // reevaluate expansion accordingly based on hierarchy

    private static SubMenu hovered, expansionBasis;
    private static boolean clicked, loggedClick;

    static {
        hovered = null;
        expansionBasis = null;

        clicked = false;
        loggedClick = false;
    }

    public static void reset() {
        // check if previous frame logged click with no nav button hovered over
        // if so, collapse all menus
        if (loggedClick && hovered == null) {
            clicked = false;
            evaluateExpansion(null);
        }

        hovered = null;
        loggedClick = false;
    }

    public static void checkForLoggedClick(final InputEventLogger eventLogger) {
        final List<GameEvent> events = eventLogger.getUnprocessedEvents();

        for (GameEvent event : events)
            if (event instanceof GameMouseEvent gme &&
                    gme.action == GameMouseEvent.Action.DOWN) {
                loggedClick = true;
                return;
            }
    }

    public static void explicitClick() {
        clicked = !clicked;

        if (!clicked)
            evaluateExpansion(null);
    }

    public static void pingHover(final SubMenu deepestRequiredExpansion) {
        if (hovered == null) {
            hovered = deepestRequiredExpansion;

            if (clicked && !hovered.equals(expansionBasis))
                evaluateExpansion(hovered);
        }
    }

    private static void evaluateExpansion(final SubMenu eb) {
        expansionBasis = eb;

        MenuBar.get().collapseAll();

        if (expansionBasis != null)
            expansionBasis.expand();
    }

    public static boolean isClicked() {
        return clicked;
    }
}
