package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.dialog.data.DialogVariable;

import java.util.Arrays;

public abstract class DialogVariableSet {
    public final boolean validate() {
        for (DialogVariable<?> variable : getAllVariables())
            if (!variable.passing())
                return false;

        return true;
    }

    public final void softReset() {
        reset(false);
    }

    public final void hardReset() {
        reset(true);
    }

    private void reset(final boolean hard) {
        Arrays.stream(getAllVariables())
                .filter(v -> hard || v.resetOnSoft)
                .forEach(DialogVariable::reset);
    }

    public abstract DialogVariable<?>[] getAllVariables();

    protected void executionApparatus(final Runnable whenReady) {
        whenReady.run();
    }

    abstract void whenReady();

    public void ok() {
        executionApparatus(this::whenReady);
    }
}
