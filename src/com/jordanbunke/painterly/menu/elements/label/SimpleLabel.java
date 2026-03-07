package com.jordanbunke.painterly.menu.elements.label;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.Colors;
import com.jordanbunke.painterly.util.ProgramFont;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.UnaryOperator;

public final class SimpleLabel extends StaticMenuElement {
    private SimpleLabel(
            final Coord2D position,
            final Anchor anchor,
            final GameImage image
    ) {
        super(position, anchor, image);
    }

    public static Builder init(final Coord2D position, final String text) {
        return new Builder(position, text);
    }

    public static class Builder implements MenuElementBuilder<SimpleLabel> {
        private final Coord2D position;

        private final List<UnaryOperator<TextBuilder>> instructions;

        private Anchor anchor;
        private Color color;
        private double textSize;
        private ProgramFont font;
        private Text.Orientation orientation;

        Builder(final Coord2D position, final String text) {
            this.position = position;

            instructions = new LinkedList<>();
            instructions.add(tb -> tb.addText(text));

            anchor = Anchor.LEFT_TOP;
            color = Colors.systemColor(Colors.SystemColor.DARK);
            textSize = 1.0;
            font = ProgramFont.FONT_DEF;
            orientation = Text.Orientation.CENTER;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setColor(final Color color) {
            this.color = color;
            return this;
        }

        public Builder setTextSize(final double textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setFont(final ProgramFont font) {
            this.font = font;
            return this;
        }

        public Builder setOrientation(final Text.Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setText(final String text) {
            instructions.clear();
            instructions.add(tb -> tb.addText(text));
            return this;
        }

        public Builder setInstructions(final UnaryOperator<TextBuilder> instructions) {
            this.instructions.clear();
            this.instructions.add(instructions);
            return this;
        }

        public Builder addInstruction(final UnaryOperator<TextBuilder> instruction) {
            this.instructions.add(instruction);
            return this;
        }

        @Override
        public SimpleLabel build() {
            final TextBuilder tb = font.getBuilder(textSize, orientation, color);

            for (UnaryOperator<TextBuilder> instruction : instructions)
                instruction.apply(tb);

            final GameImage image = tb.build().draw();

            return new SimpleLabel(position, anchor, image);
        }
    }
}
