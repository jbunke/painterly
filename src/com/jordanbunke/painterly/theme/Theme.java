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
import static com.jordanbunke.painterly.theme.Graphics.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.ProgramFont.FONT_DEF;

public abstract class Theme {
    // UI ELEMENTS

    public GameImage drawDialogBackground(final int width, final int height) {
        final GameImage image = new GameImage(width, height);
        final Color bgColor = getDialogBackgroundColor(),
                topBarColor = getDialogTopBarColor(),
                bottomBarColor = getDialogBottomBarColor(),
                borderColor = getDialogBorderColor();
        final int topBarHeight = dialogTitleStripeHeight(),
                bottomBarHeight = dialogBottomHeight(),
                bottomBarY = height - bottomBarHeight;

        // background
        image.fill(bgColor);

        // top bar
        image.fillRectangle(topBarColor, 0, 0, width, topBarHeight);
        image.drawLine(borderColor, 1f, 0, topBarHeight - 1, width, topBarHeight - 1);

        // bottom bar
        image.fillRectangle(bottomBarColor, 0, bottomBarY, width, bottomBarHeight);
        image.drawLine(borderColor, 1f, 0, bottomBarY, width, bottomBarY);

        // border
        image.drawRectangle(borderColor, 2f, 0, 0, width, height);

        smoothCorners(image, borderColor);

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
            
            final int INDICATOR_HEIGHT = 3, INDICATOR_X = 3;
            button.fillRectangle(accentColor,
                    INDICATOR_X, h - INDICATOR_HEIGHT,
                    w - (2 * INDICATOR_X), INDICATOR_HEIGHT);
        }

