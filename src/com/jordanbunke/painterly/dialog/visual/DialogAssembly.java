package com.jordanbunke.painterly.dialog.visual;

import com.jordanbunke.delta_time.menu.menu_elements.MenuElement;
import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.Painterly;
import com.jordanbunke.painterly.core.Project;
import com.jordanbunke.painterly.core.ProjectManager;
import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.menus.*;
import com.jordanbunke.painterly.menu.elements.icon_button.FeedbackElement;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;
import com.jordanbunke.painterly.resources.ResourceCode;

import java.util.stream.IntStream;

import static com.jordanbunke.painterly.util.Layout.*;
import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogAssembly {
    // TODO

    public static PopUpDialog editProgramSettings() {
        final EditProgramSettings eps = EditProgramSettings.get();
        final PopUpDialog.Builder db = PopUpDialog
                .init(RC_EDIT_PROGRAM_SETTINGS)
                .setSizeFromContents();

        assembleDialogForVariableSet(db, eps);

        return buildDialogForVariableSet(db, eps);
    }

    public static PopUpDialog focusAreaAsFocusBox() {
        final FocusAreaAsFocusBox ff = FocusAreaAsFocusBox.get();
        final PopUpDialog.Builder db = PopUpDialog
                .init(RC_FOCUS_AREA_AS_FOCUS_BOX)
                .setHeightFromContents();

        assembleDialogForVariableSet(db, ff);

        return buildDialogForVariableSet(db, ff);
    }

    public static PopUpDialog updateChannelStatus() {
        final UpdateChannelStatus ucs = UpdateChannelStatus.get();
        final PopUpDialog.Builder db = PopUpDialog.init(RC_CHANNEL_UPDATE_STATUS)
                .setSizeFromContents();

        assembleDialogForVariableSet(db, ucs);

        return buildDialogForVariableSet(db, ucs);
    }

    public static PopUpDialog duplicateProject() {
        return projectVariables(DuplicateProject.get(), RC_DUPLICATE_PROJECT);
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

        assembleDialogForVariableSet(db, varSet);

        return buildDialogForVariableSet(db, varSet);
    }

    public static PopUpDialog newProject() {
        final NewProject np = NewProject.get();
        final PopUpDialog.Builder db = PopUpDialog.init(RC_NEW_PROJECT)
                .setHeightFromContents();

        assembleDialogForVariableSet(db, np);

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
                .setSizeFromContents();

        final SimpleLabel messageLabel = SimpleLabel
                .init(messageCode, new Coord2D())
                .build();
        final DialogElement message = leadLabelForDialog(db, messageLabel,
                deb -> deb.centerInColumnX(db));

        return db.addElement(message).setOnOK(onOk).build();
    }

    // HELPER FUNCTIONS

    private static void assembleDialogForVariableSet(
            final PopUpDialog.Builder db, final DialogVariableSet vars
    ) {
        final DialogVariable<?>[] variables = vars.getAllVariables();

        IntStream.range(0, variables.length)
                .forEach(i -> db.addElements(variables[i].assemble(i, db)));
    }

    private static PopUpDialog buildDialogForVariableSet(
            final PopUpDialog.Builder db, final DialogVariableSet vars
    ) {
        return db.setPrecondition(vars::validate)
                .setOnOK(vars::ok).build();
    }

    public static DialogElement leadLabelForDialog(
            final PopUpDialog.Builder db, final MenuElement label,
            final DEBInstruction... instructions
    ) {
        final DialogElement.Builder deb = DialogElement.init(label);

        for (DEBInstruction instruction : instructions)
            instruction.transform(deb);

        return deb.autoAlignX(db).autoAlignY(db).build();
    }

    public static DialogElement feedbackAfterInteraction(
            final DialogElement interactionDE, final DialogVariable<?> variable
    ) {
        final int adjustmentY =
                ((interactionDE.element.getHeight() - ICON_DIM) / 2);

        return forDialog(FeedbackElement.init(
                interactionDE.rightOf(DIALOG_MARGIN).displaceY(adjustmentY),
                variable).build());
    }

    public static DialogElement forDialog(final MenuElement element) {
        return DialogElement.init(element).build();
    }

    public static SimpleLabel leadLabel(final ResourceCode code) {
        return SimpleLabel.init(code, new Coord2D()).build();
    }

    private static SimpleLabel litLeadLabel(final String text) {
        return SimpleLabel.initLiteral(text, new Coord2D()).build();
    }
}
