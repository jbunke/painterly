package com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.Arrays;

import static com.jordanbunke.painterly.util.Layout.TEXT_BUTTON_DEF_HEIGHT;

public final class SubMenu extends MenuElement {
    private final SubMenu parent;
    private final MenuBarButton button;
    private final SubMenuContent content;

    private boolean expanded;

    public SubMenu(
            final Coord2D position, final SubMenuData data, final SubMenu parent
    ) {
        super(parent == null
                ? position.displaceY(TEXT_BUTTON_DEF_HEIGHT)
                : position.displaceX(parent.getWidth()),
                data.getContentBounds(), Anchor.LEFT_TOP, false);

        button = parent == null
                ? SubMenuButton.at(position, data.code, this)
                : NestedSubMenuButton.at(position, data.code, this, parent.getWidth());
        content = SubMenuContent.at(getPosition(), data, this);

        this.parent = parent;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        button.process(eventLogger);

        if (expanded)
            content.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        button.update(deltaTime);

        if (expanded)
            content.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        button.render(canvas);

        if (expanded)
            content.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public SubMenu getParent() {
        return parent;
    }

    public int nextX() {
        return button.getRenderPosition().x + button.getWidth();
    }

    public void expand() {
        expanded = true;

        if (parent != null)
            parent.expand();
    }

    public void collapse() {
        expanded = false;

        Arrays.stream(content.getMenuElements())
                .filter(SubMenu.class::isInstance)
                .map(SubMenu.class::cast)
                .forEach(SubMenu::collapse);
    }

    @Override
    public String toString() {
        return button.getLabel();
    }
}
