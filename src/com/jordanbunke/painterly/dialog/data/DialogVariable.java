package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.painterly.dialog.visual.DialogElement;
import com.jordanbunke.painterly.dialog.visual.PopUpDialog;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;

import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogVariable<T> {
    public final Validator<T> validator;
    public final Supplier<T> defaultValueGetter;
    public final boolean resetOnSoft;
    public final VariableUIAssembler<T> assembler;

    private T value;

    public DialogVariable(
            final Supplier<T> defaultValueGetter,
            final boolean resetOnSoft,
            final Validator<T> validator,
            final VariableUIAssembler<T> assembler
    ) {
        this.defaultValueGetter = defaultValueGetter;
        this.resetOnSoft = resetOnSoft;
        this.validator = validator;
        this.assembler = assembler;

        value = defaultValueGetter.get();
    }

    public DialogVariable(
            final Supplier<T> defaultValueGetter,
            final Validator<T> validator,
            final VariableUIAssembler<T> assembler
    ) {
        this(defaultValueGetter, true, validator, assembler);
    }

    @SuppressWarnings("unused")
    public DialogVariable() {
        this(() -> null, Validator::nonNull, VariableUIAssembler::blank);
    }

    public void reset() {
        set(defaultValueGetter.get());
    }

    public boolean exists() {
        return value != null;
    }

    public boolean passing() {
        return validator.check(value).a();
    }

    public String feedback() {
        final ResourceCode code = validator.check(value).b();

        return code == RC_NA ? "" : LanguageData.retrieveTooltip(code);
    }

    public DialogElement[] assemble(
            final int row, final PopUpDialog.Builder db
    ) {
        return assembler.assemble(row, this, db);
    }

    public void set(final T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
