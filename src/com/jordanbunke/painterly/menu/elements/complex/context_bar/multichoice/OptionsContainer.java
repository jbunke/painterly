package com.jordanbunke.painterly.menu.elements.complex.context_bar.multichoice;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.util.EnumUtils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.painterly.util.Layout.*;

public final class OptionsContainer extends MenuElementContainer {
    private final OptionButton<?>[] elements;
    final Runnable postExecution;

    private OptionsContainer(
            final Coord2D position, final Bounds2D dimensions,
            final Anchor anchor, final IAction<?>[] actions,
            final Runnable postExecution
    ) {
        super(position, dimensions, anchor, true);

        this.postExecution = postExecution;

        final Coord2D renderPos = getRenderPosition();
        final int width = dimensions.width();

        elements = IntStream.range(0, actions.length).mapToObj(i -> {
            final Coord2D elementPos = renderPos.displace(0,
                    i * TEXT_BUTTON_DEF_HEIGHT);
            return new OptionButton<>(elementPos, width, actions[i], this);
        }).toArray(OptionButton[]::new);
    }

    public static Builder init(final Coord2D position) {
        return new Builder(position);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return elements;
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    public static class Builder implements MenuElementBuilder<OptionsContainer> {
        private final Coord2D position;

        private Anchor anchor;

        private IAction<?>[] actions;
        private Runnable postExecution;

        public Builder(final Coord2D position) {
            this.position = position;

            anchor = Anchor.LEFT_TOP;

            actions = new IAction[0];
            postExecution = () -> {};
        }

        public Builder setAnchor(final Anchor anchor) {
            this.anchor = anchor;
            return this;
        }

        public Builder setPostExecution(final Runnable postExecution) {
            this.postExecution = postExecution;
            return this;
        }

        public <T extends Enum<T>> Builder setActionsFromEnum(
                final Class<T> enumClass,
                final Function<T, IAction<?>> actionGetter
        ) {
            actions = EnumUtils.stream(enumClass)
                    .map(actionGetter)
                    .toArray(IAction[]::new);
            return this;
        }

        public Builder setActions(final IAction<?>... actions) {
            this.actions = actions;
            return this;
        }

        @Override
        public OptionsContainer build() {
            final int width = Arrays.stream(actions)
                    .mapToInt(IAction::getWidthAllotment)
                    .reduce(1, Math::max),
                    height = Math.max(1, actions.length * TEXT_BUTTON_DEF_HEIGHT);

            return new OptionsContainer(position, new Bounds2D(width, height),
                    anchor, actions, postExecution);
        }
    }
}
