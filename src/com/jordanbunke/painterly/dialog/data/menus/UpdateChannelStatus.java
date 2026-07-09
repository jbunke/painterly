package com.jordanbunke.painterly.dialog.data.menus;

import com.jordanbunke.painterly.dialog.data.DialogVariable;
import com.jordanbunke.painterly.dialog.data.Validator;
import com.jordanbunke.painterly.dialog.data.VariableUIAssembler;
import com.jordanbunke.painterly.util.EnumUtils;
import com.jordanbunke.painterly.util.debug.LogChannel;
import com.jordanbunke.painterly.util.debug.LogManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jordanbunke.painterly.resources.ResourceCode.RC_NA;

public final class UpdateChannelStatus extends DialogVariableSet {
    private static final UpdateChannelStatus INSTANCE;

    private final List<DialogVariable<Boolean>> variables;
    private final Map<DialogVariable<Boolean>, LogChannel> channelMap;

    static {
        INSTANCE = new UpdateChannelStatus();
    }

    private UpdateChannelStatus() {
        channelMap = new HashMap<>();
        variables = EnumUtils.stream(LogChannel.class)
                .filter(c -> c.channelCode != RC_NA)
                .map(this::toVariable).toList();
    }

    public static UpdateChannelStatus get() {
        return INSTANCE;
    }

    public List<DialogVariable<Boolean>> getVariables() {
        return variables;
    }

    @Override
    public DialogVariable<?>[] getAllVariables() {
        return variables.toArray(DialogVariable[]::new);
    }

    @Override
    void whenReady() {
        for (DialogVariable<Boolean> variable : variables)
            if (channelMap.containsKey(variable))
                LogManager.setChannelStatus(
                        channelMap.get(variable), variable.get());
    }

    private DialogVariable<Boolean> toVariable(final LogChannel c) {
        final DialogVariable<Boolean> variable = new DialogVariable<>(
                () -> LogManager.isChannelActive(c),
                Validator::always,
                VariableUIAssembler.assembleCheckbox(c.channelCode));
        channelMap.put(variable, c);
        return variable;
    }
}
