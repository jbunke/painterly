package com.jordanbunke.painterly.menu.elements.complex.project_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.events.GameEvent;
import com.jordanbunke.delta_time.events.GameMouseEvent;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.button.MenuButtonStub;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.icon_button.IconButton;
import com.jordanbunke.painterly.menu.elements.icon_button.IconOptionsButton;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.Alignment;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.util.Cursor;
import com.jordanbunke.painterly.util.Tooltip;

import java.util.List;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.MENU_BAR;

public final class ProjectButton extends MenuButtonStub implements TextButton {
    private final int index;
    private final Project project;

    private final IconButton closeButton;
    private final IconOptionsButton toggleSimButton;

    private boolean active;

    private final String label, tooltip;
    private final GameImage base, highlight, selected;

    private ProjectButton(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final int index
    ) {
        super(position, dimensions, anchor, true);

        this.index = index;
        project = ProjectManager.get().getProjectAt(index);

        updateActive();

        final Coord2D tl = getRenderPosition(), tr = tl.displaceX(getWidth());
        final int iconBuffer = (getHeight() - ICON_DIM) / 2;

        final String name = project.getName();
        label = determineLabel();
        tooltip = label.equals(name) ? Tooltip.NONE : name;

        closeButton = IconButton
                .init(RC_CLOSE_PROJECT,
                        tr.displace(-iconBuffer, iconBuffer),
                        () -> ProjectManager.get().closeProject(index))
                .setAnchor(Anchor.RIGHT_TOP)
                .setTooltipCode(RC_CLOSE_PROJECT)
                .build();

        toggleSimButton = IconOptionsButton
                .init(tl.displace(iconBuffer, iconBuffer),
                        RC_SIM_RESUME, RC_SIM_PAUSE)
                .copyTooltipCodesFromIconCodes()
                .setGlobal(project::toggleSimulation)
                .setIndexFunction((() -> project.isPainting() ? 1 : 0))
                .build();

        base = drawProjectButton(sim(false, false));
        highlight = drawProjectButton(sim(false, true));
        selected = drawProjectButton(sim(true, false));
    }

    public static Builder init(final int x, final int width, final int index) {
        return new Builder(x, width, index);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        closeButton.process(eventLogger);

        if (active)
            toggleSimButton.process(eventLogger);

        processButton(eventLogger);
    }

    private void processButton(final InputEventLogger eventLogger) {
        final Coord2D mousePos = eventLogger.getAdjustedMousePosition();
        final boolean mouseInBounds = mouseIsWithinBounds(mousePos);

        setHighlighted(mouseInBounds && !isSelected());

        if (mouseInBounds && Tooltip.get().isBlankForFrame())
            Tooltip.get().ping(tooltip, mousePos);

        if (isHighlighted()) {
            Cursor.ping(Cursor.POINTER);

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
    }

    @Override
    public void execute() {
        ProjectManager.get().setActiveProject(index);
    }

    @Override
    public void update(final double deltaTime) {
        updateActive();

        closeButton.update(deltaTime);

        if (active)
            toggleSimButton.update(deltaTime);
    }

    private void updateActive() {
        active = project.equals(ProjectManager.get().getProject());
    }

    @Override
    public void render(final GameImage canvas) {
        draw(isSelected() ? selected : (isHighlighted() ? highlight : base),
                canvas);

        closeButton.render(canvas);

        if (active)
            toggleSimButton.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    @Override
    public String getLabel() {
        return label;
    }

    private String determineLabel() {
        final int widthAllotment = getWidth() -
                ((3 * MENU_BAR_PADDING_X) + (2 * ICON_DIM));

        final String name = project.getName();
        final int l = name.length();
        String label = name;
        int removed = 0,
                width = SimpleLabel.initLiteral(label, new Coord2D())
                        .build().getWidth();

        while (width > widthAllotment && removed + 1 < l) {
            removed++;

            label = name.substring(0, l - removed) + ".".repeat(3);
            width = SimpleLabel.initLiteral(label, new Coord2D())
                    .build().getWidth();
        }

        return label;
    }

    @Override
    public Alignment getAlignment() {
        return Alignment.LEFT;
    }

    @Override
    public ButtonType getButtonType() {
        return ButtonType.STANDARD;
    }

    @Override
    public boolean isSelected() {
        return active;
    }

    public static class Builder implements MenuElementBuilder<ProjectButton> {
        private final int x, width, index;

        public Builder(final int x, final int width, final int index) {
            this.x = x;
            this.width = width;
            this.index = index;
        }

        @Override
        public ProjectButton build() {
            return new ProjectButton(new Coord2D(x, MENU_BAR.y.get()),
                    new Bounds2D(width, TEXT_BUTTON_DEF_HEIGHT),
                    Anchor.LEFT_TOP, index);
        }
    }
}
