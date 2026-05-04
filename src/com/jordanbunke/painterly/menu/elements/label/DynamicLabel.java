package com.jordanbunke.painterly.menu.elements.label;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractDynamicLabel;
import com.jordanbunke.delta_time.menu.menu_elements.ext.drawing_functions.LabelDrawingFunction;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.ProgramFont;

import java.awt.*;
import java.util.function.Supplier;

public final class DynamicLabel extends AbstractDynamicLabel {
    public DynamicLabel(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final Color textColor,
            final Supplier<String> getter, final LabelDrawingFunction fDraw
    ) {
        super(position, dimensions, anchor, textColor, getter, fDraw);
    }

    public static Builder init(
            final Coord2D position, final Supplier<String> getter
    ) {
        return new Builder(position, getter);
    }

    public static class Builder implements MenuElementBuilder<DynamicLabel> {
        private final Coord2D position;
        private final Supplier<String> getter;

        private String widestCase;

        private Anchor anchor;
        private Color color;
        private double textSize;
        private ProgramFont font;
        private Text.Orientation orientation;

        Builder(final Coord2D position, final Supplier<String> getter) {
            this.position = position;
            this.getter = getter;

            anchor = Anchor.LEFT_TOP;
            color = Colors.systemColor(Colors.SystemColor.DARK);
            textSize = 1.0;
            font = ProgramFont.FONT_DEF;
            orientation = Text.Orientation.CENTER;

            widestCase = "x".repeat(100);
        }

        public Builder setFont(final ProgramFont font) {
            this.font = font;
            return this;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public Builder setOrientation(final Text.Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setTextSize(final double textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setWidestCase(final String widestCase) {
            this.widestCase = widestCase;
            return this;
        }

        @Override
        public DynamicLabel build() {
            final LabelDrawingFunction fDraw =
                    (s, c) -> font.getBuilder(textSize, orientation, c)
                            .addText(s).build().draw();
            final GameImage wcImage = fDraw.draw(widestCase, color);

            return new DynamicLabel(position,
                    new Bounds2D(wcImage.getWidth(), wcImage.getHeight()),
                    anchor, color, getter, fDraw);
        }
    }
}
