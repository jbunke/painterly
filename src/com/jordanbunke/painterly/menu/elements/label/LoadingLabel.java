package com.jordanbunke.painterly.menu.elements.label;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.invisible.InvisibleMenuElement;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Constants;

import static com.jordanbunke.painterly.util.Layout.ScreenBox.SCREEN;

public final class LoadingLabel extends InvisibleMenuElement {
    private static final int MIN_P = 1, MAX_P = 3;
    private int periods, ticks;

    private final String baseText;
    private final DynamicLabel dynamicLabel;

    public LoadingLabel(final ResourceCode code) {
        periods = MIN_P;
        ticks = 0;

        baseText = LanguageData.retrieveUIText(code);

        dynamicLabel = DynamicLabel.init(
                SCREEN.at(0.5, 0.5), this::updateText)
                .setAnchor(Anchor.CENTRAL)
                .setWidestCase(baseText + ".".repeat(MAX_P))
                .build();
    }

    private String updateText() {
        ticks++;

        if (ticks >= Constants.LOADING_LABEL_TICKS) {
            ticks = 0;
            periods++;

            if (periods > MAX_P)
                periods = MIN_P;
        }

        return baseText + ".".repeat(periods);
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        dynamicLabel.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        dynamicLabel.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        dynamicLabel.render(canvas);
    }
}
