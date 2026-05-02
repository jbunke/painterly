package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.dialog.data.DialogVariable;

public abstract class AllVarsInDialog {
    public boolean validate() {
        for (DialogVariable<?> variable : getAllVariables())
            if (!variable.passing())
                return false;

        return true;
    }

    abstract DialogVariable<?>[] getAllVariables();
}
