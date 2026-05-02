package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;

public final class DialogElement {
    /**
     * Optional layout fields that can be used to inform the {@link PopUpDialog.Builder}
     * of this {@link DialogElement}'s intended position
     * */
    public final int columnIndex, columns;
    public final double row;

    public final MenuElement element;

    private DialogElement(
            final int columnIndex, final int columns,
            final double row, final MenuElement element
    ) {
        this.columnIndex = columnIndex;
        this.columns = columns;
        this.row = row;
        this.element = element;
    }

    public static Builder init(final MenuElement element) {
        return new Builder(element);
    }

    public Coord2D rightOf(final int buffer) {
        return element.rightOf().displaceX(buffer);
    }

    public Coord2D below(final int buffer) {
        return element.below().displaceY(buffer);
    }

    public static class Builder {
        private final MenuElement element;

        private int columnIndex, columns;
        private double row;

        Builder(final MenuElement element) {
            this.element = element;

            columnIndex = 0;
            columns = 1;
            row = 0;
        }

        public Builder setColumnIndex(final int columnIndex) {
            this.columnIndex = columnIndex;
            return this;
        }

        public Builder setColumns(final int columns) {
            this.columns = columns;
            return this;
        }

        public Builder setRow(final double row) {
            this.row = row;
            return this;
        }

        /**
         * To be invoked after {@link #columnIndex} and {@link #columns}
         * have been specified.
         * <br>
         * Only used for left-most element on its line in its column.
         * */
        public Builder autoAlignX(final PopUpDialog.Builder db) {
            /*
            * Anchor sanity check:
            * Only apply X alignment if element is left-anchored
            * */
            switch (element.getAnchor()) {
                case LEFT_TOP, LEFT_CENTRAL, LEFT_BOTTOM ->
                        element.setX(db.elementX(columnIndex, columns));
            }

            return this;
        }

        /**
         * To be invoked after {@link #row} has been specified.
         * */
        public Builder autoAlignY(final PopUpDialog.Builder db) {
            /*
            * Anchor sanity check:
            * Only apply Y alignment if element is top-anchored
            * */
            switch (element.getAnchor()) {
                case LEFT_TOP, CENTRAL_TOP, RIGHT_TOP ->
                    element.setY(db.elementY(row));
            }

            return this;
        }

        public DialogElement build() {
            return new DialogElement(columnIndex, columns, row, element);
        }
    }
}
