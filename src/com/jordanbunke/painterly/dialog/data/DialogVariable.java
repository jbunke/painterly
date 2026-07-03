package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;

import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogVariable<T> {
    public final Validator<T> validator;
    public final Supplier<T> defaultValueGetter;
    public final boolean resetOnSoft;

    private T value;

    public DialogVariable(
            final Supplier<T> defaultValueGetter,
            final boolean resetOnSoft,
            final Validator<T> validator
    ) {
        this.defaultValueGetter = defaultValueGetter;
        this.resetOnSoft = resetOnSoft;
        this.validator = validator;

        value = defaultValueGetter.get();
    }

    public DialogVariable(
            final Supplier<T> defaultValueGetter,
            final Validator<T> validator
    ) {
        this(defaultValueGetter, true, validator);
    }

    public DialogVariable() {
        this(() -> null, Validator::nonNull);
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

    public void set(final T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
