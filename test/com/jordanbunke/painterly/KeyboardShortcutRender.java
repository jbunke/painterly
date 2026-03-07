package com.jordanbunke.painterly;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.events.actions.GlobalAction;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.events.actions.ProjectAction;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.Graphics;

import java.util.Comparator;
import java.util.stream.Stream;

public class KeyboardShortcutRender {
    public static void main(final String[] args) {
        final GameImage[] shortcutImages =
                Stream.concat(EnumUtils.stream(GlobalAction.class),
                        EnumUtils.stream(ProjectAction.class))
                .map(IAction::getShortcut)
                .sorted(Comparator.comparing(KeyboardShortcut::toString))
                .map(Graphics::drawKeyboardShortcut).toArray(GameImage[]::new);
        System.out.println(shortcutImages.length);
    }
}
