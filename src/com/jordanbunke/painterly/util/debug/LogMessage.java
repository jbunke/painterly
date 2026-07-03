package com.jordanbunke.painterly.util.debug;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.theme.ThemeManager;

public final class LogMessage {
    public final LogChannel channel;
    private final GameImage image;

    private int age;

    public LogMessage(final LogChannel channel, final ResourceCode code) {
        this.channel = channel;
        final String text = LanguageData.retrieveUIText(code);
        image = ThemeManager.get().drawDebugMessage(text);

        age = 0;
    }

    public void update() {
        age++;
    }

    public int getAge() {
        return age;
    }

    public GameImage getImage() {
        return image;
    }
}
