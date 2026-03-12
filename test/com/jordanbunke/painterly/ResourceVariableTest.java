package com.jordanbunke.painterly;

import com.jordanbunke.painterly.resources.lang.LanguageData;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public class ResourceVariableTest {
    public static void main(String[] args) {
        final String out = LanguageData.retrieveTooltip(RC_INTERVAL_PROGRESS);
        System.out.println(out);
    }
}
