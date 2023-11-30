package com.jordanbunke.rene.settings;

import com.jordanbunke.clink.Clink;
import com.jordanbunke.delta_time.image.GameImage;
import com.jordanbunke.delta_time.io.GameImageIO;
import com.jordanbunke.rene.painter.Painter;

import java.nio.file.Path;

public class CommandParser {
    public static boolean parse(final String command, final Painter painter) {
        final Settings s = painter.getSettings();

        final String PRESET = "preset ",
                LOAD = "load ", SET = "set ",
                STATS_TICK = "stats_tick",
                SAVE_TICK = "save_tick",
                BOX_TICK = "box_tick",
                PALETTE = "palette",
                SAMPLE_PROB = "sample_prob",
                TICK_MODE = "tick_mode",
                BOX_MODE = "box_mode",
                BOX_DIVISIONS = "box_divisions",
                ADHD = "adhd",
                INSTINCT = "instinct",
                MICRODETAIL = "microdetail",
                SWEEP = "sweep",
                WHOLE = "whole";

        switch (command) {
            case "quit" -> {
                return true;
            }
            case "pause" -> s.deactivate();
            case "resume" -> s.activate();
            case "help" -> Clink.writeUpdate("Valid commands..." + Clink.NEW_LINE +
                    PRESET + "[preset_id]" + Clink.NEW_LINE +
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
                            STATS_TICK + " : " + Clink.highlight(String.valueOf(
                                    s.getStatsTick()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            SAVE_TICK + " : " + Clink.highlight(String.valueOf(
                                    s.getSaveTick()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            BOX_TICK + " : " + Clink.highlight(String.valueOf(
                                    s.getFocusBox().getBoxTick()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            PALETTE + " : " + Clink.highlight(s.getPalette().ordinal() +
                            " - " + s.getPalette().name(), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            SAMPLE_PROB + " : " + Clink.highlight(String.valueOf(
                                    s.getSampleProb()), Clink.Mode.UPDATE) + Clink.NEW_LINE +
                            TICK_MODE + " : " + Clink.highlight(
                            s.getFocusBox().getTickMode().name(), Clink.Mode.UPDATE) + Clink.NEW_LINE +
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
                        case TICK_MODE -> s.getFocusBox().setTickMode(FocusBox.TickMode.valueOf(value.toUpperCase()));
                        case SAVE_TICK -> s.setSaveTick(Integer.parseInt(value));
                        case STATS_TICK -> s.setStatsTick(Integer.parseInt(value));
                        case BOX_TICK -> s.getFocusBox().setBoxTick(Integer.parseInt(value));
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
                } else if (command.startsWith(PRESET)) {
                    final String presetID = command.substring(PRESET.length());

                    switch (presetID) {
                        case WHOLE -> {
                            s.getFocusBox().setBoxTick(1000);
                            s.getFocusBox().setMode(FocusBox.Mode.FREE);
                            s.getFocusBox().setTickMode(FocusBox.TickMode.STROKE);
                            s.getFocusBox().setDivisions(1);
                            s.activate();
                        }
                        case ADHD -> {
                            s.getFocusBox().setBoxTick(25);
                            s.getFocusBox().setMode(FocusBox.Mode.RANDOM);
                            s.getFocusBox().setTickMode(FocusBox.TickMode.ATTEMPT);
                            s.getFocusBox().setDivisions(15);
                            s.activate();
                        }
                        case SWEEP -> {
                            s.getFocusBox().setBoxTick(100);
                            s.getFocusBox().setMode(FocusBox.Mode.ITERATE);
                            s.getFocusBox().setTickMode(FocusBox.TickMode.ATTEMPT);
                            s.getFocusBox().setDivisions(8);
                            s.activate();
                        }
                        case INSTINCT -> {
                            s.getFocusBox().setBoxTick(100);
                            s.getFocusBox().setMode(FocusBox.Mode.WORST);
                            s.getFocusBox().setTickMode(FocusBox.TickMode.ATTEMPT);
                            s.getFocusBox().setDivisions(12);
                            s.activate();
                        }
                        case MICRODETAIL -> {
                            s.setSampleProb(1d);
                            s.getFocusBox().setBoxTick(2000);
                            s.getFocusBox().setMode(FocusBox.Mode.WORST);
                            s.getFocusBox().setTickMode(FocusBox.TickMode.ATTEMPT);
                            s.getFocusBox().setDivisions(40);
                            s.activate();
                        }
                        default -> Clink.writeError("Preset ID " +
                                Clink.highlight(presetID, Clink.Mode.ERROR) +
                                " was not recognized");
                    }
                }
            }
        }

        return false;
    }
}
