package com.jordanbunke.painterly.menu.elements.icon_button;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.SimpleToggleMenuButton;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Graphics;
import com.jordanbunke.painterly.util.Tooltip;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.jordanbunke.painterly.util.Layout.*;

public final class IconOptionsButton extends SimpleToggleMenuButton {
    private final ResourceCode[] tooltipCodes;

    public IconOptionsButton(
            final Coord2D position, final Anchor anchor,
            final GameImage[] bases, final ResourceCode[] tooltipCodes,
            final Runnable[] behaviours, final Supplier<Integer> indexFunction,
            final Runnable global
    ) {
        super(position, new Bounds2D(ICON_DIM, ICON_DIM), anchor, true,
                bases, Arrays.stream(bases).map(Graphics::highlightIcon)
                        .toArray(GameImage[]::new),
                behaviours, indexFunction, global);

        this.tooltipCodes = tooltipCodes;
    }

    public static Builder init(
            final Coord2D position, final ResourceCode... resourceCodes
    ) {
        return new Builder(position, resourceCodes);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        super.process(eventLogger);

        if (isHighlighted()) {
            final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
            Tooltip.get().pingCode(getTooltipCode(), mousePos);
            Cursor.ping(Cursor.POINTER);
        }
    }

    private ResourceCode getTooltipCode() {
        return tooltipCodes[getIndex()];
    }

    public static class Builder implements MenuElementBuilder<IconOptionsButton> {
        private final Coord2D position;
        private final ResourceCode[] iconCodes;

        private Anchor anchor;

        private ResourceCode[] tooltipCodes;
        private Runnable[] behaviours;
        private Supplier<Integer> indexFunction;
        private Runnable global;

        public Builder(
                final Coord2D position, final ResourceCode... iconCodes
        ) {
            this.position = position;
            this.iconCodes = iconCodes;

            anchor = Anchor.LEFT_TOP;

            tooltipCodes = null;
            behaviours = null;
            indexFunction = () -> 0;
            global = () -> {};
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setBehaviours(final Runnable... behaviours) {
            this.behaviours = behaviours;
            return this;
        }

        public Builder setTooltipCodes(final ResourceCode... tooltipCodes) {
            this.tooltipCodes = tooltipCodes;
            return this;
        }

        public Builder copyTooltipCodesFromIconCodes() {
            return setTooltipCodes(iconCodes);
        }

        public Builder setIndexFunction(final Supplier<Integer> indexFunction) {
            this.indexFunction = indexFunction;
            return this;
        }

        public Builder setGlobal(final Runnable global) {
            this.global = global;
            return this;
        }

        @Override
        public IconOptionsButton build() {
            final GameImage[] bases = Arrays.stream(iconCodes)
                    .map(Graphics::readIcon).toArray(GameImage[]::new);

            if (tooltipCodes == null)
                tooltipCodes = IntStream.range(0, iconCodes.length)
                        .mapToObj(i -> ResourceCode.RC_NA)
                        .toArray(ResourceCode[]::new);

            if (behaviours == null)
                behaviours = IntStream.range(0, iconCodes.length)
                        .mapToObj(i -> (Runnable) () -> {})
                        .toArray(Runnable[]::new);

            return new IconOptionsButton(position, anchor, bases,
                    tooltipCodes, behaviours, indexFunction, global);
        }
    }
}
