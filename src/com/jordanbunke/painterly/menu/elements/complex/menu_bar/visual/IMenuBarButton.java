package com.jordanbunke.painterly.menu.elements.complex.menu_bar.visual;

import com.jordanbunke.delta_time.utility.math.Coord2D;

// TODO - remove class
@Deprecated
public interface IMenuBarButton {
    Coord2D getRenderPosition();
    int getWidth();
    Coord2D rightOf();
    Coord2D below();

//    default boolean isChildOf(IMenuBarButton ancestorCandidate) {
//        final IMenuBarButton parent = getParent();
//
//        if (parent == null)
//            return false;
//        else if (ancestorCandidate.equals(parent))
//            return true;
//
//        return parent.isChildOf(ancestorCandidate);
//    }
}
