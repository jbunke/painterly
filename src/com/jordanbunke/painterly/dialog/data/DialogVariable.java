package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogVariable<T> {
    private final T defaultValue;
    private final Validator<T> validator;
    private T value;

    public DialogVariable(
            final T defaultValue,
            final Validator<T> validator
    ) {
        this.defaultValue = defaultValue;
        this.validator = validator;
        value = defaultValue;
    }

    public DialogVariable() {
        this(null, Validator::nonNull);
    }

    public boolean passing() {
        return validator.check(value).a();
    }

    public String feedback() {
        final ResourceCode code = validator.check(value).b();

        return code == RC_NA ? "" : LanguageData.retrieveUIText(code);
    }

    public void set(final T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
