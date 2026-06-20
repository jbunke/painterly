package com.jordanbunke.painterly.util;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.ResourceLoader;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.menu.elements.Button;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.ProgramFont.FontFormatter;

import java.awt.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;

import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.*;

public final class Graphics {
    private static final Path ICONS_FOLDER = Path.of("icons"),
            CURSORS_FOLDER = Path.of("cursors");

    // TODO

    // IO

    public static GameImage readIcon(final ResourceCode code) {
        final Path iconFile = ICONS_FOLDER.resolve(code.id() + ".png");
        return ResourceLoader.loadImageResource(iconFile);
    }

    public static GameImage readCursor(final Cursor cursor) {
        final Path cursorFile = CURSORS_FOLDER.resolve(
                cursor.id() + ".png");
        return ResourceLoader.loadImageResource(cursorFile);
    }

    // UI ELEMENTS

    public static GameImage drawDialogBackground(
            final int width, final int height
    ) {
        // TODO - temp implementation

        final GameImage image = new GameImage(width, height);
        final Color bgColor = systemColor(LIGHT),
                topBarColor = systemColor(MID),
                borderColor = systemColor(MID_DARK);

        // background
        image.fill(bgColor);

        // top bar
        image.fillRectangle(topBarColor, 0, 0, width, dialogTitleStripeHeight());

        // border
        image.drawRectangle(borderColor, 4f, 0, 0, width, height);

        return image.submit();
    }

    public static int naiveButtonWidth(final String label) {
        final GameImage textImage = new FontFormatter(FONT_DEF)
                .setColor(systemColor(DARK)).realize()
                .addText(label).build().draw();

        return textImage.getWidth() + TEXT_BUTTON_PADDING_X;
    }

    public static GameImage drawContextBarElement(final TextButton tb) {
        // TODO - temp
        final Color textColor, bgColor, accentColor;

        if (tb.isSelected()) {
            bgColor = systemColor(LIGHT);
            accentColor = systemColor(MID_DARK);
            textColor = systemColor(DARK);
        } else if (tb.isHighlighted()) {
            bgColor = systemColor(MID_DARK);
            accentColor = systemColor(MID_LIGHT);
            textColor = systemColor(LIGHT);
        } else if (tb.getButtonType() == ButtonType.STUB) {
            bgColor = systemColor(DARK);
            accentColor = systemColor(MID_DARK);
            textColor = systemColor(MID_LIGHT);
        } else {
            bgColor = systemColor(DARK);
            accentColor = systemColor(MID);
            textColor = systemColor(LIGHT);
        }

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        final GameImage textImage = new FontFormatter(FONT_DEF).realize()
                .setColor(textColor).addText(tb.getLabel()).build().draw();

        // background
        button.fill(bgColor);

        // draw text
        // TODO
        final int x = switch (tb.getAlignment()) {
            // TODO - subject to whether there is an icon?
            case LEFT -> MENU_BAR_PADDING_X + ICON_DIM + MENU_BAR_PADDING_X;
            case CENTER -> (w - textImage.getWidth()) / 2;
            case RIGHT -> w - (TEXT_BUTTON_TEXT_OFFSET_X + textImage.getWidth());
        };

        // TODO
        button.draw(textImage, x, TEXT_BUTTON_TEXT_OFFSET_Y);

        // border
        button.drawLine(accentColor, 4f, 0, 0, 0, h);
        button.drawLine(accentColor, 4f, w, 0, w, h);

        return button.submit();
    }

    public static GameImage drawSubMenuHeader(final TextButton tb) {
        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        drawSubMenuButton(button, tb, false);

        final GameImage expanderImage = new FontFormatter(FONT_DEF)
                .realize().setColor(systemColor(LIGHT))
                .addText(Constants.NESTED_MENU_BAR_EXPANDER).build().draw();

        final int textX = tb.getWidth() -
                (MENU_BAR_PADDING_X + expanderImage.getWidth());
        button.draw(expanderImage, textX, TEXT_BUTTON_TEXT_OFFSET_Y);

        return button.submit();
    }

    public static <T> GameImage drawActionMenuButton(
            final TextButton tb, final boolean stub,
            final IAction<T> action
    ) {
        final KeyboardShortcut shortcut = action.getShortcut();

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        drawSubMenuButton(button, tb, stub);

        // TODO - icon

        if (shortcut != null) {
            final GameImage shortcutImage = drawKeyboardShortcut(shortcut);
            final int shortcutX = button.getWidth() -
                    (MENU_BAR_PADDING_X + shortcutImage.getWidth()),
                    shortcutY = (button.getHeight() - shortcutImage.getHeight()) / 2;
            button.draw(shortcutImage, shortcutX, shortcutY);
        }

        return button.submit();
    }

