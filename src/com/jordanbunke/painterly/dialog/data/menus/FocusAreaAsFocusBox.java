package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.delta_time.utility.math.Pair;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.VariableUIAssembler;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.function.Function;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.ProjectUtils.*;

public final class FocusAreaAsFocusBox extends DialogVariableSet {
    private static final FocusAreaAsFocusBox INSTANCE;

    public final DialogVariable<Integer>
            divsLeft, divsRight, divsAbove, divsBelow;

    static {
        INSTANCE = new FocusAreaAsFocusBox();
    }

    private FocusAreaAsFocusBox() {
        divsLeft = new DialogVariable<>(wrapGetter(
                p -> p.focusManager.maxDivsToLeft(), 0),
                this::validDivsLeft,
                VariableUIAssembler.assembleTextbox(
                        RC_DIALOG_TX_DIVS_LEFT,
                        Validator::nullableParseInt,
                        b -> b.setWidthRelative(0.4)));
        divsRight = new DialogVariable<>(wrapGetter(
                p -> p.focusManager.maxDivsToRight(), 0),
                this::validDivsRight,
                VariableUIAssembler.assembleTextbox(
                        RC_DIALOG_TX_DIVS_RIGHT,
                        Validator::nullableParseInt,
                        b -> b.setWidthRelative(0.4)));
        divsAbove = new DialogVariable<>(wrapGetter(
                p -> p.focusManager.maxDivsAbove(), 0),
                this::validDivsAbove,
                VariableUIAssembler.assembleTextbox(
                        RC_DIALOG_TX_DIVS_ABOVE,
                        Validator::nullableParseInt,
                        b -> b.setWidthRelative(0.4)));
        divsBelow = new DialogVariable<>(wrapGetter(
                p -> p.focusManager.maxDivsBelow(), 0),
                this::validDivsBelow,
                VariableUIAssembler.assembleTextbox(
                        RC_DIALOG_TX_DIVS_BELOW,
                        Validator::nullableParseInt,
                        b -> b.setWidthRelative(0.4)));
    }

    public static FocusAreaAsFocusBox get() {
        return INSTANCE;
    }

    @Override
    public DialogVariable<?>[] getAllVariables() {
        return new DialogVariable[] {
                divsLeft, divsRight, divsAbove, divsBelow
        };
    }

    @Override
    void whenReady() {
        doIfHasProject(p ->
                p.focusManager.focusAreaAsNewFocusBox(
                        divsLeft.get(), divsRight.get(),
                        divsAbove.get(), divsBelow.get())).run();
    }

    // validators

    private Pair<Boolean, ResourceCode> validDivsLeft(final Integer divsLeft) {
        return genericValidDivs(divsLeft,
                p -> p.focusManager.maxDivsToLeft(),
                RC_DIALOG_FB_FF_LEFT_TOO_HIGH);
    }

    private Pair<Boolean, ResourceCode> validDivsRight(final Integer divsRight) {
        return genericValidDivs(divsRight,
                p -> p.focusManager.maxDivsToRight(),
                RC_DIALOG_FB_FF_RIGHT_TOO_HIGH);
    }

    private Pair<Boolean, ResourceCode> validDivsAbove(final Integer divsAbove) {
        return genericValidDivs(divsAbove,
                p -> p.focusManager.maxDivsAbove(),
                RC_DIALOG_FB_FF_ABOVE_TOO_HIGH);
    }

    private Pair<Boolean, ResourceCode> validDivsBelow(final Integer divsBelow) {
        return genericValidDivs(divsBelow,
                p -> p.focusManager.maxDivsBelow(),
                RC_DIALOG_FB_FF_BELOW_TOO_HIGH);
    }

    private Pair<Boolean, ResourceCode> genericValidDivs(
            final Integer divs, final Function<Project, Integer> maxFunction,
            final ResourceCode maxBoundFeedbackCode
    ) {
        final int maxDivs = wrapGetter(maxFunction, 0).get();

        if (divs == null)
            return new Pair<>(false, RC_DIALOG_FB_CANNOT_READ_INT);
        if (divs < 0)
            return new Pair<>(false, RC_DIALOG_FB_MUST_BE_GR_EQ_0);
        else if (divs > maxDivs)
            return new Pair<>(false, maxBoundFeedbackCode);

        return new Pair<>(true, RC_NA);
    }

    // resource variable accessors

    public String raMaxDivsLeft() {
        return String.valueOf(wrapGetter(p ->
                p.focusManager.maxDivsToLeft(), 0).get());
    }

    public String raMaxDivsRight() {
        return String.valueOf(wrapGetter(p ->
                p.focusManager.maxDivsToRight(), 0).get());
    }

    public String raMaxDivsAbove() {
        return String.valueOf(wrapGetter(p ->
                p.focusManager.maxDivsAbove(), 0).get());
    }

    public String raMaxDivsBelow() {
        return String.valueOf(wrapGetter(p ->
                p.focusManager.maxDivsBelow(), 0).get());
    }
}
