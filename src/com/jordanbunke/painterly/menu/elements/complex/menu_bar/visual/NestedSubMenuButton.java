package com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual;

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
import static com.jordanbunke.painterly.util.Layout.TEXT_BUTTON_DEF_HEIGHT;

public final class NestedSubMenuButton extends MenuBarButton {
    private final String label;
    private final SubMenu subMenu;

    private final GameImage base, highlight;

    private NestedSubMenuButton(
            final Coord2D position, final Bounds2D dimensions,
            final String label, final SubMenu subMenu
    ) {
        super(position, dimensions);

        this.label = label;
        this.subMenu = subMenu;

        base = drawSubMenuHeader(sim(false, false));
        highlight = drawSubMenuHeader(sim(false, true));
    }

    public static NestedSubMenuButton at(
            final Coord2D position, final ResourceCode code,
            final SubMenu subMenu, final int parentWidth
    ) {
        final String label = LanguageData.retrieveUIText(code);
        final Bounds2D dimensions =
                new Bounds2D(parentWidth, TEXT_BUTTON_DEF_HEIGHT);

        return new NestedSubMenuButton(position, dimensions, label, subMenu);
    }

    @Override
    public void execute() {}

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            Cursor.ping(Cursor.MAIN);
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
        return Alignment.LEFT;
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.MENU_BAR_NESTED;
    }
}