    /**
     * For common elements in sub-menu expanders and action buttons
     * */
    private static void drawSubMenuButton(
            final GameImage button, final TextButton tb,
            final boolean stub
    ) {
        // TODO - temp

        final boolean highlight = tb.isHighlighted();

        final Color textColor, bgColor;

        textColor = stub ? systemColor(MID_LIGHT) : systemColor(LIGHT);
        bgColor = highlight ? systemColor(MID) : systemColor(DARK);

        button.fill(bgColor);

        final GameImage textImage = new FontFormatter(FONT_DEF).realize()
                .setColor(textColor).addText(tb.getLabel()).build().draw();

        final int textX = MENU_BAR_PADDING_X + ICON_DIM + MENU_BAR_PADDING_X;
        button.draw(textImage, textX, TEXT_BUTTON_TEXT_OFFSET_Y);

        // TODO - highlight underline?
    }

    public static GameImage drawTextButton(final TextButton tb) {
        // TODO - temp implementation

        final ButtonType type = tb.getButtonType();
        final boolean highlight = tb.isHighlighted();

        final Color textColor, bgColor, accentColor;

        switch (type) {
            case STUB -> {
                bgColor = transparent();
                accentColor = systemColor(MID_DARK);
                textColor = systemColor(MID_DARK);
            }
            default -> {
                bgColor = systemColor(highlight ? MID_DARK : DARK);
                accentColor = systemColor(highlight ? MID_LIGHT : MID);
                textColor = systemColor(LIGHT);
            }
        }

        final GameImage textImage = new FontFormatter(FONT_DEF).realize()
                .setColor(textColor).addText(tb.getLabel()).build().draw();
        final GameImage button = new GameImage(tb.getWidth(), tb.getHeight());

        final int w = button.getWidth(), h = button.getHeight();

        // background
        button.fill(bgColor);

        // draw text
        final int x = switch (tb.getAlignment()) {
            case LEFT -> TEXT_BUTTON_TEXT_OFFSET_X;
            case CENTER -> (w - textImage.getWidth()) / 2;
            case RIGHT -> w - (TEXT_BUTTON_TEXT_OFFSET_X + textImage.getWidth());
        };

        button.draw(textImage, x, TEXT_BUTTON_TEXT_OFFSET_Y);

        // border
        button.drawRectangle(accentColor, 4f, 0, 0, w, h);

        return button.submit();
    }

    public static GameImage drawTextbox(
            final Bounds2D dims,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        // TODO - temp implementation; copied from TDSM

        // pre-processing
        final int left = Math.min(cursorIndex, selectionIndex),
                right = Math.max(cursorIndex, selectionIndex),
                INC = TEXTBOX_SEG_INC;

        final boolean hasSelection = left != right,
                cursorAtRight = cursorIndex == right;

        // setup
        final Color
                mainColor = valid ? systemColor(DARK) : invalidText(),
                backgroundColor = valid ? systemColor(LIGHT) : invalidTextBG(),
                outlineColor = typing ? highlightOverlay() :
                        (highlighted ? systemColor(LIGHT) : mainColor),
                affixColor = shiftRGB(mainColor, 0x40),
                highlightOverlay = highlightOverlay();

        // text and cursor

        final String preSel = text.substring(0, left),
                sel = text.substring(left, right),
                postSel = text.substring(right);

        final FontFormatter formatter = new FontFormatter(FONT_DEF);

        final GameImage prefixImage = formatter.realize()
                .setColor(affixColor).addText(prefix).build().draw(),
                suffixImage = formatter.realize()
                        .setColor(affixColor)
                        .addText(suffix).build().draw(),
                preSelImage = formatter.realize()
                        .setColor(mainColor)
                        .addText(preSel).build().draw(),
                selImage = formatter.realize()
                        .setColor(mainColor)
                        .addText(sel).build().draw(),
                postSelImage = formatter.realize()
                        .setColor(mainColor)
                        .addText(postSel).build().draw();

        final GameImage box = new GameImage(dims.width(), dims.height());

        // background
        box.fill(backgroundColor);

        Coord2D textPos = new Coord2D(TEXT_BUTTON_TEXT_OFFSET_X,
                TEXT_BUTTON_TEXT_OFFSET_Y);

        // possible prefix
        box.draw(prefixImage, textPos.x, textPos.y);
        if (!prefix.isEmpty())
            textPos = textPos.displace(prefixImage.getWidth() + INC, 0);

        // main text prior to possible selection
        box.draw(preSelImage, textPos.x, textPos.y);
        if (!preSel.isEmpty())
            textPos = textPos.displace(preSelImage.getWidth() + INC, 0);

        // possible selection text
        if (hasSelection) {
            if (!cursorAtRight)
                textPos = textPos.displace(2 * INC, 0);

            box.draw(selImage, textPos.x, textPos.y);
            box.fillRectangle(highlightOverlay, textPos.x - INC, 0,
                    selImage.getWidth() + (2 * INC), box.getHeight());
            textPos = textPos.displace(selImage.getWidth() + INC, 0);
        }

        // cursor
        box.fillRectangle(mainColor,
                textPos.x - (cursorAtRight ? 0
                        : selImage.getWidth() + (3 * INC)),
                0, INC, box.getHeight());
        if (cursorAtRight)
            textPos = textPos.displace(2 * INC, 0);

        // main text following possible selection
        box.draw(postSelImage, textPos.x, textPos.y);
        if (!postSel.isEmpty())
            textPos = textPos.displace(postSelImage.getWidth() + INC, 0);

        // possible suffix
        box.draw(suffixImage, textPos.x, textPos.y);

        // outline
        box.drawRectangle(outlineColor, 2f, 0, 0,
                box.getWidth(), box.getHeight());

        return box.submit();
    }

