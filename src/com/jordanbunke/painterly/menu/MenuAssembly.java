package com.jordanbunke.painterly.menu;

import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.menu.Menu;
import com.jordanbunke.delta_time.menu.MenuBuilder;
import com.jordanbunke.delta_time.menu.menu_elements.visual.StaticMenuElement;
import com.jordanbunke.delta_time.text.Text;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.ProgramInfo;
import com.jordanbunke.painterly.flow.ProgramState;
import com.jordanbunke.painterly.dialog.visual.DialogAssembly;
import com.jordanbunke.painterly.dialog.visual.DialogManager;
import com.jordanbunke.painterly.menu.elements.MenuElementBuilder;
import com.jordanbunke.painterly.menu.elements.label.LoadingLabel;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.ButtonType;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.resources.lang.LanguageData;
import com.jordanbunke.painterly.util.Colors;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static com.jordanbunke.delta_time.menu.menu_elements.MenuElement.Anchor.*;
import static com.jordanbunke.painterly.menu.elements.text_button.Alignment.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.util.Layout.ScreenBox.*;

public final class MenuAssembly {
    public static Menu stub() {
        return new Menu();
    }

    public static Menu mainMenu() {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - background element

        final int MARGIN = 20;

        // navigation
        addMenuElements(mb, (i, builder) ->
                // common
                builder.setAlignment(CENTER).setAnchor(LEFT_CENTRAL)
                        .setButtonType(ButtonType.STANDARD)
                        .setWidth(SCREEN.ofWidth(0.2))
                        .setPosition(new Coord2D(
                                SCREEN.offsetX(MARGIN),
                                SCREEN.atY(0.5) +
                                        (i * (builder.getHeight() + TEXT_BUTTON_INTERVAL_S_Y))
                        )),
                // elements
                SimpleTextButton.init(RC_START, new Coord2D(), ProgramState::setWorkspace),
                SimpleTextButton.init(RC_ABOUT, new Coord2D(), () -> {})
                        .setTooltipCode(RC_ABOUT),
                SimpleTextButton.init(RC_PROGRAM_SETTINGS, new Coord2D(), () -> {})
                        .setTooltipCode(RC_PROGRAM_SETTINGS),
                SimpleTextButton.init(RC_QUIT, new Coord2D(), Painterly::quitProgram));

        // TODO - logo

        // version and credits
        final SimpleLabel programLabel = SimpleLabel.initLiteral(
                ProgramInfo.formatVersion(),
                        new Coord2D(MARGIN, SCREEN.offsetY(SCREEN.height.get() - MARGIN)))
                .setAnchor(LEFT_BOTTOM)
                .setOrientation(Text.Orientation.LEFT)
                .addInstruction(tb -> tb.addLineBreak()
                        .addText(LanguageData.retrieveUIText(RC_COPYRIGHT)))
                .build();
        mb.add(programLabel);

        return mb.build();
    }

    public static Menu contextBar() {
        final MenuBuilder mb = new MenuBuilder();
        final ScreenBox sb = CONTEXT_BAR;

        final int x = sb.x.get(), y = sb.y.get(),
                w = sb.width.get(), h = sb.height.get();

        // TODO

        // TODO - temp
        addTempScreenBoxSpan(mb, sb);

        return mb.build();
    }

    public static Menu menuBar() {
        final MenuBuilder mb = new MenuBuilder();
        final ScreenBox sb = MENU_BAR;

        final int x = sb.x.get(), y = sb.y.get(),
                w = sb.width.get(), h = sb.height.get();

        // TODO

        // TODO - temp
        addTempScreenBoxSpan(mb, sb);

        return mb.build();
    }

    public static Menu noProjectsOpenMenu() {
        final MenuBuilder mb = new MenuBuilder();
        final ScreenBox sb = PROJECT_VIEWPORT;

        // TODO
        final SimpleLabel noProjectsOpenLabel = SimpleLabel.init(
                RC_NO_PROJECTS_OPEN, sb.at(0.5, 0.5))
                .setAnchor(CENTRAL_BOTTOM).build();
        mb.add(noProjectsOpenLabel);

        final int MARGIN = 10;

        addMenuElements(mb, (i, builder) ->
                        builder.setAlignment(CENTER)
                                .setButtonType(ButtonType.STANDARD)
                                .setWidth(SCREEN.ofWidth(0.15))
                                .setPosition(sb.at(0.5, 0.5)
                                        .displaceY(MARGIN)
                                        .displaceX(MARGIN * (i == 0 ? -1 : 1))),
                // buttons
                SimpleTextButton.init(RC_NEW_PROJECT, new Coord2D(),
                                () -> DialogManager.set(DialogAssembly::newProject))
                        .setTooltipCode(RC_NEW_PROJECT)
                        .setAnchor(RIGHT_TOP),
                SimpleTextButton.init(RC_OPEN_PROJECT, new Coord2D(), () -> {} /* TODO */)
                        .setTooltipCode(RC_OPEN_PROJECT)
                        .setAnchor(LEFT_TOP));

        return mb.build();
    }

    public static Menu loading(final ResourceCode code) {
        final MenuBuilder mb = new MenuBuilder();

        // TODO - potential background elements

        mb.add(new LoadingLabel(code));

        return mb.build();
    }

    // TODO - temporary; for removal
    @Deprecated
    private static void addTempScreenBoxSpan(
            final MenuBuilder mb, final ScreenBox sb
    ) {
        final int x = sb.x.get(), y = sb.y.get(),
                w = sb.width.get(), h = sb.height.get();

        final GameImage img = new GameImage(w, h);
        img.fill(Colors.systemColor(Colors.SystemColor.DARK));
        final StaticMenuElement tempBackground = new StaticMenuElement(
                new Coord2D(x, y), LEFT_TOP, img);
        mb.add(tempBackground);
    }

    // HELPER

    @SafeVarargs
    private static <B extends MenuElementBuilder<?>> void addMenuElements(
            final MenuBuilder mb,
            final BiConsumer<Integer, B> forEvery,
            final B... builders
    ) {
        for (int i = 0; i < builders.length; i++) {
            final B builder = builders[i];
            forEvery.accept(i, builder);
            mb.add(builder.build());
        }
    }

    private static <B extends MenuElementBuilder<?>> void addMenuElements(
            final MenuBuilder mb, final int amount,
            final BiConsumer<Integer, B> forEvery,
            final Function<Integer, B> initialization
    ) {
        if (amount <= 0)
            return;

        IntStream.range(0, amount).forEach(i -> {
            final B builder = initialization.apply(i);
            forEvery.accept(i, builder);
            mb.add(builder.build());
        });
    }
}
