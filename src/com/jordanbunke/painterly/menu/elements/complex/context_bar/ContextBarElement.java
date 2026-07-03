package com.jordanbunke.painterly.menu.elements.complex.context_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.theme.Theme;
import com.jordanbunke.painterly.theme.ThemeManager;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Layout;
import com.jordanbunke.painterly.util.Tooltip;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.util.Layout.ScreenBox.CONTEXT_BAR;

public final class ContextBarElement extends MenuButtonStub
        implements TextButton {
    public final ContextBarSection section;

    private final MenuElement expansion;
    private final boolean expandable, requiresProject;

    private final ResourceCode textCode, tooltipCode;
    private final Supplier<ResourceCode> iconCodeGetter;
    private final Alignment alignment;

    private boolean expanded;

    private String label;
    private ResourceCode iconCode;
    private GameImage base, highlight, selected;

    private ContextBarElement(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final ContextBarSection section,
            final MenuElement expansion, final boolean requiresProject,
            final ResourceCode textCode, final Alignment alignment,
            final ResourceCode tooltipCode,
            final Supplier<ResourceCode> iconCodeGetter
    ) {
        super(position, dimensions, anchor, true);

        this.section = section;

        this.expansion = expansion;
        expandable = expansion != null;
        this.requiresProject = expandable && requiresProject;

        this.tooltipCode = tooltipCode;

        this.textCode = textCode;
        this.alignment = alignment;

        this.iconCodeGetter = iconCodeGetter;

        label = LanguageData.retrieveUIText(textCode);
        iconCode = iconCodeGetter.get();

        highlight = GameImage.dummy();
        selected = GameImage.dummy();
        updateAssets();
    }

    public static Builder init(
            final ContextBarSection section,
            final ResourceCode textCode, final int x
    ) {
        return new Builder(section, textCode, x);
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
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        final boolean mouseInBounds = mouseIsWithinBounds(mousePos),
                hasProject = ProjectManager.get().hasProject(),
                projectConditionsSatisfied = !requiresProject || hasProject;

        // tooltip and cursor
        if (mouseInBounds) {
            Tooltip.get().pingCode(tooltipCode, mousePos);

            if (expandable && (projectConditionsSatisfied || expanded))
                Cursor.ping(Cursor.POINTER);
        }

        // highlight
        setHighlighted(mouseInBounds && expandable && projectConditionsSatisfied);

        // element click processing
        if (mouseInBounds && expandable &&
                (projectConditionsSatisfied || expanded)) {
            final List<GameEvent> unprocessed = eventLogger.getUnprocessedEvents();
            for (GameEvent e : unprocessed) {
                if (e instanceof GameMouseEvent mouseEvent &&
                        mouseEvent.matchesAction(GameMouseEvent.Action.DOWN)) {
                    mouseEvent.markAsProcessed();
                    execute();
                    return;
                }
            }
        }

        // expansion processing handoff
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
        checkForAssetUpdate();

        if (expanded)
            expansion.update(deltaTime);
    }

    private void checkForAssetUpdate() {
        final String label = LanguageData.retrieveUIText(textCode);
        final ResourceCode iconCode = iconCodeGetter.get();

        if (!(this.label.equals(label) && this.iconCode == iconCode)) {
            this.label = label;
            this.iconCode = iconCode;
            updateAssets();
        }
    }

    private void updateAssets() {
        final Theme theme = ThemeManager.get();

        base = theme.drawContextBarElement(
                sim(false, false), iconCode);

        if (expandable) {
            highlight = theme.drawContextBarElement(
                    sim(false, true), iconCode);
            selected = theme.drawContextBarElement(
                    sim(true, false), iconCode);
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
        return getRenderPosition().x + getWidth() +
                /* TODO - test */ Layout.CONTEXT_BAR_GAP_X;
    }

    public static class Builder implements MenuElementBuilder<ContextBarElement> {
        private final ContextBarSection section;
        private final ResourceCode textCode;
        private final Coord2D position;

        private Bounds2D dimensions;
        private Anchor anchor;

        private ResourceCode tooltipCode;
        private Supplier<ResourceCode> iconCodeGetter;
        private MenuElement expansion;
        private boolean requiresProject;

        private Alignment alignment;

        private Builder(
                final ContextBarSection section,
                final ResourceCode textCode, final int x
        ) {
            this.section = section;
            this.textCode = textCode;
            position = new Coord2D(x, CONTEXT_BAR.y.get());

            dimensions = new Bounds2D(
                    Layout.defaultContextBarElementWidth(),
                    CONTEXT_BAR.height.get());
            anchor = Anchor.LEFT_TOP;

            tooltipCode = ResourceCode.RC_NA;
            iconCodeGetter = () -> ResourceCode.RC_NA;
            expansion = null;
            requiresProject = true;

            alignment = Alignment.LEFT;
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setWidth(final int width) {
            dimensions = new Bounds2D(width, CONTEXT_BAR.height.get());
            return this;
        }

        public Builder setWidthFromPercentage(final double percentage) {
            return setWidth(CONTEXT_BAR.ofWidth(percentage));
        }

        public Builder setExpansion(final MenuElement expansion) {
            this.expansion = expansion;
            return this;
        }

        public Builder setRequiresProject(final boolean requiresProject) {
            this.requiresProject = requiresProject;
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

        public Builder setIconCodeGetter(
                final Supplier<ResourceCode> iconCodeGetter
        ) {
            this.iconCodeGetter = iconCodeGetter;
            return this;
        }

        public Builder setIconCodeGetter(
                final Function<Project, ResourceCode> iconCodeGetter,
                final ResourceCode failCase
        ) {
            return setIconCodeGetter(() -> {
                final Project p = ProjectManager.get().getProject();

                if (p == null)
                    return failCase;

                return iconCodeGetter.apply(p);
            });
        }

        public Builder setStaticIconCode(final ResourceCode iconCode) {
            return setIconCodeGetter(() -> iconCode);
        }

        @Override
        public ContextBarElement build() {
            return new ContextBarElement(position, dimensions, anchor,
                    section, expansion, requiresProject, textCode,
                    alignment, tooltipCode, iconCodeGetter);
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