    public static GameImage drawVertScrollBar(
            final int w, final int h, final int barH,
            final int barY, final Button b
    ) {
        // TODO - copied from TDSM -- review

        final GameImage scrollSpace = new GameImage(w, h),
                scrollBar = new GameImage(w, barH);

        final Color c = b.outcomes(systemColor(LIGHT),
                systemColor(MID_LIGHT), systemColor(MID_DARK)),
                accent = b.outcomes(systemColor(MID_LIGHT),
                        systemColor(MID_DARK), systemColor(DARK));

        scrollBar.fill(c);
        scrollBar.drawLine(accent, 1f, 0, barH - 2, w, barH - 2);
        scrollBar.drawRectangle(systemColor(DARK), 1f, 0, 0, w - 1, barH - 1);
        // TODO - clearCorners(scrollBar);

        scrollSpace.draw(scrollBar, 0, barY);

        return scrollSpace.submit();
    }

    public static GameImage drawKeyboardShortcut(final KeyboardShortcut shortcut) {
        final String[] stringArray = shortcut.asStringArray();

        return Arrays.stream(stringArray).map(Graphics::drawKey)
                .reduce((a, b) -> {
                    // here
                    final int bX = a.getWidth() + KEY_SHORTCUT_INTERVAL_X,
                            w = bX + b.getWidth(), h = a.getHeight();

                    final GameImage combined = new GameImage(w, h);
                    combined.draw(a);
                    combined.draw(b, bX, 0);

                    return combined.submit();
                }).orElse(GameImage.dummy());
    }

    public static GameImage drawKey(final String keyAsString) {
        // TODO - temp implementation

        final Color textColor = systemColor(LIGHT),
                backgroundColor = systemColor(DARK),
                accentColor = systemColor(MID_DARK);

        final GameImage textImage = new FontFormatter(FONT_DEF)
                .setTextSize(1.0)
                .setColor(textColor).realize().addText(keyAsString)
                .build().draw();

        final int w = textImage.getWidth() + (2 * KEY_SHORTCUT_TEXT_MARGIN_X),
                h = textImage.getHeight() + KEY_SHORTCUT_DROP_SHADOW;
        final GameImage key = new GameImage(w, h);

        key.fill(backgroundColor);

        int shadowY = h - KEY_SHORTCUT_DROP_SHADOW;
        key.fillRectangle(accentColor, 0, shadowY, w, KEY_SHORTCUT_DROP_SHADOW);

        // round out drop shadow
        keyShadowCurve(key, accentColor);

        // clear corners
        clearKeyCorners(key);

        key.draw(textImage, KEY_SHORTCUT_TEXT_MARGIN_X, 0);

        return key.submit();
    }

