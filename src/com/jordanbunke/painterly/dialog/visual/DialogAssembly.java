package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.menus.*;
import com.jordanbunke.painterly.menu.elements.icon_button.Checkbox;
import com.jordanbunke.painterly.menu.elements.icon_button.FeedbackElement;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.menu.elements.text_button.SimpleTextButton;
import com.jordanbunke.painterly.menu.elements.textbox.Textbox;
import com.jordanbunke.painterly.resources.ResourceCode;
import com.jordanbunke.painterly.util.Constants;

import java.util.List;
import java.util.stream.IntStream;

import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogAssembly {
    // TODO

    public static PopUpDialog updateChannelStatus() {
        final UpdateChannelStatus ucs = UpdateChannelStatus.get();
        final PopUpDialog.Builder db = PopUpDialog.init(RC_CHANNEL_UPDATE_STATUS)
                .setSizeFromContents();

        final List<DialogVariable<Boolean>> variables = ucs.getVariables();
        final int l = variables.size();

        // lead label menu elements
        final SimpleLabel[] labels = variables.stream()
                .map(v -> leadLabel(
                        ucs.logChannelForVariable(v).channelCode))
                .toArray(SimpleLabel[]::new);

        // dialog realization
        final DialogElement[] labelDEs = IntStream.range(0, l)
                .mapToObj(i -> leadLabelForDialog(db, labels[i],
                        deb -> deb.setRow(i)))
                .toArray(DialogElement[]::new);

        db.addElements(labelDEs);

        final DialogElement[] checkboxes = IntStream.range(0, l)
                .mapToObj(i -> forDialog(
                        Checkbox.init(labels[i].followIcon())
                                .setDialogVariableEndpoint(variables.get(i))
                                .build())).toArray(DialogElement[]::new);

        db.addElements(checkboxes);

        return buildDialogForVariableSet(db, ucs);
    }

    public static PopUpDialog editProjectSettings() {
        return projectVariables(EditProjectSettings.get(),
                RC_EDIT_PROJECT_SETTINGS);
    }

    public static PopUpDialog saveAs() {
        return projectVariables(SaveAs.get(), RC_SAVE_AS);
    }

    private static PopUpDialog projectVariables(
            final ProjectVariables varSet, final ResourceCode titleCode
    ) {
        final PopUpDialog.Builder db = PopUpDialog.init(titleCode)
                .setHeightFromContents();

        // lead label menu elements
        final SimpleLabel
                projectNameLabel = leadLabel(RC_NPD_PROJECT_NAME),
                folderLabel = leadLabel(RC_NPD_FOLDER),
                autosaveLabel = leadLabel(RC_NPD_AUTOSAVE),
                autosaveFrequencyLabel = leadLabel(RC_NPD_AUTOSAVE_FREQUENCY);

        // dialog realization
        final DialogElement
                projectNameLabelDE = leadLabelForDialog(db, projectNameLabel),
                folderLabelDE = leadLabelForDialog(db,
                        folderLabel,
                        deb -> deb.setRow(1)),
                autosaveLabelDE = leadLabelForDialog(db,
                        autosaveLabel,
                        deb -> deb.setRow(2)),
                autosaveFrequencyLabelDE = leadLabelForDialog(db,
                        autosaveFrequencyLabel,
                        deb -> deb.setRow(3));

        db.addElements(projectNameLabelDE, folderLabelDE,
                autosaveLabelDE, autosaveFrequencyLabelDE);

        final DialogElement
                projectNameTextbox = forDialog(
                Textbox.init(projectNameLabel.followTB())
                        .setDialogVariableEndpoint(varSet.name, s -> s)
                        .build()),
                projectNameFeedback = feedbackAfterInteraction(
                        projectNameTextbox, varSet.name),
                folderButton = forDialog(
                        SimpleTextButton.init(RC_NPD_CHOOSE_FOLDER,
                                folderLabel.followTB(),
                                varSet::chooseFolder).build()),
                folderFeedback = feedbackAfterInteraction(
                        folderButton, varSet.folder),
                autosaveCheckbox = forDialog(
                        Checkbox.init(autosaveLabel.followIcon())
                                .setDialogVariableEndpoint(varSet.autosave)
                                .build()),
                autosaveFrequencyTextbox = forDialog(
                        Textbox.init(autosaveFrequencyLabel.followTB())
                                .setDialogVariableEndpoint(varSet.autosaveFrequency,
                                        Validator::nullableParseInt)
                                .setPrefix(RC_NPD_AUTOSAVE_FREQUENCY_PREFIX)
                                .setSuffix(RC_NPD_AUTOSAVE_FREQUENCY_SUFFIX)
                                .setWidthRelative(1.5)
                                .setMaxLength(String.valueOf(Constants.MAX_AUTOSAVE_FREQUENCY).length())
                                .build()),
                autosaveFrequencyFeedback = feedbackAfterInteraction(
                        autosaveFrequencyTextbox, varSet.autosaveFrequency);

        db.addElements(projectNameTextbox, projectNameFeedback,
                folderButton, folderFeedback,
                autosaveCheckbox,
                autosaveFrequencyTextbox, autosaveFrequencyFeedback);

        return buildDialogForVariableSet(db, varSet);
    }

    public static PopUpDialog newProject() {
        final NewProject np = NewProject.get();
        final PopUpDialog.Builder db = PopUpDialog.init(RC_NEW_PROJECT)
                .setHeightFromContents();

        // lead label menu elements
        final SimpleLabel
                projectNameLabel = leadLabel(RC_NPD_PROJECT_NAME),
                folderLabel = leadLabel(RC_NPD_FOLDER),
                refImageLabel = leadLabel(RC_NPD_SOURCE_IMAGE),
                scaleFactorLabel = leadLabel(RC_NPD_SCALE_FACTOR),
                autosaveLabel = leadLabel(RC_NPD_AUTOSAVE),
                autosaveFrequencyLabel = leadLabel(RC_NPD_AUTOSAVE_FREQUENCY);

        // dialog realization
        final DialogElement
                projectNameLabelDE = leadLabelForDialog(db, projectNameLabel),
                folderLabelDE = leadLabelForDialog(db,
                        folderLabel,
                        deb -> deb.setRow(1)),
                refImageLabelDE = leadLabelForDialog(db,
                        refImageLabel,
                        deb -> deb.setRow(2)),
                scaleFactorLabelDE = leadLabelForDialog(db,
                        scaleFactorLabel,
                        deb -> deb.setRow(3)),
                autosaveLabelDE = leadLabelForDialog(db,
                        autosaveLabel,
                        deb -> deb.setRow(4)),
                autosaveFrequencyLabelDE = leadLabelForDialog(db,
                        autosaveFrequencyLabel,
                        deb -> deb.setRow(5));

        db.addElements(projectNameLabelDE, folderLabelDE,
                refImageLabelDE, scaleFactorLabelDE,
                autosaveLabelDE, autosaveFrequencyLabelDE);

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
                        scaleFactorTextbox, np.scaleFactor),
                autosaveCheckbox = forDialog(
                        Checkbox.init(autosaveLabel.followIcon())
                                .setDialogVariableEndpoint(np.autosave)
                                .build()),
                autosaveFrequencyTextbox = forDialog(
                        Textbox.init(autosaveFrequencyLabel.followTB())
                                .setDialogVariableEndpoint(np.autosaveFrequency,
                                        Validator::nullableParseInt)
                                .setPrefix(RC_NPD_AUTOSAVE_FREQUENCY_PREFIX)
                                .setSuffix(RC_NPD_AUTOSAVE_FREQUENCY_SUFFIX)
                                .setWidthRelative(1.5)
                                .setMaxLength(String.valueOf(Constants.MAX_AUTOSAVE_FREQUENCY).length())
                                .build()),
                autosaveFrequencyFeedback = feedbackAfterInteraction(
                        autosaveFrequencyTextbox, np.autosaveFrequency);

        db.addElements(projectNameTextbox, projectNameFeedback,
                folderButton, folderFeedback,
                uploadImageButton, uploadImageFeedback,
                scaleFactorTextbox, scaleFactorFeedback,
                autosaveCheckbox,
                autosaveFrequencyTextbox, autosaveFrequencyFeedback);

        return buildDialogForVariableSet(db, np);
    }

    public static PopUpDialog errorMessages(
            final ResourceCode titleCode, final ResourceCode... errorCodes
    ) {
        final PopUpDialog.Builder db = PopUpDialog.init(titleCode)
                .setSizeFromContents()
                .setAsOnlyInformation();

        final DialogElement[] labelDEs =
                IntStream.range(0, errorCodes.length).mapToObj(i -> {
                    final ResourceCode errorCode = errorCodes[i];
                    final SimpleLabel label = leadLabel(errorCode);
                    return leadLabelForDialog(db, label,
                            deb -> deb.setRow(i));
                }).toArray(DialogElement[]::new);

        db.addElements(labelDEs);

        return db.build();
    }

    // ARE YOU SUREs

    public static PopUpDialog aysCloseProject(final int index) {
        return areYouSure(RC_AYS_CLOSE_PROJECT_TITLE,
                RC_AYS_CLOSE_PROJECT_MESSAGE,
                () -> ProjectManager.get().closeProject(index));
    }

    public static PopUpDialog aysDeleteActiveBounds(final Project p) {
        return areYouSure(RC_AYS_DELETE_ACTIVE_BOUNDS_TITLE,
                RC_AYS_DELETE_ACTIVE_BOUNDS_MESSAGE,
                p.canvas::deleteActiveBounds);
    }

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
