package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Cursor;

import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;

public final class SubMenuButton extends MenuBarButton {
    private final String label;
    private final SubMenu subMenu;

    private final GameImage base, highlight;

    private SubMenuButton(
            final Coord2D position, final Bounds2D dimensions,
            final String label, final SubMenu subMenu
    ) {
        super(position, dimensions);

        this.label = label;
        this.subMenu = subMenu;

        // TODO - actual dedicated draw functions
        base = drawTextButton(sim(false, false));
        highlight = drawTextButton(sim(false, true));
    }

    public static SubMenuButton at(
            final Coord2D position, final ResourceCode code,
            final SubMenu subMenu
    ) {
        final String label = LanguageData.retrieveUIText(code);
        final int width = (2 * MENU_BAR_PADDING_X) + standardTextWidth(label);
        final Bounds2D dimensions = new Bounds2D(width, TEXT_BUTTON_DEF_HEIGHT);

        return new SubMenuButton(position, dimensions, label, subMenu);
    }

    @Override
    public void execute() {
        MenuBarManager.explicitClick();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            Cursor.ping(Cursor.POINTER);
            MenuBarManager.pingHover(subMenu);
        }
    }

    @Override
    public void update(final double deltaTime) {}

    @Override
    public void render(final GameImage canvas) {
        draw(isHighlighted() ? highlight : base, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.CENTER;
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.MENU_BAR_HEADER;
    }
}
