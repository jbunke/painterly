package com.jordanbunke.painterly.theme;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.events.KeyboardShortcut;
import com.jordanbunke.painterly.events.actions.IAction;
import com.jordanbunke.painterly.menu.elements.Button;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.TextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Constants;
import com.jordanbunke.painterly.util.ProgramFont;

import java.awt.*;
import java.util.Arrays;

import static com.jordanbunke.painterly.theme.Colors.*;
import static com.jordanbunke.painterly.theme.Colors.SystemColor.*;
import static com.jordanbunke.painterly.theme.Colors.highlightOverlay;
import static com.jordanbunke.painterly.theme.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.FONT_DEF;

public abstract class Theme {
    // UI ELEMENTS

    public GameImage drawDialogBackground(final int width, final int height) {
        final GameImage image = new GameImage(width, height);
        final Color bgColor = getDialogBackgroundColor(),
                topBarColor = getDialogTopBarColor(),
                borderColor = getDialogBorderColor();

        // background
        image.fill(bgColor);

        // top bar
        image.fillRectangle(topBarColor, 0, 0, width, dialogTitleStripeHeight());

        // border
        image.drawRectangle(borderColor, 2f, 0, 0, width, height);

        return image.submit();
    }

    public GameImage drawContextBarElement(
            final TextButton tb, final ResourceCode iconCode
    ) {
        final Color textColor = getContextBarElementTextColor(tb),
                bgColor = getContextBarElementBackgroundColor(tb);

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        // background
        button.fill(bgColor);

        // draw text
        stampText(button, tb, textColor, true);

        // icon
        stampIcon(button, iconCode);

        return button.submit();
    }

    public GameImage drawSubMenuHeader(final TextButton tb) {
        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        drawSubMenuButton(button, tb, false);

        final GameImage expanderImage = new ProgramFont.FontFormatter(FONT_DEF)
                .realize().setColor(getSubMenuButtonTextColor(tb, false))
                .addText(Constants.NESTED_MENU_BAR_EXPANDER).build().draw();

        final int textX = tb.getWidth() -
                (MENU_BAR_PADDING_X + expanderImage.getWidth());
        button.draw(expanderImage, textX, TEXT_BUTTON_TEXT_OFFSET_Y);

        return button.submit();
    }

    public <T> GameImage drawContextBarOptionButton(
            final TextButton tb, final boolean stub,
            final IAction<T> action
    ) {
        // TODO - unique visuals
        return drawActionMenuButton(tb, stub, action);
    }

