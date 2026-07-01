package com.jordanbunke.painterly.util.debug;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.EnumUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jordanbunke.painterly.util.Layout.ScreenBox.PROJECT_VIEWPORT;

public final class LogManager {
    private static final Map<LogChannel, Boolean> channelMap;
    private static final List<LogMessage> messageLog;
    private static boolean globalOff;

    static {
        globalOff = false;
        channelMap = new HashMap<>();
        messageLog = new LinkedList<>();

        EnumUtils.stream(LogChannel.class)
                .forEach(c -> channelMap.put(c, c.defaultValue));
    }

    public static void toggleChannelStatus(final LogChannel channel) {
        setChannelStatus(channel, !isChannelActive(channel));
    }

    public static void setChannelStatus(
            final LogChannel channel, final boolean status
    ) {
        channelMap.put(channel, status);
    }

    public static void setGlobalOff(final boolean globalOff) {
        LogManager.globalOff = globalOff;

        if (LogManager.globalOff)
            messageLog.clear();
    }

    public static boolean isChannelActive(final LogChannel channel) {
        return channelMap.getOrDefault(channel, false);
    }

    public static boolean isGlobalOff() {
        return globalOff;
    }

    public static void log(final LogMessage message) {
        if (channelMap.getOrDefault(message.channel, false))
            messageLog.add(0, message);
    }

    public static void update() {
        if (globalOff)
            return;

        messageLog.forEach(LogMessage::update);

        final List<LogMessage> remove = messageLog.stream()
                .filter(m -> m.getAge() >= Constants.DEBUG_MESSAGE_FRAMES)
                .toList();
        messageLog.removeAll(remove);
    }

    public static void render(final GameImage canvas) {
        if (globalOff)
            return;

        final GameImage[] images = messageLog.stream()
                .map(LogMessage::getImage)
                .toArray(GameImage[]::new);
        final Coord2D tr = PROJECT_VIEWPORT.at(1d, 0d);
        int y = tr.y;

        for (GameImage image : images) {
            final int x = tr.x - image.getWidth();
            canvas.draw(image, x, y);
            y += image.getHeight();
        }
    }
}
