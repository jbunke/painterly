package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.fonts.Font;
import com.jordanbunke.delta_time.fonts.FontBuilder;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.text.TextBuilder;

import java.awt.*;
import java.nio.file.Path;

import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;

public enum ProgramFont {
    FONT_DEF("deltan", 3, 0.6, 0.6);

    private final Font font;
    private final double lineSpacing;

    ProgramFont(
            final String baseName, final int pixelSpacing,
            final double lineSpacing, final double whitespaceMult
    ) {
        final Path FOLDER = Path.of("font");
        final FontBuilder builder = new FontBuilder()
                .setPixelSpacing(pixelSpacing)
                .setWhitespaceBreadthMultiplier(whitespaceMult);

        font = builder.build(FOLDER, true, baseName);
        this.lineSpacing = lineSpacing;
    }

    public TextBuilder getBuilder(
            final double textSize, final Text.Orientation orientation,
            final Color color
    ) {
        return new TextBuilder(textSize,
                lineSpacing, orientation, color, font);
    }

    public static class FontFormatter {
        private ProgramFont font;
        private double textSize, lineSpacing;
        private Color color;
        private Text.Orientation orientation;

        public FontFormatter(final ProgramFont font) {
            this.font = font;

            lineSpacing = font.lineSpacing;
            textSize = 1.0;
            color = systemColor(DARK);
            orientation = Text.Orientation.CENTER;
        }

        public FontFormatter setFont(
                final ProgramFont font,
                final boolean overrideLineSpacing
        ) {
            this.font = font;

            if (overrideLineSpacing)
                lineSpacing = font.lineSpacing;

            return this;
        }

        public FontFormatter setTextSize(final double textSize) {
            this.textSize = textSize;
            return this;
        }

        public FontFormatter setOrientation(final Text.Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        public FontFormatter setColor(Color color) {
            this.color = color;
            return this;
        }

        public FontFormatter setLineSpacing(final double lineSpacing) {
            this.lineSpacing = lineSpacing;
            return this;
        }

        public TextBuilder realize() {
            return new TextBuilder(textSize, lineSpacing, orientation,
                    color, font.font);
        }
    }
}
