package com.jordanbunke.painterly.dialog.visual;

@FunctionalInterface
public interface DEBInstruction {
    DialogElement.Builder transform(final DialogElement.Builder deb);
}
