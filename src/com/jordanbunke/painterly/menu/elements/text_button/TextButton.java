package com.jordanbunke.painterly.menu.elements.text_button;

import com.jordanbunke.painterly.util.Layout;

public interface TextButton {
    String getLabel();
    int getWidth();
    int getHeight();
    Alignment getAlignment();
    ButtonType getButtonType();
    boolean isSelected();
    boolean isHighlighted();

    static TextButton of(final String label, final int width) {
        return of(label, width, Layout.TEXT_BUTTON_DEF_HEIGHT, Alignment.CENTER, ButtonType.STANDARD);
    }

    static TextButton of(
            final String label, final int width, final int height,
            final Alignment alignment, final ButtonType type
    ) {
        return new TextButton() {
            @Override
            public String getLabel() {
                return label;
            }

            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public Alignment getAlignment() {
                return alignment;
            }

            @Override
            public ButtonType getButtonType() {
                return type;
            }

            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public boolean isHighlighted() {
                return false;
            }
        };
    }

    default TextButton sim(
            final boolean selected, final boolean highlighted
    ) {
        final TextButton ref = this;

        return new TextButton() {
            @Override
            public String getLabel() {
                return ref.getLabel();
            }

            @Override
            public int getWidth() {
                return ref.getWidth();
            }

            @Override
            public int getHeight() {
                return ref.getHeight();
            }

            @Override
            public Alignment getAlignment() {
                return ref.getAlignment();
            }

            @Override
            public ButtonType getButtonType() {
                return ref.getButtonType();
            }

            @Override
            public boolean isSelected() {
                return selected;
            }

            @Override
            public boolean isHighlighted() {
                return highlighted;
            }
        };
    }
}