        return button.submit();
    }

    public GameImage drawNavbarSubMenuButton(final TextButton tb) {
        final Color textColor = getTextButtonTextColor(tb),
                bgColor = getTextButtonBackgroundColor(tb);

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        // background
        button.fill(bgColor);

        // draw text
        stampText(button, tb, textColor, false);

        return button.submit();
    }

    public GameImage drawTextButton(final TextButton tb) {
        final Color textColor = getTextButtonTextColor(tb),
                bgColor = getTextButtonBackgroundColor(tb),
                borderColor = getTextButtonBorderColor(tb);

        final int w = tb.getWidth(), h = tb.getHeight();
        final GameImage button = new GameImage(w, h);

        // background
        button.fill(bgColor);

        // draw text
        stampText(button, tb, textColor, false);

        // border
        button.drawRectangle(borderColor, 2f, 0, 0, w, h);
        smoothCorners(button, borderColor);

        return button.submit();
    }

    public GameImage drawTextbox(
            final Bounds2D dims,
            final String prefix, final String text, final String suffix,
            final int cursorIndex, final int selectionIndex,
            final boolean valid, final boolean highlighted, final boolean typing
    ) {
        final int w = dims.width(), h = dims.height();
        final GameImage image = new GameImage(w, h);

        // pre-processing
        final int left = Math.min(cursorIndex, selectionIndex),
                right = Math.max(cursorIndex, selectionIndex),
                INC = TEXTBOX_SEG_INC;

        final boolean hasSelection = left != right,
                cursorAtRight = cursorIndex == right;

        // setup
        final Color textColor = getTextboxTextColor(typing, valid, highlighted),
                backgroundColor = getTextboxBackgroundColor(typing, valid, highlighted),
                borderColor = getTextboxBorderColor(typing, valid, highlighted),
                affixColor = getTextboxAffixColor(typing, valid, highlighted),
                highlightOverlayColor = textboxHighlightOverlayColor();

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
                        .setColor(textColor)
                        .addText(preSel).build().draw(),
                selImage = formatter.realize()
                        .setColor(textColor)
                        .addText(sel).build().draw(),
                postSelImage = formatter.realize()
                        .setColor(textColor)
                        .addText(postSel).build().draw();

        // background
        image.fill(backgroundColor);

        Coord2D textPos = new Coord2D(TEXT_BUTTON_TEXT_OFFSET_X,
                TEXT_BUTTON_TEXT_OFFSET_Y);

        // possible prefix
        image.draw(prefixImage, textPos.x, textPos.y);
        if (!prefix.isEmpty())
            textPos = textPos.displaceX(prefixImage.getWidth() + INC);

        // main text prior to possible selection
        image.draw(preSelImage, textPos.x, textPos.y);
        if (!preSel.isEmpty())
            textPos = textPos.displaceX(preSelImage.getWidth() + INC);

        // possible selection text
        if (hasSelection) {
            if (!cursorAtRight)
                textPos = textPos.displaceX(2 * INC);

            image.fillRectangle(highlightOverlayColor, textPos.x - INC, 0,
                    selImage.getWidth() + (2 * INC), h);
            image.draw(selImage, textPos.x, textPos.y);
            textPos = textPos.displaceX(selImage.getWidth() + INC);
        }

        // cursor
        image.fillRectangle(textColor,
                textPos.x - (cursorAtRight ? 0
                        : selImage.getWidth() + (3 * INC)),
                0, INC, h);
        if (cursorAtRight)
            textPos = textPos.displaceX(2 * INC);

        // main text following possible selection
        image.draw(postSelImage, textPos.x, textPos.y);
        if (!postSel.isEmpty())
            textPos = textPos.displaceX(postSelImage.getWidth() + INC);

        // possible suffix
        image.draw(suffixImage, textPos.x, textPos.y);

        // border
        image.drawRectangle(borderColor, 2f, 0, 0, w, h);
        smoothCorners(image, borderColor);

        return image.submit();
    }

    public GameImage drawHorzSlider(
            final int w, final int h, final double fractionX, final Button b
    ) {
        final GameImage slider = new GameImage(w, h);

        final int shellHeight = SLIDER_SHELL_HEIGHT,
                shellY = (SLIDER_HEIGHT - shellHeight) / 2,
                bd = SLIDER_BALL_DIM, range = w - bd,
                shellOffsetX = bd / 2,
                ballX = (int)(fractionX * range);

        final Color sliderFillColor = contrastUIBackgroundColor(),
                sliderBorderColor = contrastUIAccentColor();

        // render shell
        final GameImage sliderShell = new GameImage(range, shellHeight);
        sliderShell.fill(sliderFillColor);
        sliderShell.drawRectangle(sliderBorderColor, 2f, 0, 0, range, shellHeight);
        smoothCorners(sliderShell, sliderBorderColor);

        slider.draw(sliderShell.submit(), shellOffsetX, shellY);

        final GameImage ball = new GameImage(bd, bd);
        final Color ballBorder = getSliderBallBorderColor(b),
                ballFill = getSliderBallFillColor(b);

        // render ball
        ball.fill(ballFill);
        circleOnly(ball);
        innerOutline(ball, ballFill, ballBorder);

        slider.draw(ball.submit(), ballX, 0);

        return slider.submit();
    }

    public GameImage drawVertScrollBar(
            final int w, final int h, final int barH,
            final int barY, final Button b
    ) {
        final GameImage scrollSpace = new GameImage(w, h),
                scrollBar = new GameImage(w, barH);

        final Color fillColor = getScrollBarFillColor(b),
                borderColor = getScrollBarBorderColor(b);

        scrollBar.fill(fillColor);
        scrollBar.drawRectangle(borderColor, 2f, 0, 0, w, barH);
        smoothCorners(scrollBar, borderColor);

        scrollSpace.draw(scrollBar.submit(), 0, barY);

        return scrollSpace.submit();
    }

    public GameImage drawSubMenuSeparator(final int width) {
        final GameImage sepImage =
                new GameImage(width, MENU_BAR_SEPARATOR_HEIGHT);
        sepImage.fill(subMenuSeparatorColor());
        return sepImage.submit();
    }

    public GameImage drawMenuBackground(final int w, final int h) {
        final GameImage image = new GameImage(w, h);
        image.fill(menuBackgroundColor());
        return image.submit();
    }

    public GameImage drawWorkspaceMenuBackground(final int w, final int h) {
        return drawMenuBackground(w, h);
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

    public GameImage drawLogMessage(final String text) {
        final Color textColor = logMessageTextColor();
        final GameImage[] lines = splitTextLines(text, textColor);
        final int ls = lines.length,
                w = Arrays.stream(lines)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_LINE_INC_Y * ls;

        final GameImage logMessage = new GameImage(w, h);

        // background
        logMessage.fill(logMessageBackgroundColor());

        for (int l = 0; l < ls; l++) {
            final GameImage line = lines[l];
            final int x = w - (line.getWidth() + TOOLTIP_PADDING_X / 2),
                    y = TOOLTIP_INITIAL_OFFSET_Y + (l * TOOLTIP_LINE_INC_Y);
            logMessage.draw(line, x, y);
        }

        return logMessage.submit();
    }

    public GameImage drawTooltip(final String text) {
        final Color textColor = tooltipTextColor(),
                backgroundColor = tooltipBackgroundColor(),
                borderColor = tooltipBorderColor();
        final GameImage[] lines = splitTextLines(text, textColor);
        final int ls = lines.length,
                w = Arrays.stream(lines)
                        .map(GameImage::getWidth)
                        .reduce(1, Math::max) + TOOLTIP_PADDING_X,
                h = TOOLTIP_PADDING_Y + TOOLTIP_LINE_INC_Y * ls;

        final GameImage tooltip = new GameImage(w, h);

        // background
        tooltip.fill(backgroundColor);

        // render text
        for (int l = 0; l < ls; l++) {
            final GameImage line = lines[l];
            final int x = (w - line.getWidth()) / 2,
                    y = (TOOLTIP_PADDING_Y / 2) +
                            TOOLTIP_INITIAL_OFFSET_Y +
                            (l * TOOLTIP_LINE_INC_Y);
            tooltip.draw(line, x, y);
        }

        // border
        tooltip.drawRectangle(borderColor, 2f, 0, 0, w, h);
        smoothCorners(tooltip, borderColor);

        return tooltip.submit();
    }

    // COLOR DETERMINERS

    Color getTextboxTextColor(
            final boolean typing, final boolean valid,
            final boolean highlighted
    ) {
        return valid ? validTextboxTextColor() : invalidTextboxTextColor();
    }

    Color getTextboxBackgroundColor(
            final boolean typing, final boolean valid,
            final boolean highlighted
    ) {
        return valid
                ? validTextboxBackgroundColor()
                : invalidTextboxBackgroundColor();
    }

    Color getTextboxBorderColor(
            final boolean typing, final boolean valid,
            final boolean highlighted
    ) {
        if (typing)
            return typingTextboxBorderColor();
        else if (highlighted)
            return highlightedTextboxBorderColor();
        else if (valid)
            return validTextboxBorderColor();

        return invalidTextboxBorderColor();
    }

    Color getTextboxAffixColor(
            final boolean typing, final boolean valid,
            final boolean highlighted
    ) {
        return valid ? validTextboxAffixColor() : invalidTextboxAffixColor();
    }

    Color getScrollBarFillColor(final Button b) {
        if (b.isSelected() || b.isHighlighted())
            return primaryUIAccentColor();

        return neutralUIElementColor();
    }

    Color getScrollBarBorderColor(final Button b) {
        return primaryUIBackgroundColor();
    }

    Color getSliderBallFillColor(final Button b) {
        if (b.isSelected() || b.isHighlighted())
            return primaryUIAccentColor();

        return neutralUIElementColor();
    }

    Color getSliderBallBorderColor(final Button b) {
        if (b.isSelected())
            return contrastUIBackgroundColor();

        return primaryUIBackgroundColor();
    }

    Color getTextButtonTextColor(final TextButton tb) {
        if (tb.getButtonType() == ButtonType.STUB)
            return stubTextColor();

        return primaryTextColor();
    }

    Color getTextButtonBackgroundColor(final TextButton tb) {
        if (tb.getButtonType() == ButtonType.STUB)
            return transparent();
        else if (tb.isHighlighted())
            return highlightUIBackgroundColor();

        return primaryUIBackgroundColor();
    }

    Color getTextButtonBorderColor(final TextButton tb) {
        if (tb.getButtonType() == ButtonType.STUB)
            return stubTextColor();
        else if (tb.isHighlighted())
            return primaryTextColor();

        return stubTextColor();
    }

    Color getSubMenuButtonTextColor(final TextButton tb, final boolean stub) {
        return stub ? stubTextColor() : primaryTextColor();
    }

    Color getSubMenuButtonBackgroundColor(final TextButton tb, final boolean stub) {
        return tb.isHighlighted()
                ? highlightUIBackgroundColor()
                : primaryUIBackgroundColor();
    }

    Color getContextBarElementTextColor(final TextButton tb) {
        return tb.isSelected() ? contrastTextColor() : primaryTextColor();
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
        return primaryTextColor();
    }
    
    Color getProjectButtonBackgroundColor(final TextButton tb) {
        return tb.isHighlighted() 
                ? highlightUIBackgroundColor() 
                : primaryUIBackgroundColor();
    }
    
    Color getProjectButtonSelectedAccentColor() {
        return systemColor(ACCENT);
    }

    Color getDialogTopBarColor() {
        return menuBackgroundContrastColor();
    }

    Color getDialogBottomBarColor() {
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
        return systemColor(LIGHT_BG);
    }

    public Color dialogBoxTitleTextColor() {
        return systemColor(LIGHT_TEXT);
    }

    public Color viewportBackgroundColor() {
        return systemColor(VIEWPORT_BG);
    }

    Color tooltipTextColor() {
        // TODO
        return systemColor(DARK);
    }

    Color tooltipBackgroundColor() {
        return systemColor(TOOLTIP_BG);
    }

    Color tooltipBorderColor() {
        return systemColor(DARK);
    }

    Color logMessageTextColor() {
        return primaryTextColor();
    }

    Color logMessageBackgroundColor() {
        return primaryUIBackgroundColor();
    }

    Color validTextboxTextColor() {
        return primaryTextColor();
    }

    Color invalidTextboxTextColor() {
        return invalidText();
    }

    Color validTextboxBackgroundColor() {
        return contrastUIAccentColor();
    }

    Color invalidTextboxBackgroundColor() {
        return invalidTextBG();
    }

    Color validTextboxAffixColor() {
        return stubTextColor();
    }

    Color invalidTextboxAffixColor() {
        // TODO
        return validTextboxAffixColor();
    }

    Color typingTextboxBorderColor() {
        return highlightOverlay();
    }

    Color highlightedTextboxBorderColor() {
        return contrastUIBackgroundColor();
    }

    Color validTextboxBorderColor() {
        return primaryUIAccentColor();
    }

    Color invalidTextboxBorderColor() {
        return invalidText();
    }

    Color textboxHighlightOverlayColor() {
        return highlightOverlay();
    }

    Color menuBackgroundColor() {
        return systemColor(MENU_BG);
    }

    Color menuBackgroundContrastColor() {
        // TODO
        return systemColor(MID_DARK);
    }

    Color menuBorderColor() {
        // TODO
        return systemColor(DARK);
    }

    Color primaryTextColor() {
        // TODO
        return systemColor(LIGHT_TEXT);
    }

    Color contrastTextColor() {
        // TODO
        return systemColor(DARK);
    }

    Color stubTextColor() {
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

    Color primaryUIAccentColor() {
        // TODO
        return systemColor(MID_LIGHT);
    }

    Color contrastUIBackgroundColor() {
        // TODO
        return systemColor(LIGHT_BG);
    }

    Color contrastUIAccentColor() {
        // TODO
        return systemColor(MID_DARK);
    }

    Color neutralUIElementColor() {
        // TODO
        return systemColor(MID);
    }
}
