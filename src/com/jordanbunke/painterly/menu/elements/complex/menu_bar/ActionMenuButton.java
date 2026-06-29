package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Tooltip;

import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;

public final class ActionMenuButton<T> extends MenuBarButton {
    private final IAction<T> action;
    private final SubMenu parent;

    private String label;
    private GameImage stub, base, highlight;

    private boolean passing;

    public ActionMenuButton(
            final Coord2D position, final int width,
            final IAction<T> action, final SubMenu parent
    ) {
        super(position, new Bounds2D(width, TEXT_BUTTON_DEF_HEIGHT));

        this.action = action;
        this.parent = parent;

        this.label = LanguageData.retrieveUIText(action.getCode());

        drawButtons();
        update(0d);
    }

    @Override
    public void execute() {
        action.execute();

        MenuBarManager.explicitClick();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
            Tooltip.get().pingCode(action.getTooltipCode(), mousePos);

            Cursor.ping(MenuBarManager.isClicked() && passing
                    ? Cursor.POINTER : Cursor.MAIN);

            MenuBarManager.pingHover(parent);
        }
    }

    @Override
    public void update(final double deltaTime) {
        passing = action.isPassing();

        checkUpdatedLabel();
    }

    private void checkUpdatedLabel() {
        final String label = LanguageData.retrieveUIText(action.getCode());

        if (!label.equals(this.label)) {
            this.label = label;
            drawButtons();
        }
    }

    private void drawButtons() {
        stub = drawActionMenuButton(this, true, action);
        base = drawActionMenuButton(sim(false, false), false, action);
        highlight = drawActionMenuButton(sim(false, true), false, action);
    }

    @Override
    public void render(final GameImage canvas) {
        draw(passing ? (isHighlighted() ? highlight : base) : stub, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.LEFT;
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.MENU_BAR_ACTION;
    }
}
