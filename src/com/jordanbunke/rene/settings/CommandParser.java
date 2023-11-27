package com.jordanbunke.rene.settings;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.rene.painter.Painter;

import java.nio.file.Path;

public class CommandParser {
    public static boolean parse(final String command, final Painter painter) {
        final Settings s = painter.getSettings();

        final String LOAD = "load ", SET = "set ",
                STROKES_PER_STATS = "strokes_per_stats",
                STROKES_PER_SAVE = "strokes_per_save",
                STROKES_PER_BOX_UPDATE = "strokes_per_box_update",
                PALETTE = "palette",
                SAMPLE_PROB = "sample_prob",
                BOX_MODE = "box_mode",
                BOX_DIVISIONS = "box_divisions";

        switch (command) {
            case "quit" -> {
                return true;
            }
            case "pause" -> s.deactivate();
            case "resume" -> s.activate();
            case "help" -> Clink.writeUpdate("Valid commands..." + Clink.NEW_LINE +
                    "help" + Clink.NEW_LINE +
                    LOAD + "[filepath]" + Clink.NEW_LINE +
                    "pause" + Clink.NEW_LINE +
                    "quit" + Clink.NEW_LINE +
                    "resume" + Clink.NEW_LINE +
                    "save" + Clink.NEW_LINE +
                    SET + "[setting_id] [value]" + Clink.NEW_LINE +
                    "settings" + Clink.NEW_LINE +
                    "stats");
            case "save" -> painter.savePainting();
            case "settings" -> Clink.writeUpdate(
                    "Settings..." + Clink.NEW_LINE +
                            STROKES_PER_STATS + " : " + Clink.highlight(String.valueOf(
                                    s.getStrokesToCalculateSimilarity()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            STROKES_PER_SAVE + " : " + Clink.highlight(String.valueOf(
                                    s.getStrokesToSavePainting()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            STROKES_PER_BOX_UPDATE + " : " + Clink.highlight(String.valueOf(
                                    s.getFocusBox().getStrokesPerUpdate()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            PALETTE + " : " + Clink.highlight(s.getPalette().ordinal() +
                            " - " + s.getPalette().name(), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            SAMPLE_PROB + " : " + Clink.highlight(String.valueOf(
                                    s.getSampleProb()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            BOX_MODE + " : " + Clink.highlight(
                                    s.getFocusBox().getMode().name(), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            BOX_DIVISIONS + " : " + Clink.highlight(String.valueOf(
                                    s.getFocusBox().getDivisions()), Clink.Mode.UPDATE));
            case "stats" -> painter.calculateStats();
            default -> {
                if (command.startsWith(SET)) {
                    final String subcommand = command.substring(SET.length());
                    final String[] settingAndVal = subcommand.split(" ");

                    if (settingAndVal.length != 2) {
                        Clink.writeError("Syntax for setting command should be: " +
                                Clink.highlight(SET + "[setting_id] [value]", Clink.Mode.ERROR));
                        return false;
                    }

                    final String setting = settingAndVal[0], value = settingAndVal[1];

                    switch (setting) {
                        case SAMPLE_PROB -> s.setSampleProb(Double.parseDouble(value));
                        case PALETTE -> s.setPalette(Integer.parseInt(value));
                        case BOX_DIVISIONS -> s.getFocusBox().setDivisions(Integer.parseInt(value));
                        case BOX_MODE -> s.getFocusBox().setMode(FocusBox.Mode.valueOf(value.toUpperCase()));
                        case STROKES_PER_SAVE -> s.setStrokesToSavePainting(Integer.parseInt(value));
                        case STROKES_PER_STATS -> s.setStrokesToCalculateSimilarity(Integer.parseInt(value));
                        case STROKES_PER_BOX_UPDATE -> s.getFocusBox().setStrokesPerUpdate(Integer.parseInt(value));
                        default -> Clink.writeError("Setting ID " +
                                Clink.highlight(setting, Clink.Mode.ERROR) +
                                " was not recognized");
                    }
                } else if (command.startsWith(LOAD)) {
                    final String filepath = command.substring(LOAD.length());

                    final GameImage painting = GameImageIO.readImage(Path.of(filepath));
                    painter.overridePainting(painting);

                    Clink.writeUpdate("Wrote image at " +
                            Clink.highlight(filepath, Clink.Mode.UPDATE) +
                            " into program as in-progress painting");
                }
            }
        }

        return false;
    }
}
