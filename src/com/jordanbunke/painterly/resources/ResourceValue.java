package com.jordanbunke.painterly.resources;

import com.jordanbunke.painterly.resources.lang.LanguageData;

public final class ResourceValue {
    private final boolean isString;
    private final String ifString;
    private final ResourceCode ifRC;

    private ResourceValue(
            final boolean isString, final String ifString, final ResourceCode ifRC
    ) {
        this.isString = isString;
        this.ifString = ifString;
        this.ifRC = ifRC;
    }

    public static ResourceValue ofString(final String value) {
        return new ResourceValue(true, value, null);
    }

    public static ResourceValue ofRC(final ResourceCode value) {
        return new ResourceValue(false, null, value);
    }

    public String retrieve() {
        return isString ? ifString : LanguageData.retrieveValue(ifRC);
    }
}
