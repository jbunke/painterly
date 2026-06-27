package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.util.Graphics;

import static com.jordanbunke.painterly.util.Layout.*;

public final class SubMenuContent extends MenuElementContainer {
    private final MenuElement[] menuElements;
    private final int[] separators;

    private final GameImage sepImage;

    private SubMenuContent(
            final Coord2D position,
            final Bounds2D dimensions,
            final MenuElement[] menuElements,
            final int[] separators
    ) {
        super(position, dimensions, Anchor.LEFT_TOP, true);

        this.menuElements = menuElements;
        this.separators = separators;

        sepImage = Graphics.drawMenuBarSeparator(getWidth());
    }

    public static SubMenuContent at(
            final Coord2D position,
            final SubMenuData data,
            final SubMenu subMenu
    ) {
        final MenuElement[] menuElements = new MenuElement[data.entries.length];
        final Bounds2D dimensions = data.getContentBounds();

        for (int i = 0; i < menuElements.length; i++) {
            final ISubMenuEntry entry = data.entries[i];
            final Coord2D entryPosition = position.displace(0,
                    TEXT_BUTTON_DEF_HEIGHT * i);

            if (entry instanceof SubMenuData d)
                menuElements[i] = new SubMenu(entryPosition, d, subMenu);
            else if (entry instanceof IAction<?> action)
                menuElements[i] = new ActionMenuButton<>(entryPosition,
                        dimensions.width(), action, subMenu);
        }

        return new SubMenuContent(position,
                dimensions, menuElements, data.separators);
    }

    @Override
    public void render(final GameImage canvas) {
        super.render(canvas);

        for (int separator : separators) {
            final Coord2D offset = getPosition()
                    .displaceY(separator * TEXT_BUTTON_DEF_HEIGHT)
                    .displaceY(-sepImage.getHeight() / 2);

            canvas.draw(sepImage, offset.x, offset.y);
        }
    }

    // TODO - override render to render separators

    @Override
    public MenuElement[] getMenuElements() {
        return menuElements;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }
}
