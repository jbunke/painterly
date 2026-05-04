package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.debug.GameDebugger;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.InputEventLogger;
import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementContainer;
import com.jordanbunke.delta_time.menu.menu_elements.container.MenuElementGrouping;
import com.jordanbunke.delta_time.menu.menu_elements.ext.scroll.Scrollable;
import com.jordanbunke.delta_time.utility.math.Bounds2D;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.complex.logic.EnumMenuElement;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.scroll.VertScrollBox;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Graphics;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.SCREEN;

public final class PopUpDialog extends MenuElementContainer {
    private final GameImage background;
    private final MenuElement title, contents, resolutionButtons;

    private PopUpDialog(
            final int width, final int height,
            final MenuElement title, final MenuElement contents,
            final MenuElement resolutionButtons
    ) {
        super(SCREEN.at(0.5, 0.5),
                new Bounds2D(width, height), Anchor.CENTRAL, true);

        this.title = title;
        this.contents = contents;
        this.resolutionButtons = resolutionButtons;

        background = Graphics.drawDialogBackground(width, height);
    }

    public static Builder init(
            final ResourceCode titleCode
    ) {
        return new Builder(titleCode);
    }

    @Override
    public MenuElement[] getMenuElements() {
        return new MenuElement[] {
                title, contents, resolutionButtons
        };
    }

    @Override
    public boolean hasNonTrivialBehaviour() {
        return true;
    }

    @Override
    public void process(final InputEventLogger eventLogger) {
        // TODO - key input processing

        title.process(eventLogger);
        contents.process(eventLogger);
        resolutionButtons.process(eventLogger);
    }

    @Override
    public void update(final double deltaTime) {
        title.update(deltaTime);
        contents.update(deltaTime);
        resolutionButtons.update(deltaTime);
    }

    @Override
    public void render(final GameImage canvas) {
        // background
        canvas.fill(dialogVeil());
        final Coord2D rp = getRenderPosition();
        canvas.draw(background, rp.x, rp.y);

        // contents
        contents.render(canvas);
        title.render(canvas);
        resolutionButtons.render(canvas);
    }

    @Override
    public void debugRender(final GameImage canvas, final GameDebugger debugger) {}

    public static class Builder implements MenuElementBuilder<PopUpDialog> {
        private final String title;
        private final List<DialogElement> elements;

        private boolean widthFromContents, heightFromContents;
        private int width, height, contentBottom;

        /**
         * If {@code true}, bottom-right corner has single "Close" button
         * instead of a confirmation button and a "Cancel" button
         * */
        private boolean onlyInformation;
        private String okText;
        private Supplier<Boolean> precondition;
        private Runnable onOK;

        Builder(
                final ResourceCode titleCode
        ) {
            this.title = LanguageData.retrieveUIText(titleCode);
            elements = new LinkedList<>();

            widthFromContents = false;
            heightFromContents = false;
            width = defaultDialogWidth();
            height = defaultDialogHeight();
            contentBottom = 0;

            onlyInformation = false;
            okText = LanguageData.retrieveUIText(RC_DIALOG_DEFAULT_OK);
            precondition = () -> true;
            onOK = () -> {};
        }

        // elements

        public Builder addElements(final DialogElement... elements) {
            Arrays.stream(elements).forEach(this::addElement);
            return this;
        }

        public Builder addElement(final DialogElement element) {
            elements.add(element);

            if (widthFromContents)
                width += augmentWidthIfElementExceeds(element);

            contentBottom = Math.max(contentBottom, element.below(0).y);

            if (heightFromContents)
                calculateHeightFromContents();

            return this;
        }

        // layout

        public Builder setSizeFromContents() {
            setWidthFromContents();
            setHeightFromContents();

            return this;
        }

        /**
         * To be invoked after {@link #setAsOnlyInformation()} but before
         * calls to {@link #addElement(DialogElement)}
         * */
        public Builder setWidthFromContents() {
            widthFromContents = true;

            if (elements.isEmpty())
                setAsMinimumWidthAccountingForTitle();

            return this;
        }

        public Builder setWidth(final int width) {
            this.width = width;
            return this;
        }

        public Builder setWidthAsScreenPercentage(final double percW) {
            return setWidth((int)(percW * width()));
        }
        
        public Builder setHeightFromContents() {
            heightFromContents = true;

            calculateHeightFromContents();
            
            return this;
        }

        public Builder setHeight(final int height) {
            this.height = height;
            return this;
        }

        public Builder setHeightAsScreenPercentage(final double percH) {
            return setHeight((int)(percH * height()));
        }

        // layout calculators

