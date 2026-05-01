package com.jordanbunke.painterly.menu.dialog;

import com.jordanbunke.delta_time.utility.math.Coord2D;
import com.jordanbunke.painterly.menu.elements.label.SimpleLabel;

import static com.jordanbunke.painterly.resources.ResourceCode.*;

public final class DialogAssembly {
    // TODO
    public static PopUpDialog newProject() {
        final PopUpDialog.Builder db = PopUpDialog.init(RC_NEW_PROJECT)
                .setAsOnlyInformation().setWidthFromContents();

        final DialogElement projectNameLabel = DialogElement.init(
                "project-name-label",
                SimpleLabel.init(new Coord2D(), RC_PROJECT_NAME).build())
                .autoAlignX(db)
                .build(),
                projectNameTextbox = DialogElement.init(
                        "project-label-textbox", SimpleLabel.initLiteral(
                                projectNameLabel.rightOf(10), "[Textbox here]")
                                        .build())
                        .build();
        db.addElements(projectNameLabel, projectNameTextbox);
        // TODO

        return db.build();
    }
}