    private static void keyShadowCurve(
            final GameImage key, final Color accentColor
    ) {
        final int w = key.getWidth(), h = key.getHeight(),
                margin = KEY_SHORTCUT_SHADOW_MARGIN_X;

        for (int x = 0; x < margin; x++) {
            final int extraH = (int)(Math.pow((margin - x) / (double)margin, 3.) * KEY_SHORTCUT_DROP_SHADOW_EXTRA),
                    y = h - (KEY_SHORTCUT_DROP_SHADOW + extraH),
                    x2 = w - (x + 1);

            key.fillRectangle(accentColor, x, y, 1, extraH);
            key.fillRectangle(x2, y, 1, extraH);
        }
    }

    private static void clearKeyCorners(final GameImage key) {
        final int w = key.getWidth(), h = key.getHeight(),
                margin = KEY_SHORTCUT_CORNER_MARGIN_X;

        for (int x = 0; x < margin; x++) {
            final int ys = (int)(Math.pow((margin - x) / (double)margin, 3.) * KEY_SHORTCUT_MAX_CLEARED),
                    x2 = w - (x + 1);

            final int transparent = transparent().getRGB();
            for (int y = 0; y < ys; y++) {
                final int y2 = h - (y + 1);

                key.setRGB(x, y, transparent);
                key.setRGB(x2, y, transparent);
                key.setRGB(x, y2, transparent);
                key.setRGB(x2, y2, transparent);
            }
        }
    }

    // TODO

    // ADDITIONAL UI

    public static GameImage drawTooltip(final String text) {
        final Color textColor = systemColor(DARK);
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> new FontFormatter(FONT_DEF).realize()
                        .setColor(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
        final int ls = lines.length,
                w = Arrays.stream(lineImages)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(systemColor(MID_LIGHT));

        for (int l = 0; l < ls; l++) {
            final GameImage line = lineImages[l];
            final int x = (w - line.getWidth()) / 2,
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        return tooltip.submit();
    }

    public static GameImage drawMenuBarSeparator(final int width) {
        final GameImage sepImage =
                new GameImage(width, MENU_BAR_SEPARATOR_HEIGHT);
        sepImage.fill(systemColor(LIGHT));
        return sepImage.submit();
    }

    public static int standardTextWidth(final String text) {
        return new FontFormatter(FONT_DEF).realize().addText(text)
                .build().draw().getWidth();
    }

    // ALGORITHMS

    public static GameImage highlightIcon(
            final GameImage icon
    ) {
        final int w = icon.getWidth(), h = icon.getHeight();
        final GameImage highlight = new GameImage(icon);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                final Color c = icon.getColorAt(x, y);

                if (c.getAlpha() == 0 && hasAdjacent(icon, x, y))
                    highlight.dot(/* TODO */ Colors.bg(), x, y);
            }
        }

        return highlight.submit();
    }

    private static boolean hasAdjacent(
            final GameImage image, final int x, final int y
    ) {
        return notTransparent(image, x - 1, y) ||
                notTransparent(image, x + 1, y) ||
                notTransparent(image, x, y - 1) ||
                notTransparent(image, x, y + 1);
    }

    private static boolean notTransparent(
            final GameImage image, final int x, final int y
    ) {
        if (x < 0 || x >= image.getWidth() ||
                y < 0 || y >= image.getHeight())
            return false;

        return image.getColorAt(x, y).getAlpha() > 0;
    }

    public static GameImage pixelWiseTransformation(
            final GameImage input, final Function<Color, Color> f
    ) {
        final GameImage output = new GameImage(input);

        final int w = output.getWidth(), h = output.getHeight();

        for (int x = 0; x < w; x++)
            for (int y = 0; y < h; y++)
                output.setRGB(x, y, f.apply(input.getColorAt(x, y)).getRGB());

        return output.submit();
    }

    public static Color greyscale(final Color in) {
        final int avg = (in.getRed() + in.getGreen() + in.getBlue()) / 3;
        return new Color(avg, avg, avg, in.getAlpha());
    }

    private static Color shiftRGB(final Color base, final int shift) {
        return new Color(
                shiftChannel(base.getRed(), Math.abs(shift)),
                shiftChannel(base.getGreen(), Math.abs(shift)),
                shiftChannel(base.getBlue(), Math.abs(shift)));
    }

    private static int shiftChannel(final int c, final int shift) {
        final int MIDDLE = 0x80;
        final boolean increase = Math.signum((double) (MIDDLE - c)) >= 0.0;

        return c + (increase ? shift : -shift);
    }
}
