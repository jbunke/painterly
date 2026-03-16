package com.jordanbunke.painterly.menu.elements.text_button;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.*;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class SimpleTextButton extends MenuButton implements TextButton {
    private final String label, tooltip;
    private final Alignment alignment;
    private final ButtonType buttonType;

    private final GameImage base, highlight;

    public SimpleTextButton(
            final String label, final String tooltip,
            final Coord2D position,
            final Bounds2D dimensions,
            final Anchor anchor, final Alignment alignment,
            final ButtonType buttonType, final Runnable behaviour
    ) {
        super(position, dimensions, anchor, true, behaviour);

        this.label = label;
        this.tooltip = tooltip;
        this.alignment = alignment;
        this.buttonType = buttonType;

        base = Graphics.drawTextButton(sim(false, false));
        highlight = Graphics.drawTextButton(sim(false, true));
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @Override
    public ButtonType getButtonType() {
        return buttonType;
    }

    @Override
    public void process(InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
            Tooltip.get().ping(tooltip, mousePos);
            Cursor.ping(Cursor.POINTER);
        }
    }

    @Override
    public void setHighlighted(final boolean highlighted) {
        final boolean hover = highlighted && Locks.canHover();
        super.setHighlighted(hover);

        if (hover)
            Locks.hover();
    }

    @Override
    public void update(final double deltaTime) {}

    @Override
    public void render(final GameImage canvas) {
        draw(isHighlighted() ? highlight : base, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static Builder init(
            final ResourceCode code, final Coord2D position,
            final Runnable behaviour
    ) {
        return new Builder(code, position, behaviour);
    }

    public static class Builder implements MenuElementBuilder<SimpleTextButton> {
        private static final int DEF_WIDTH = 0;

        // explicit def necessary, final
        private final ResourceCode code;
        private final Runnable behaviour;

        // explicit def necessary
        private Coord2D position;

        // optional, has default if not defined
        private int width;
        private int height;
        private ResourceCode tooltipCode;
        private Anchor anchor;
        private Alignment alignment;
        private ButtonType buttonType;

        Builder(
                final ResourceCode code, final Coord2D position,
                final Runnable behaviour
        ) {
            this.code = code;
            this.position = position;
            this.behaviour = behaviour;

            width = DEF_WIDTH;
            height = Layout.TEXT_BUTTON_DEF_HEIGHT;
            tooltipCode = RC_NA;
            anchor = Anchor.LEFT_TOP;
            alignment = Alignment.CENTER;
            buttonType = ButtonType.STANDARD;
        }

        public Builder setPosition(final Coord2D position) {
            this.position = position;
            return this;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setAlignment(final Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder setButtonType(final ButtonType buttonType) {
            this.buttonType = buttonType;
            return this;
        }

        public Builder setHeight(final int height) {
            this.height = height;
            return this;
        }

        public Builder setTooltipCode(final ResourceCode tooltipCode) {
            this.tooltipCode = tooltipCode;
            return this;
        }

        public Builder setWidth(final int width) {
            this.width = width;
            return this;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public SimpleTextButton build() {
            final String label = LanguageData.retrieveUIText(code),
                    tooltip = Tooltip.resolve(tooltipCode);
            final int width = this.width == DEF_WIDTH
                    ? Graphics.naiveButtonWidth(label)
                    : this.width;

            return new SimpleTextButton(label, tooltip,
                    position, new Bounds2D(width, height),
                    anchor, alignment, buttonType, behaviour);
        }
    }
}
