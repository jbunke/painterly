package com.jordanbunke.painterly.menu.elements.complex.menu_bar;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.jordanbunke.painterly.events.actions.GlobalAction.*;
import static com.jordanbunke.painterly.events.actions.ProjectAction.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class MenuBar extends MenuElement {
    private static MenuBar INSTANCE;

    private final SubMenu[] subMenus;

    static {
        INSTANCE = build();
    }

    private MenuBar(final SubMenu[] subMenus) {
        super(new Coord2D(), new Bounds2D(1, 1), Anchor.LEFT_TOP, false);

        this.subMenus = subMenus;
    }

    public static MenuBar get() {
        return INSTANCE;
    }

    private static MenuBar build() {
        final Builder b = new Builder();

        b.addSubMenu(
                SubMenuData.init(RC_NAV_PROGRAM)
                        .addGlobalAction(MAIN_MENU)
                        .addGlobalAction(QUIT_PROGRAM)
                        .build())
                .addSubMenu(SubMenuData.init(RC_NAV_PROJECT)
                        .addGlobalAction(NEW_PROJECT)
                        .addGlobalAction(OPEN_PROJECT)
                        .addProjectAction(DUPLICATE_PROJECT)
                        .addSeparator()
                        .addProjectAction(SAVE)
                        .addProjectAction(SAVE_AS)
                        .addProjectAction(EDIT_PROJECT_SETTINGS)
                        .addProjectAction(EXPORT)
                        .build())
                .addSubMenu(SubMenuData.init(RC_NAV_EDIT)
                        .addProjectAction(TOGGLE_SIM)
                        .addSeparator()
                        .addProjectAction(CLEAR_FOCUS_BOXES)
                        .addProjectAction(FOCUS_BOX_AS_FOCUS_AREA)
                        .addProjectAction(RESET_FOCUS_AREA)
                        .addProjectAction(DELETE_ACTIVE_BOUNDS)
                        .addSeparator()
                        .addProjectAction(TOGGLE_TICK_MODE)
                        .build())
                .addSubMenu(SubMenuData.init(RC_NAV_VIEW)
                        .addGlobalAction(TOGGLE_FULLSCREEN)
                        .addSeparator()
                        .addProjectAction(TOGGLE_SOURCE)
                        .addProjectAction(RESET_POSITIONING)
                        .addProjectAction(FIT_TO_FOCUS_AREA)
                        .addSeparator()
                        .addGlobalAction(UPDATE_CHANNEL_STATUS)
                        .addGlobalAction(TOGGLE_LOG_GLOBAL_OFF)
                        .addGlobalAction(TOGGLE_RECENT_STROKES_VIS)
                        .addGlobalAction(TOGGLE_FPS_INDICATOR)
                        // TODO
//                        .addSeparator()
//                        .addGlobalAction(TOGGLE_FPS_INDICATOR)
//                        .addGlobalAction(TOGGLE_RECENT_STROKE_VISUALIZATION)
                        .build());

        return b.build();
    }

    public static void regen() {
        INSTANCE = build();
    }

    public void collapseAll() {
        for (SubMenu subMenu : subMenus)
            subMenu.collapse();
    }

    public int endX() {
        return subMenus[subMenus.length - 1].nextX();
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        for (SubMenu subMenu : subMenus)
            subMenu.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        for (SubMenu subMenu : subMenus)
            subMenu.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        for (SubMenu subMenu : subMenus)
            subMenu.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    private static class Builder implements MenuElementBuilder<MenuBar> {
        private final List<SubMenuData> subMenus;

        private Builder() {
            subMenus = new LinkedList<>();
        }

        public Builder addSubMenu(final SubMenuData subMenu) {
            subMenus.add(subMenu);
            return this;
        }

        @Override
        public MenuBar build() {
            final SubMenu[] subs = new SubMenu[subMenus.size()];
            int x = 0;

            for (int i = 0; i < subs.length; i++) {
                final SubMenu subMenu = new SubMenu(
                        new Coord2D(x, 0), subMenus.get(i), null);
                x = subMenu.nextX();
                subs[i] = subMenu;
            }

            return new MenuBar(subs);
        }
    }
}
