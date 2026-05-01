package com.jordanbunke.painterly.menu.dialog;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class DialogElement {
    public final int columnIndex, columns;
    public final String id;
    public final MenuElement element;

    private DialogElement(
            final int columnIndex, final int columns,
            final String id, final MenuElement element
    ) {
        this.columnIndex = columnIndex;
        this.columns = columns;
        this.id = id;
        this.element = element;
    }

    public static Builder init(
            final String id, final MenuElement element
    ) {
        return new Builder(id, element);
    }

    public Coord2D rightOf(final int buffer) {
        return element.rightOf().displaceX(buffer);
    }

    // TODO - default cases

    public Coord2D below(final int buffer) {
        return element.below().displaceY(buffer);
    }

    public static class Builder {
        private final String id;
        private final MenuElement element;

        private int columnIndex, columns;

        Builder(final String id, final MenuElement element) {
            this.id = id;
            this.element = element;

            columnIndex = 0;
            columns = 1;
        }

        public Builder setColumnIndex(final int columnIndex) {
            this.columnIndex = columnIndex;
            return this;
        }

        public Builder setColumns(final int columns) {
            this.columns = columns;
            return this;
        }

        /**
         * To be invoked after {@link #columnIndex} and {@link #columns}
         * have been specified.
         * <br>
         * Only used for left-most element on its line in its column.
         * */
        public Builder autoAlignX(final PopUpDialog.Builder db) {
            switch (element.getAnchor()) {
                case LEFT_TOP, LEFT_CENTRAL, LEFT_BOTTOM ->
                        element.setX(db.elementX(columnIndex, columns));
            }

            return this;
        }

        public DialogElement build() {
            return new DialogElement(columnIndex, columns, id, element);
        }
    }
}
