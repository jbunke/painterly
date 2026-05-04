package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.menu.elements.label.DynamicLabel;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.menu.elements.textbox.Textbox;
import com.jordanbunke.painterly.resources.ResourceCode;

import static com.jordanbunke.painterly.util.Colors.*;
import static com.jordanbunke.painterly.util.Colors.SystemColor.*;
import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogAssembly {
    // TODO
    public static PopUpDialog newProject() {
        final PopUpDialog.Builder db = PopUpDialog.init(RC_NEW_PROJECT)
                .setHeightFromContents();

        // lead label menu elements
        final SimpleLabel
                projectNameLabel = leadLabel(RC_NPD_PROJECT_NAME),
                folderLabel = leadLabel(RC_NPD_FOLDER),
                refImageLabel = leadLabel(RC_NPD_REFERENCE_IMAGE),
                scaleFactorLabel = leadLabel(RC_NPD_SCALE_FACTOR);

        // dialog realization
        final DialogElement
                projectNameLabelDE = leadLabelForDialog(db, projectNameLabel),
                folderLabelDE = leadLabelForDialog(db, folderLabel,
                        deb -> deb.setRow(2.5)),
                refImageLabelDE = leadLabelForDialog(db, refImageLabel,
                        deb -> deb.setRow(5)),
                scaleFactorLabelDE = leadLabelForDialog(db, scaleFactorLabel,
                        deb -> deb.setRow(7.5));

        db.addElements(projectNameLabelDE, folderLabelDE,
                refImageLabelDE, scaleFactorLabelDE);

        // dependent dialog elements, directly realized
        final DialogElement
                projectNameTextbox = forDialog(
                        Textbox.init(projectNameLabel.followTB())
                                .setDialogVariableEndpoint(
                                        NewProject.get().name, s -> s)
                                .build()),
                projectNameFeedback = feedbackUnderLeadLabel(
                        projectNameLabelDE, NewProject.get().name),
                folderButton = forDialog(
                        SimpleTextButton.init(RC_NPD_CHOOSE_FOLDER,
                                folderLabel.followTB(),
                                () -> {} /* TODO */).build()),
                folderFeedback = feedbackUnderLeadLabel(
                        folderLabelDE, NewProject.get().folder),
                uploadImageButton = forDialog(
                        SimpleTextButton.init(RC_UPLOAD,
                                refImageLabel.followTB(),
                                () -> {} /* TODO */).build()),
                uploadImageFeedback = feedbackUnderLeadLabel(
                        refImageLabelDE, NewProject.get().ref),
                scaleFactorTextbox = forDialog(
                        Textbox.init(scaleFactorLabel.followTB())
                                .setDialogVariableEndpoint(
                                        NewProject.get().scaleFactor,
                                        Validator::nullableParseInt)
                                .setWidthRelative(0.4)
                                .build()),
                scaleFactorFeedback = feedbackUnderLeadLabel(
                        scaleFactorLabelDE, NewProject.get().scaleFactor);

        db.addElements(projectNameTextbox, projectNameFeedback,
                folderButton, folderFeedback,
                uploadImageButton, uploadImageFeedback,
                scaleFactorTextbox, scaleFactorFeedback);

        // TODO

        return db.setPrecondition(NewProject.get()::validate)
                .setOnOK(() -> {} /* TODO */).build();
    }

    // helper

    private static DialogElement leadLabelForDialog(
            final PopUpDialog.Builder db, final MenuElement label,
            final DEBInstruction... instructions
    ) {
        final DialogElement.Builder deb = DialogElement.init(label);

        for (DEBInstruction instruction : instructions)
            instruction.transform(deb);

        return deb.autoAlignX(db).autoAlignY(db).build();
    }

    private static DialogElement feedbackUnderLeadLabel(
            final DialogElement leadLabelDE, final DialogVariable<?> variable
    ) {
        return forDialog(
                DynamicLabel.init(leadLabelDE.below(DIALOG_MARGIN),
                                variable::feedback)
                        .setColor(systemColor(MID))
                        .build());
    }

    private static DialogElement forDialog(final MenuElement element) {
        return DialogElement.init(element).build();
    }

    private static SimpleLabel leadLabel(final ResourceCode code) {
        return SimpleLabel.init(code, new Coord2D()).build();
    }

    private static SimpleLabel litLeadLabel(final String text) {
        return SimpleLabel.initLiteral(text, new Coord2D()).build();
    }
}
