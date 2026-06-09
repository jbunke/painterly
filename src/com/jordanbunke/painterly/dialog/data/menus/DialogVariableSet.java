package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.visual.DialogManager;

public abstract class DialogVariableSet {
    public boolean validate() {
        for (DialogVariable<?> variable : getAllVariables())
            if (!variable.passing())
                return false;

        return true;
    }

    abstract DialogVariable<?>[] getAllVariables();

    protected void executionApparatus(final Runnable whenReady) {
        whenReady.run();
    }

    abstract void whenReady();

    public void ok() {
        executionApparatus(this::whenReady);
        DialogManager.close();
    }
}
