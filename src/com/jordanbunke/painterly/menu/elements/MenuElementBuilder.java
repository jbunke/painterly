package com.jordanbunke.painterly.menu.elements;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;

@FunctionalInterface
public interface MenuElementBuilder<T extends MenuElement> {
    T build();
}
