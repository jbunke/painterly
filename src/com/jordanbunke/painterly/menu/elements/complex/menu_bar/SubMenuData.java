package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.painterly.events.actions.GlobalAction;
import com.jordanbunke.painterly.events.actions.ProjectAction;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Constants;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;

public final class SubMenuData implements ISubMenuEntry {
    public final ResourceCode code;
    public final ISubMenuEntry[] entries;
    public final int[] separators;

    private ResourceCode parent;

    private SubMenuData(
            final ResourceCode code, final ISubMenuEntry[] entries,
            final int[] separators
    ) {
        this.code = code;
        this.entries = entries;
        this.separators = separators;

        parent = RC_NA;
    }

    public static Builder init(final ResourceCode code) {
        return new Builder(code);
    }

    @Override
    public int getWidthAllotment() {
        int width = 0;

        final String text = LanguageData.retrieveUIText(code);

        // initial padding
        width += MENU_BAR_PADDING_X;

        // icon allotment; does not depend on whether there is a valid icon code
        width += ICON_DIM;

        // between icon and text
        width += MENU_BAR_PADDING_X;

        // text
        width += standardTextWidth(text);

        // text to expander divider
        width += MENU_BAR_DIVIDER_WIDTH;

        // expander
        width += standardTextWidth(Constants.NESTED_MENU_BAR_EXPANDER);

        // final padding
        width += MENU_BAR_PADDING_X;

        return width;
    }

    public Bounds2D getContentBounds() {
        final int width = Arrays.stream(entries)
                .mapToInt(ISubMenuEntry::getWidthAllotment)
                .reduce(1, Math::max),
                height = entries.length * TEXT_BUTTON_DEF_HEIGHT;
        return new Bounds2D(width, height);
    }

    public static class Builder {
        private final ResourceCode code;

        private final List<ISubMenuEntry> entries;
        private final List<Integer> separators;

        Builder(final ResourceCode code) {
            this.code = code;

            entries = new LinkedList<>();
            separators = new LinkedList<>();
        }

        public Builder addSeparator() {
            if (!entries.isEmpty() && !separators.contains(entries.size()))
                separators.add(entries.size());

            return this;
        }

        public Builder addNestedSubMenu(final SubMenuData subMenu) {
            entries.add(subMenu);
            subMenu.parent = code;

            return this;
        }

        public Builder addProjectAction(final ProjectAction action) {
            entries.add(action);
            return this;
        }

        public Builder addGlobalAction(final GlobalAction action) {
            entries.add(action);
            return this;
        }

        public SubMenuData build() {
            return new SubMenuData(code,
                    entries.toArray(ISubMenuEntry[]::new),
                    separators.stream().mapToInt(i -> i).toArray());
        }
    }
}
