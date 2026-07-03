package com.jordanbunke.painterly.menu.elements.textbox;

import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.ext.AbstractTextbox;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.theme.Graphics;
import com.jordanbunke.painterly.util.ProgramFont;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.util.Layout.*;

public class Textbox extends AbstractTextbox {
    private final boolean unconditionalSender;

    static {
        setTypingCode(Constants.TYPING_CODE);
    }

    Textbox(
            final Coord2D position, final int width, final Anchor anchor,
            final String prefix, final String initialText, final String suffix,
            final Predicate<String> textValidator, final boolean unconditionalSender,
            final Consumer<String> setter, final int maxLength
    ) {
        super(position, new Bounds2D(width, TEXT_BUTTON_DEF_HEIGHT),
                anchor, () -> prefix, initialText, () -> suffix,
                textValidator, setter, Graphics::drawTextbox, maxLength);

        this.unconditionalSender = unconditionalSender;
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted())
            Cursor.ping(Cursor.TEXT);
    }

    @Override
    protected void attemptSend() {
        if (unconditionalSender)
            send();
        else
            super.attemptSend();
    }

    public static class Builder implements MenuElementBuilder<Textbox> {
        private final Coord2D position;

        private int width;
        private ProgramFont font;
        private Anchor anchor;

        private String prefix;
        private String initialText;
        private String suffix;

        private Predicate<String> textValidator;
        private boolean unconditionalSender;
        private Consumer<String> setter;
        private Supplier<String> getter;

        private int maxLength;

        Builder(final Coord2D position) {
            this.position = position;

            width = TEXTBOX_DEF_WIDTH;
            font = ProgramFont.FONT_DEF;
            anchor = Anchor.LEFT_TOP;

            prefix = "";
            initialText = "";
            suffix = "";

            textValidator = s -> true;
            unconditionalSender = false;
            setter = s -> {
            };
            getter = null;

            maxLength = Constants.TEXTBOX_DEF_MAX_LENGTH;
        }

        public Builder setWidth(final int width) {
            this.width = width;
            return this;
        }

        public Builder setWidthRelative(final double ratio) {
            width = (int)(TEXTBOX_DEF_WIDTH * ratio);
            return this;
        }

        public Builder setFont(final ProgramFont font) {
            this.font = font;
            return this;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setInitialText(final String initialText) {
            this.initialText = initialText;
            return this;
        }

        public Builder setPrefix(final String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder setSuffix(final String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder setMaxLength(final int maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder setGetter(final Supplier<String> getter) {
            this.getter = getter;
            return this;
        }

        public Builder setSetter(final Consumer<String> setter) {
            this.setter = setter;
            return this;
        }

        public Builder setTextValidator(final Predicate<String> textValidator) {
            this.textValidator = textValidator;
            return this;
        }

        public <T> Builder setDialogVariableEndpoint(
                final DialogVariable<T> variable,
                final Function<String, T> parser
        ) {
            return setDialogVariableEndpoint(variable, parser, false, true, true);
        }

        public <T> Builder setDialogVariableEndpoint(
                final DialogVariable<T> variable,
                final Function<String, T> parser,
                final boolean dynamic, final boolean unconditionalSender,
                final boolean setInitialText
        ) {
            this.unconditionalSender = unconditionalSender;

            setter = s -> variable.set(parser.apply(s));
            textValidator = s -> variable.validator.check(parser.apply(s)).a();

            if (dynamic)
                getter = () -> String.valueOf(variable.get());

            if (setInitialText && variable.passing())
                setInitialText(String.valueOf(variable.get()));

            return this;
        }

        public Builder setUnconditionalSender(final boolean unconditionalSender) {
            this.unconditionalSender = unconditionalSender;
            return this;
        }

        @Override
        public Textbox build() {
            if (getter == null) {
                // not dynamic
                return new Textbox(position, width, anchor,
                        prefix, initialText, suffix,
                        textValidator, unconditionalSender, setter, maxLength);
            } else {
                // dynamic
                return new DynamicTextbox(position, width, anchor,
                        prefix, suffix, textValidator, unconditionalSender,
                        getter, setter, maxLength);
            }
        }
    }
}
