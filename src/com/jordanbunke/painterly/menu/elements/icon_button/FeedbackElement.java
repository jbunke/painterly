package com.jordanbunke.painterly.menu.elements.icon_button;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.Tooltip;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Graphics.readIcon;
import static com.jordanbunke.painterly.util.Layout.ICON_DIM;

public final class FeedbackElement extends MenuElement {
    private static final GameImage VALID, INVALID;

    private final DialogVariable<?> variable;
    private boolean valid;

    static {
        VALID = readIcon(RC_FEEDBACK_VALID);
        INVALID = readIcon(RC_FEEDBACK_INVALID);
    }

    private FeedbackElement(
            final Coord2D position, final Anchor anchor,
            final DialogVariable<?> variable
    ) {
        super(position, new Bounds2D(ICON_DIM, ICON_DIM), anchor, true);

        this.variable = variable;

        update(0d);
    }

    public static Builder init(
            final Coord2D position, final DialogVariable<?> variable
    ) {
        return new Builder(position, variable);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();

        if (mouseIsWithinBounds(mousePos)) {
            // TODO - potential ? cursor
            Tooltip.get().ping(variable.feedback(), mousePos);
        }
    }

    @Override
    public void update(final double deltaTime) {
        valid = variable.passing();
    }

    @Override
    public void render(final GameImage canvas) {
        draw(valid ? VALID : INVALID, canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static class Builder implements MenuElementBuilder<FeedbackElement> {
        private final Coord2D position;
        private final DialogVariable<?> variable;

        private Anchor anchor;

        public Builder(
                final Coord2D position, final DialogVariable<?> variable
        ) {
            this.position = position;
            this.variable = variable;

            anchor = Anchor.LEFT_TOP;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        @Override
        public FeedbackElement build() {
            return new FeedbackElement(position, anchor, variable);
        }
    }
}
