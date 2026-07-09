package com.jordanbunke.painterly.dialog.data;

import com.jordanbunke.painterly.dialog.visual.DialogElement;
import com.jordanbunke.painterly.dialog.visual.PopUpDialog;
import com.jordanbunke.painterly.menu.elements.icon_button.Checkbox;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.menu.elements.textbox.Textbox;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Constants;

import java.util.function.Function;

import static com.jordanbunke.painterly.dialog.visual.DialogAssembly.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

@FunctionalInterface
public interface VariableUIAssembler<T> {
    DialogElement[] assemble(
            final int row, final DialogVariable<T> variable,
            final PopUpDialog.Builder db
    );

    // BLANK

    static <T> DialogElement[] blank(
            @SuppressWarnings("unused") final int row,
            @SuppressWarnings("unused") final DialogVariable<T> variable,
            @SuppressWarnings("unused") final PopUpDialog.Builder db
    ) {
        return new DialogElement[] {};
    }

    // SPECIFIC ASSEMBLERS

    static VariableUIAssembler<String> assembleProjectNameTextbox() {
        return assembleTextbox(RC_DIALOG_TX_PROJECT_NAME,
                s -> s, b -> b);
    }

    static VariableUIAssembler<Double> assembleScaleFactorTextbox() {
        return assembleTextbox(RC_DIALOG_TX_SCALE_FACTOR,
                Validator::nullableParseDouble, b -> b
                        .setWidthRelative(0.4)
                        .setSuffix("x"));
    }

    static VariableUIAssembler<Integer> assembleAutosaveFrequencyTextbox() {
        return assembleTextbox(RC_DIALOG_TX_AUTOSAVE_FREQUENCY,
                Validator::nullableParseInt, b -> b
                        .setPrefix(RC_DIALOG_TX_AUTOSAVE_FREQUENCY_PREFIX)
                        .setSuffix(RC_DIALOG_TX_AUTOSAVE_FREQUENCY_SUFFIX)
                        .setWidthRelative(1.5)
                        .setMaxLength(String.valueOf(
                                Constants.MAX_AUTOSAVE_FREQUENCY).length()));
    }

    static <T> VariableUIAssembler<T> assembleChooseFolderButton(
            final Runnable chooseFolderFunction
    ) {
        return assembleButton(RC_DIALOG_TX_SAVE_FOLDER,
                RC_DIALOG_UI_CHOOSE_FOLDER, chooseFolderFunction);
    }

    static <T> VariableUIAssembler<T> assembleUploadSourceImageButton(
            final Runnable uploadSourceImageFunction
    ) {
        return assembleButton(RC_DIALOG_TX_SOURCE_IMAGE,
                RC_DIALOG_UI_UPLOAD, uploadSourceImageFunction);
    }

    // GENERIC ASSEMBLERS

    static <T> VariableUIAssembler<T> assembleTextbox(
            final ResourceCode labelCode, final Function<String, T> parser,
            final Function<Textbox.Builder, Textbox.Builder> instructions
    ) {
        return (row, variable, db) -> {
            final SimpleLabel label = leadLabel(labelCode);
            final DialogElement labelDE = leadLabelForDialog(db, label,
                    deb -> deb.setRow(row)),
                    textbox = forDialog(buildTextbox(
                            label, variable, parser, instructions)),
                    feedback = feedbackAfterInteraction(textbox, variable);

            return new DialogElement[] { labelDE, textbox, feedback };
        };
    }

    static <T> VariableUIAssembler<T> assembleButton(
            final ResourceCode labelCode, final ResourceCode buttonTextCode,
            final Runnable behaviour
    ) {
        return (row, variable, db) -> {
            final SimpleLabel label = leadLabel(labelCode);
            final DialogElement labelDE = leadLabelForDialog(db, label,
                    deb -> deb.setRow(row)),
                    button = forDialog(
                            SimpleTextButton.init(buttonTextCode,
                                    label.followTB(), behaviour).build()),
                    feedback = feedbackAfterInteraction(button, variable);

            return new DialogElement[] { labelDE, button, feedback };
        };
    }

    static VariableUIAssembler<Boolean> assembleCheckbox(
            final ResourceCode labelCode
    ) {
        return (row, variable, db) -> {
            final SimpleLabel label = leadLabel(labelCode);
            final DialogElement labelDE = leadLabelForDialog(db, label,
                    deb -> deb.setRow(row)),
                    checkbox = forDialog(Checkbox.init(label.followIcon())
                            .setDialogVariableEndpoint(variable)
                            .build());

            return new DialogElement[] { labelDE, checkbox };
        };
    }

    // HELPER FUNCTIONS

    private static <T> Textbox buildTextbox(
            final SimpleLabel label,
            final DialogVariable<T> variable, final Function<String, T> parser,
            final Function<Textbox.Builder, Textbox.Builder> instructions
    ) {
        final Textbox.Builder tbb = Textbox.init(label.followTB())
                .setDialogVariableEndpoint(variable, parser);
        return instructions.apply(tbb).build();

    }
}
