package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.AbstractNavbar;
import com.jordanbunke.delta_time.menu.menu_elements.ext.dropdown.NestedItem;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.AbstractVerticalScrollBox;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;

public final class MenuBar extends AbstractNavbar {
    // TODO - extract constants and simplify constructor signature
    private MenuBar(Coord2D position, Bounds2D dimensions, int renderOrder, NestedItem[] submenus) {
        super(position, dimensions, renderOrder, submenus);
    }

    // TODO - signature and implementation
    @Override
    protected AbstractVerticalScrollBox makeDDContainer(Coord2D coord2D) {
        return null;
    }

    public static Builder init(/* TODO */) {
        return new Builder();
    }

    public static class Builder implements MenuElementBuilder<MenuBar> {
        private final Coord2D position;
        // TODO - other fields

        Builder(/* TODO */) {
            this.position = new Coord2D();

            // TODO
        }

        // TODO - setters

        @Override
        public MenuBar build() {
            // TODO
            return null;
        }
    }
}
