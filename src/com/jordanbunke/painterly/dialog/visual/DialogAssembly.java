package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.menus.DialogVariableSet;
import com.jordanbunke.painterly.dialog.data.menus.NewProject;
import com.jordanbunke.painterly.menu.elements.icon_button.FeedbackElement;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.menu.elements.textbox.Textbox;
import com.jordanbunke.painterly.resources.ResourceCode;

import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogAssembly {
    // TODO
    public static PopUpDialog newProject() {
        final NewProject np = NewProject.get();
        final PopUpDialog.Builder db = PopUpDialog.init(RC_NEW_PROJECT)
                .setHeightFromContents();

        // lead label menu elements
        final SimpleLabel
                projectNameLabel = leadLabel(RC_NPD_PROJECT_NAME),
                folderLabel = leadLabel(RC_NPD_FOLDER),
                refImageLabel = leadLabel(RC_NPD_SOURCE_IMAGE),
                scaleFactorLabel = leadLabel(RC_NPD_SCALE_FACTOR);

        // dialog realization
        final DialogElement
                projectNameLabelDE = leadLabelForDialog(db, projectNameLabel),
                folderLabelDE = leadLabelForDialog(db, folderLabel,
                        deb -> deb.setRow(1.5)),
                refImageLabelDE = leadLabelForDialog(db, refImageLabel,
                        deb -> deb.setRow(3)),
                scaleFactorLabelDE = leadLabelForDialog(db, scaleFactorLabel,
                        deb -> deb.setRow(4.5));

        db.addElements(projectNameLabelDE, folderLabelDE,
                refImageLabelDE, scaleFactorLabelDE);

        // dependent dialog elements, directly realized
        final DialogElement
                projectNameTextbox = forDialog(
                        Textbox.init(projectNameLabel.followTB())
                                .setDialogVariableEndpoint(np.name, s -> s)
                                .build()),
                projectNameFeedback = feedbackAfterInteraction(
                        projectNameTextbox, np.name),
                folderButton = forDialog(
                        SimpleTextButton.init(RC_NPD_CHOOSE_FOLDER,
                                folderLabel.followTB(),
                                np::chooseFolder).build()),
                folderFeedback = feedbackAfterInteraction(
                        folderButton, np.folder),
                uploadImageButton = forDialog(
                        SimpleTextButton.init(RC_UPLOAD,
                                refImageLabel.followTB(),
                                np::uploadSourceImage).build()),
                uploadImageFeedback = feedbackAfterInteraction(
                        uploadImageButton, np.sourceImage),
                scaleFactorTextbox = forDialog(
                        Textbox.init(scaleFactorLabel.followTB())
                                .setDialogVariableEndpoint(np.scaleFactor,
                                        Validator::nullableParseDouble)
                                .setWidthRelative(0.4)
                                .build()),
                scaleFactorFeedback = feedbackAfterInteraction(
                        scaleFactorTextbox, np.scaleFactor);

        db.addElements(projectNameTextbox, projectNameFeedback,
                folderButton, folderFeedback,
                uploadImageButton, uploadImageFeedback,
                scaleFactorTextbox, scaleFactorFeedback);

        return buildDialogForVariableSet(db, np);
    }

    // ARE YOU SUREs

    public static PopUpDialog aysQuit() {
        return areYouSure(RC_AYS_QUIT_TITLE, RC_AYS_QUIT_MESSAGE,
                Painterly::quitProgram);
    }

    // template
    private static PopUpDialog areYouSure(
            final ResourceCode titleCode, final ResourceCode messageCode,
            final Runnable onOk
            /* TODO - add ResourceCode for overriding OK text */
    ) {
        final PopUpDialog.Builder db = PopUpDialog.init(titleCode)
                .setHeightFromContents()
                .setWidthAsScreenPercentage(0.4);

        final SimpleLabel messageLabel = SimpleLabel
                .init(messageCode, new Coord2D())
                .setAnchor(MenuElement.Anchor.CENTRAL_TOP)
                .build();
        final DialogElement message = leadLabelForDialog(db, messageLabel,
                deb -> deb.centerInColumnX(db));

        return db.addElement(message).setOnOK(onOk).build();
    }

    // helper

    private static PopUpDialog buildDialogForVariableSet(
            final PopUpDialog.Builder db, final DialogVariableSet vars
    ) {
        return db.setPrecondition(vars::validate)
                .setOnOK(vars::ok).build();
    }

    private static DialogElement leadLabelForDialog(
            final PopUpDialog.Builder db, final MenuElement label,
            final DEBInstruction... instructions
    ) {
        final DialogElement.Builder deb = DialogElement.init(label);

        for (DEBInstruction instruction : instructions)
            instruction.transform(deb);

        return deb.autoAlignX(db).autoAlignY(db).build();
    }

    private static DialogElement feedbackAfterInteraction(
            final DialogElement interactionDE, final DialogVariable<?> variable
    ) {
        final int adjustmentY =
                ((interactionDE.element.getHeight() - ICON_DIM) / 2);

        return forDialog(FeedbackElement.init(
                interactionDE.rightOf(DIALOG_MARGIN).displaceY(adjustmentY),
                variable).build());
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
