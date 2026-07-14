package com.jordanbunke.painterly.menu.elements.complex.logic;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.InvisibleMenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.ThinkingMenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;

import java.util.function.Supplier;

public final class EnumMenuElement extends InvisibleMenuElement {
    private final ThinkingMenuElement process;
    private final MenuElement[] outcomes;

    public EnumMenuElement(
            final Supplier<MenuElement> process, final MenuElement... outcomes
    ) {
        this.process = new ThinkingMenuElement(process);
        this.outcomes = outcomes;
    }

    @Override
    public void incrementX(final int deltaX) {
        for (MenuElement outcome : outcomes)
            outcome.incrementX(deltaX);
    }

    @Override
    public void incrementY(final int deltaY) {
        for (MenuElement outcome : outcomes)
            outcome.incrementY(deltaY);
    }

    @Override
    public void setPosition(final Coord2D position) {
        for (MenuElement outcome : outcomes)
            outcome.setPosition(position);
    }

    @Override
    public void setX(final int x) {
        for (MenuElement outcome : outcomes)
            outcome.setX(x);
    }

    @Override
    public void setY(final int y) {
        for (MenuElement outcome : outcomes)
            outcome.setY(y);
    }

    @Override
    public void setDimensions(final Bounds2D dimensions) {
        for (MenuElement outcome : outcomes)
            outcome.setDimensions(dimensions);
    }

    @Override
    public void setWidth(final int width) {
        for (MenuElement outcome : outcomes)
            outcome.setWidth(width);
    }

    @Override
    public void setHeight(final int height) {
        for (MenuElement outcome : outcomes)
            outcome.setHeight(height);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        process.process(eventLogger);
    }

    @Override
    public void render(final GameImage canvas) {
        process.render(canvas);
    }

    @Override
    public void update(double deltaTime) {
        process.update(deltaTime);
    }
}
