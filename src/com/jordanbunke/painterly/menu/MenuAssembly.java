package com.jordanbunke.painterly.menu;

import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.menu.elements.icon_button.IconButton;

import static com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.*;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu mainMenu() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - temporary

        final IconButton test = IconButton.init(
                RC_TEMP, SCREEN.at(0.5, 0.5), Painterly::quitProgram
        ).setAnchor(CENTRAL).build();
        mb.add(test);

        // TODO

        return mb.build();
    }
}
