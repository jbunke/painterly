package com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual;

import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;

public abstract class MenuBarButton extends MenuButtonStub
        implements TextButton {
    public MenuBarButton(
            final Coord2D position, final Bounds2D dimensions
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);
    }
}