        public int elementX(final int columnIndex, final int columns) {
            final int nonMarginWidth = width - ((columns + 1) * DIALOG_MARGIN),
                    columnWidth = nonMarginWidth / columns;

            return (columnWidth * columnIndex) +
                    (DIALOG_MARGIN * (columnIndex + 1));
        }

        public int elementY(final double row) {
            return ((int)(row * DIALOG_ROW_INCREMENT));
        }

        // logic

        public Builder setAsOnlyInformation() {
            onlyInformation = true;
            return this;
        }

        public Builder setOKText(final ResourceCode code) {
            return setOKTextLiteral(LanguageData.retrieveUIText(code));
        }

        public Builder setOKTextLiteral(final String okText) {
            this.okText = okText;
            return this;
        }

        public Builder setPrecondition(final Supplier<Boolean> precondition) {
            this.precondition = precondition;
            return this;
        }

        public Builder setOnOK(final Runnable onOK) {
            this.onOK = onOK;
            return this;
        }

        @Override
        public PopUpDialog build() {
            final Coord2D screenMiddle = SCREEN.at(0.5, 0.5),
                    renderPos = screenMiddle.displace(
                            new Coord2D(-width / 2, -height / 2)),
                    scrollBoxPos = renderPos.displace(1, dialogTitleStripeHeight()),
                    elementOffset = renderPos.displaceY(DIALOG_CONTENT_TOP_OFFSET_Y),
                    bottomRight = screenMiddle.displace(
                            (width / 2) - DIALOG_MARGIN,
                            (height / 2) - DIALOG_MARGIN);

            // title
            final SimpleLabel titleLabel = SimpleLabel.initLiteral(
                            title, renderPos.displace(DIALOG_MARGIN, DIALOG_MARGIN))
                    .setColor(systemColor(MID_DARK))
                    .setAnchor(Anchor.LEFT_TOP).build();

            // resolution buttons (cancel/close and OK?)
            final List<MenuElement> resolutionButtons = new LinkedList<>();

            final SimpleTextButton cancelCloseButton = SimpleTextButton.init(
                    onlyInformation ? RC_DIALOG_CLOSE : RC_DIALOG_CANCEL,
                    bottomRight, DialogManager::close)
                    .setWidth(DIALOG_RESOLUTION_BUTTON_WIDTH)
                    .setAnchor(Anchor.RIGHT_BOTTOM)
                    .build();
            resolutionButtons.add(cancelCloseButton);

            if (!onlyInformation) {
                final EnumMenuElement okButton = SimpleTextButton.initLiteral(
                        okText, cancelCloseButton.getRenderPosition()
                                        .displaceX(-DIALOG_MARGIN), onOK)
                        .setWidth(DIALOG_RESOLUTION_BUTTON_WIDTH)
                        .setAnchor(Anchor.RIGHT_TOP)
                        .buildConditional(precondition);
                resolutionButtons.add(okButton);
            }

            // scroll box
            final int boxMargin = dialogTitleStripeHeight() + dialogBottomHeight();
            final VertScrollBox scrollBox = new VertScrollBox(
                    scrollBoxPos, new Bounds2D(width - 2, height - boxMargin),
                    elements.stream()
                            .map(de -> de.element)
                            .peek(me -> {
                                me.incrementX(elementOffset.x);
                                me.incrementY(elementOffset.y);
                            })
                            .map(Scrollable::new).toArray(Scrollable[]::new),
                    contentBottom + elementOffset.y, 0);

            return new PopUpDialog(width, height, titleLabel, scrollBox,
                    new MenuElementGrouping(resolutionButtons.toArray(MenuElement[]::new)));
        }

        // helper

        private int augmentWidthIfElementExceeds(final DialogElement element) {
            final int room = width - (2 * DIALOG_MARGIN),
                    augmentation = element.rightOf(0).x - room;

            return Math.max(augmentation, 0);
        }

        private void setAsMinimumWidthAccountingForTitle() {
            final int buttonAllotment = DIALOG_RESOLUTION_BUTTON_WIDTH +
                    (4 * DIALOG_MARGIN) +
                    (onlyInformation ? 0 : DIALOG_RESOLUTION_BUTTON_WIDTH +
                            DIALOG_MARGIN),
                    titleAllotment = Graphics.naiveButtonWidth(title);

            width = Math.max(buttonAllotment, titleAllotment) +
                    (2 * DIALOG_MARGIN);
        }

        private void calculateHeightFromContents() {
            final int buffer = DIALOG_CONTENT_TOP_OFFSET_Y + dialogBottomHeight(),
                    prospectiveHeight = Math.max(contentBottom, DIALOG_CONTENT_MIN_HEIGHT) + buffer,
                    maxHeight = maxDialogHeight();

            height = Math.min(prospectiveHeight, maxHeight);
        }
    }
}
