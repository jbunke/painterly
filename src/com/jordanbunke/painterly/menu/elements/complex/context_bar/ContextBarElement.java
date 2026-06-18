package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Layout;
import com.jordanbunke.painterly.util.Tooltip;

import static com.jordanbunke.painterly.util.Graphics.drawContextBarElement;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.CONTEXT_BAR;

public final class ContextBarElement extends MenuButtonStub
        implements TextButton {
    private final MenuElement expansion;
    private final boolean expandable;

    private final ResourceCode textCode;
    // TODO - ResourceCode supplier for icon
    private final Alignment alignment;

    private final String tooltip;

    private boolean expanded;

    private String label;
    private GameImage base, highlight, selected;

    private ContextBarElement(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor,
            final MenuElement expansion,
            final ResourceCode textCode, final Alignment alignment,
            final String tooltip
    ) {
        super(position, dimensions, anchor, true);

        this.expansion = expansion;
        expandable = expansion != null;

        this.tooltip = tooltip;

        this.textCode = textCode;
        this.alignment = alignment;

        label = LanguageData.retrieveUIText(textCode);

        highlight = GameImage.dummy();
        selected = GameImage.dummy();
        updateAssets();
    }

    public static Builder init(final ResourceCode textCode, final int x) {
        return new Builder(textCode, x);
    }

    public void collapse() {
        expanded = false;
    }

    @Override
    public boolean isSelected() {
        return expanded;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (mouseIsWithinBounds(mousePos)) {
            Tooltip.get().ping(tooltip, mousePos);

            if (expandable)
                Cursor.ping(Cursor.POINTER);
        }

        if (expanded)
            expansion.process(eventLogger);
    }

    @Override
    public void execute() {
        expanded = expandable && !expanded;

        if (expandable)
            ContextBar.get().collapseOthers(this);
    }

    @Override
    public void update(final double deltaTime) {
        checkForTextUpdate();

        if (expanded)
            expansion.update(deltaTime);
    }

    private void checkForTextUpdate() {
        final String label = LanguageData.retrieveUIText(textCode);

        if (!this.label.equals(label)) {
            this.label = label;
            updateAssets();
        }
    }

    private void updateAssets() {
        base = drawContextBarElement(sim(false, false));

        if (expandable) {
            highlight = drawContextBarElement(sim(false, true));
            selected = drawContextBarElement(sim(true, false));
        }
    }

    @Override
    public void render(final GameImage canvas) {
        draw(resolveImage(), canvas);

        if (expanded)
            expansion.render(canvas);
    }

    private GameImage resolveImage() {
        if (!expandable)
            return base;

        return isSelected() ? selected : (isHighlighted() ? highlight : base);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

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
        return expandable ? ButtonType.STANDARD : ButtonType.STUB;
    }

    public int nextX() {
        return getRenderPosition().x + getWidth();
    }

    public static class Builder implements MenuElementBuilder<ContextBarElement> {
        private final ResourceCode textCode;
        private final Coord2D position;

        private Bounds2D dimensions;
        private Anchor anchor;

        private ResourceCode tooltipCode;
        private MenuElement expansion;

        private Alignment alignment;

        private Builder(final ResourceCode textCode, final int x) {
            this.textCode = textCode;
            position = new Coord2D(x, CONTEXT_BAR.y.get());

            dimensions = new Bounds2D(
                    Layout.defaultContextBarElementWidth(),
                    CONTEXT_BAR.height.get());
            anchor = Anchor.LEFT_TOP;

            tooltipCode = ResourceCode.RC_NA;
            expansion = null;

            alignment = Alignment.CENTER;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setWidth(final int width) {
            dimensions = new Bounds2D(width, CONTEXT_BAR.height.get());
            return this;
        }

        public Builder setExpansion(final MenuElement expansion) {
            this.expansion = expansion;
            return this;
        }

        public Builder setTooltipCode(final ResourceCode tooltipCode) {
            this.tooltipCode = tooltipCode;
            return this;
        }

        public Builder setAlignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        @Override
        public ContextBarElement build() {
            final String tooltip = Tooltip.resolve(tooltipCode);

            // TODO

            return new ContextBarElement(position, dimensions, anchor,
                    expansion, textCode, alignment, tooltip);
        }

        // GETTER

        public Coord2D getPosition() {
            return position;
        }

        // HELPER

        public Anchor complementaryReflected() {
            return switch (anchor) {
                case LEFT_TOP -> Anchor.LEFT_BOTTOM;
                case CENTRAL_TOP -> Anchor.CENTRAL_BOTTOM;
                case RIGHT_TOP -> Anchor.RIGHT_BOTTOM;
                default -> anchor;
            };
        }
    }
}