    public <T> GameImage drawActionMenuButton(
            final TextButton tb, final boolean stub,
            final IAction<T> action
    ) {
        final KeyboardShortcut shortcut = action.getShortcut();
        final ResourceCode iconCode = action.getIconCode();

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        drawSubMenuButton(button, tb, stub);

        // icon
        stampIcon(button, iconCode);

        // keyboard shortcut
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
    public void drawSubMenuButton(
            final GameImage button, final TextButton tb,
            final boolean stub
    ) {
        final Color textColor = getSubMenuButtonTextColor(tb, stub),
                bgColor = getSubMenuButtonBackgroundColor(tb, stub);

        button.fill(bgColor);

        stampText(button, tb, textColor, true);
    }

    public GameImage drawProjectButton(final TextButton tb) {
        final Color textColor = getProjectButtonTextColor(tb), 
                bgColor = getProjectButtonBackgroundColor(tb);

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        // background
        button.fill(bgColor);

        // draw text
        stampText(button, tb, textColor, tb.isSelected());

        // selection accent
        if (tb.isSelected()) {
            final Color accentColor = getProjectButtonSelectedAccentColor();
            
            final int INDICATOR_HEIGHT = 4;
            button.fillRectangle(accentColor, 0, 
                    h - INDICATOR_HEIGHT, w, INDICATOR_HEIGHT);
        }

        return button.submit();
    }

    public GameImage drawNavbarSubMenuButton(final TextButton tb) {
        // TODO - unique visuals
        return drawTextButton(tb);
    }

    public GameImage drawTextButton(final TextButton tb) {
        // TODO - temp implementation

        final ButtonType type = tb.getButtonType();
        final boolean highlight = tb.isHighlighted();

        final Color textColor, bgColor, accentColor;

        switch (type) {
            case STUB -> {
                bgColor = transparent();
                accentColor = /* TODO */ systemColor(MID_DARK);
                textColor = /* TODO */ systemColor(MID_DARK);
            }
            default -> {
                bgColor = /* TODO */ systemColor(highlight ? MID_DARK : DARK);
                accentColor = /* TODO */ systemColor(highlight ? MID_LIGHT : MID);
                textColor = /* TODO */ systemColor(LIGHT);
            }
        }

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        // background
        button.fill(bgColor);

        // draw text
        stampText(button, tb, textColor, false);

        // border
        button.drawRectangle(accentColor, 4f, 0, 0, w, h);

        return button.submit();
    }

    public GameImage drawTextbox(
            final Bounds2D dims,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        // TODO - temp implementation; copied from TDSM
        final GameImage image = new GameImage(dims.width(), dims.height());

        // pre-processing
        final int left = Math.min(cursorIndex, selectionIndex),
                right = Math.max(cursorIndex, selectionIndex),
                INC = TEXTBOX_SEG_INC;

        final boolean hasSelection = left != right,
                cursorAtRight = cursorIndex == right;

        // setup
        final Color
                mainColor = valid ? /* TODO */ systemColor(DARK) : invalidText(),
                backgroundColor = valid ? /* TODO */ systemColor(LIGHT) : invalidTextBG(),
                outlineColor = typing ? highlightOverlay() :
                        (highlighted ? /* TODO */ systemColor(LIGHT) : mainColor),
                affixColor = shiftRGB(mainColor, 0x40),
                highlightOverlay = highlightOverlay();

        // text and cursor

        final String preSel = text.substring(0, left),
                sel = text.substring(left, right),
                postSel = text.substring(right);

        final ProgramFont.FontFormatter formatter = new ProgramFont.FontFormatter(FONT_DEF);

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

        // background
        image.fill(backgroundColor);

        Coord2D textPos = new Coord2D(TEXT_BUTTON_TEXT_OFFSET_X,
                TEXT_BUTTON_TEXT_OFFSET_Y);

        // possible prefix
        image.draw(prefixImage, textPos.x, textPos.y);
        if (!prefix.isEmpty())
            textPos = textPos.displace(prefixImage.getWidth() + INC, 0);

        // main text prior to possible selection
        image.draw(preSelImage, textPos.x, textPos.y);
        if (!preSel.isEmpty())
            textPos = textPos.displace(preSelImage.getWidth() + INC, 0);

        // possible selection text
        if (hasSelection) {
            if (!cursorAtRight)
                textPos = textPos.displace(2 * INC, 0);

            image.draw(selImage, textPos.x, textPos.y);
            image.fillRectangle(highlightOverlay, textPos.x - INC, 0,
                    selImage.getWidth() + (2 * INC), image.getHeight());
            textPos = textPos.displace(selImage.getWidth() + INC, 0);
        }

        // cursor
        image.fillRectangle(mainColor,
                textPos.x - (cursorAtRight ? 0
                        : selImage.getWidth() + (3 * INC)),
                0, INC, image.getHeight());
        if (cursorAtRight)
            textPos = textPos.displace(2 * INC, 0);

        // main text following possible selection
        image.draw(postSelImage, textPos.x, textPos.y);
        if (!postSel.isEmpty())
            textPos = textPos.displace(postSelImage.getWidth() + INC, 0);

        // possible suffix
        image.draw(suffixImage, textPos.x, textPos.y);

        // outline
        image.drawRectangle(outlineColor, 2f, 0, 0,
                image.getWidth(), image.getHeight());

        return image.submit();
    }

    public GameImage drawHorzSlider(
            final int w, final int h, final double fractionX, final Button b
    ) {
        // TODO - temp
        final GameImage slider = new GameImage(w, h);

        final int shellHeight = SLIDER_SHELL_HEIGHT,
                shellY = (SLIDER_HEIGHT - shellHeight) / 2,
                bd = SLIDER_BALL_DIM, range = w - bd,
                ballX = (int)(fractionX * range);

        slider.fillRectangle(/* TODO */ systemColor(LIGHT), 0, shellY, w, shellHeight);
        slider.drawRectangle(/* TODO */ systemColor(MID_DARK), 2f, 0, shellY, w, shellHeight);

        final GameImage ball = new GameImage(bd, bd);
        final Color ballBorder, ballFill;

        if (b.isSelected()) {
            ballFill = /* TODO */ systemColor(MID_LIGHT);
            ballBorder = /* TODO */ systemColor(LIGHT);
        } else if (b.isHighlighted()) {
            ballFill = /* TODO */ systemColor(MID);
            ballBorder = /* TODO */ systemColor(MID_LIGHT);
        } else {
            ballFill = /* TODO */ systemColor(MID);
            ballBorder = /* TODO */ systemColor(MID_DARK);
        }

        ball.fill(ballFill);
        ball.drawRectangle(ballBorder, 2f, 0, 0, bd, bd);

        slider.draw(ball.submit(), ballX, 0);

        return slider.submit();
    }

    public GameImage drawVertScrollBar(
            final int w, final int h, final int barH,
            final int barY, final Button b
    ) {
        // TODO - copied from TDSM -- review

        final GameImage scrollSpace = new GameImage(w, h),
                scrollBar = new GameImage(w, barH);

        final Color c = b.outcomes(/* TODO */ systemColor(LIGHT),
                /* TODO */ systemColor(MID_LIGHT), /* TODO */ systemColor(MID_DARK)),
                accent = b.outcomes(/* TODO */ systemColor(MID_LIGHT),
                        /* TODO */ systemColor(MID_DARK), /* TODO */ systemColor(DARK));

        scrollBar.fill(c);
        scrollBar.drawLine(accent, 1f, 0, barH - 2, w, barH - 2);
        scrollBar.drawRectangle(/* TODO */ systemColor(DARK), 1f, 0, 0, w - 1, barH - 1);
        // TODO - clearCorners(scrollBar);

        scrollSpace.draw(scrollBar, 0, barY);

        return scrollSpace.submit();
    }

    public GameImage drawSubMenuSeparator(final int width) {
        final GameImage sepImage =
                new GameImage(width, MENU_BAR_SEPARATOR_HEIGHT);
        sepImage.fill(subMenuSeparatorColor());
        return sepImage.submit();
    }

    public GameImage drawMenuBarBackground(final int w, final int h) {
        return drawFilledDarkImage(w, h);
    }

    public GameImage drawContextBarExpansionBackground(
            final int w, final int h
    ) {
        return drawFilledDarkImage(w, h);
    }

    public GameImage drawContextBarBackground(final int w, final int h) {
        return drawFilledDarkImage(w, h);
    }

    // UI HELPERS

    @SuppressWarnings("unused")
    GameImage drawFilledDarkImage(final int w, final int h) {
        final GameImage image = new GameImage(w, h);
        image.fill(primaryUIBackgroundColor());
        return image.submit();
    }

    // TEXT POP-UPS

    public GameImage drawDebugMessage(final String text) {
        // TODO - temp implementation
        final Color textColor = debug();
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> new ProgramFont.FontFormatter(FONT_DEF).realize()
                        .setColor(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
        final int ls = lines.length,
                w = Arrays.stream(lineImages)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(/* TODO */ systemColor(DARK));

        for (int l = 0; l < ls; l++) {
            final GameImage line = lineImages[l];
            final int x = w - (line.getWidth() + TOOLTIP_PADDING_X / 2),
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        return tooltip.submit();
    }

    public GameImage drawTooltip(final String text) {
        // TODO - temp implementation
        final Color textColor = /* TODO */ systemColor(DARK);
        final String[] lines = text.split("\n");
        final GameImage[] lineImages = Arrays.stream(lines)
                .map(l -> new ProgramFont.FontFormatter(FONT_DEF).realize()
                        .setColor(textColor).addText(l).build().draw())
                .toArray(GameImage[]::new);
        final int ls = lines.length,
                w = Arrays.stream(lineImages)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(/* TODO */ systemColor(MID_LIGHT));

        for (int l = 0; l < ls; l++) {
            final GameImage line = lineImages[l];
            final int x = (w - line.getWidth()) / 2,
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        return tooltip.submit();
    }

    // COLOR DETERMINERS

    Color getSubMenuButtonTextColor(final TextButton tb, final boolean stub) {
        return stub ? stubTextColor() : lightTextColor();
    }

    Color getSubMenuButtonBackgroundColor(final TextButton tb, final boolean stub) {
        return tb.isHighlighted()
                ? highlightUIBackgroundColor()
                : primaryUIBackgroundColor();
    }

    Color getContextBarElementTextColor(final TextButton tb) {
        return tb.isSelected() ? darkTextColor() : lightTextColor();
    }

    Color getContextBarElementBackgroundColor(final TextButton tb) {
        if (tb.isSelected())
            return contrastUIBackgroundColor();
        else if (tb.isHighlighted())
            return highlightUIBackgroundColor();
        else
            return primaryUIBackgroundColor();
    }

    Color getProjectButtonTextColor(final TextButton tb) {
        return lightTextColor();
    }
    
    Color getProjectButtonBackgroundColor(final TextButton tb) {
        return tb.isHighlighted() 
                ? highlightUIBackgroundColor() 
                : primaryUIBackgroundColor();
    }
    
    Color getProjectButtonSelectedAccentColor() {
        // TODO
        return systemColor(LIGHT);
    }

    Color getDialogTopBarColor() {
        return menuBackgroundContrastColor();
    }

    Color getDialogBackgroundColor() {
        return menuBackgroundColor();
    }

    Color getDialogBorderColor() {
        return menuBorderColor();
    }

    // ROLE COLORS

    public Color subMenuSeparatorColor() {
        // TODO
        return systemColor(LIGHT);
    }

    public Color dialogBoxTitleTextColor() {
        // TODO
        return systemColor(LIGHT);
    }

    public Color viewportBackgroundColor() {
        // TODO
        return systemColor(MID);
    }

    Color menuBackgroundColor() {
        // TODO
        return systemColor(LIGHT);
    }

    Color menuBackgroundContrastColor() {
        // TODO
        return systemColor(MID_DARK);
    }

    Color menuBorderColor() {
        // TODO
        return systemColor(MID_DARK);
    }

    Color lightTextColor() {
        // TODO
        return systemColor(LIGHT);
    }

    Color darkTextColor() {
        // TODO
        return systemColor(DARK);
    }

    Color stubTextColor() {
        // TODO
        return systemColor(MID_LIGHT);
    }

    Color highlightUIBackgroundColor() {
        // TODO
        return systemColor(MID_DARK);
    }

    Color primaryUIBackgroundColor() {
        // TODO
        return systemColor(DARK);
    }

    Color contrastUIBackgroundColor() {
        // TODO
        return systemColor(LIGHT);
    }
}
