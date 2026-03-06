package com.jordanbunke.painterly.menu.dialog;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class DialogElement {
    public final int columnIndex, columns;
    public final String id;
    public final MenuElement element;

    DialogElement(
            final int columnIndex, final int columns,
            final String id, final MenuElement element
    ) {
        this.columnIndex = columnIndex;
        this.columns = columns;
        this.id = id;
        this.element = element;
    }

    public Coord2D rightOf(final int buffer) {
        return element.rightOf().displaceX(buffer);
    }

    // TODO - default cases

    public Coord2D below(final int buffer) {
        return element.below().displaceY(buffer);
    }
}
